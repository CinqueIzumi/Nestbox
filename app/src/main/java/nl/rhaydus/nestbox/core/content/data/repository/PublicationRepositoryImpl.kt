package nl.rhaydus.nestbox.core.content.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import nl.rhaydus.common.runCatchingCancellable
import nl.rhaydus.nestbox.core.content.data.datasource.PublicationLocalDataSource
import nl.rhaydus.nestbox.core.content.data.datasource.PublicationRemoteDataSource
import nl.rhaydus.nestbox.core.content.data.mapper.PublicationMarkdownMapper
import nl.rhaydus.nestbox.core.content.data.model.PublicationHeader
import nl.rhaydus.nestbox.core.content.data.model.RepositoryFile
import nl.rhaydus.nestbox.core.content.data.model.SyncManifest
import nl.rhaydus.nestbox.core.content.domain.model.Publication
import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.nestbox.core.content.domain.model.PublicationType
import nl.rhaydus.nestbox.core.content.domain.repository.PublicationRepository

/**
 * Publications come from a private GitHub repository, synced incrementally: the stored manifest is
 * compared against the repository tree and only files whose content actually changed are fetched.
 *
 * The steady state is therefore one small request per open, and a week's worth of new publications
 * costs a handful of small ones. A publication's id is its repository-relative path
 * (`2026/2026-06-12-weekly.md`), stable across syncs and readable in a log.
 */
internal class PublicationRepositoryImpl(
    private val remoteDataSource: PublicationRemoteDataSource,
    private val localDataSource: PublicationLocalDataSource,
    private val mapper: PublicationMarkdownMapper,
    private val ioDispatcher: CoroutineDispatcher,
) : PublicationRepository {
    private val syncMutex = Mutex()

    override suspend fun getPublications(): List<PublicationSummary> = withContext(ioDispatcher) {
        sync()

        localDataSource.listPaths()
            .filter { path -> isPublication(path) }
            .map { path -> summaryOf(path) }
            .sortedWith(
                compareByDescending<PublicationSummary> { summary -> summary.date }
                    .thenByDescending { summary -> summary.id },
            )
    }

    override suspend fun getPublication(id: String): Publication = withContext(ioDispatcher) {
        sync()

        val markdown = localDataSource.readMarkdown(id)
        val header = mapper.parseHeader(markdown)

        Publication(
            id = id,
            type = typeOf(
                header = header,
                path = id,
            ),
            number = header?.number,
            date = dateOf(
                header = header,
                markdown = markdown,
                path = id,
            ),
            markdown = mapper.stripMetadata(markdown),
        )
    }

    // Behind the same lock as the sync, so content cannot be deleted while a sync is midway through
    // writing it and leave orphaned files the manifest no longer accounts for.
    override suspend fun clearLocalContent() = withContext(ioDispatcher) {
        syncMutex.withLock { localDataSource.clear() }
    }

    private suspend fun sync() = syncMutex.withLock {
        val manifest = localDataSource.readManifest()

        // Content already on disk keeps the reader usable while offline, so a failed refresh is a
        // deliberate no-op; with nothing synced yet the failure is all the caller can act on.
        val headSha = runCatchingCancellable { remoteDataSource.fetchHeadSha() }
            .onFailure { error -> if (manifest == null) throw error }
            .getOrNull()
            ?: return@withLock

        if (headSha == manifest?.headSha) return@withLock

        // Filtered before the downloads rather than after: translations and repository furniture are
        // nearly half the markdown in the repository, and none of it is ever read.
        val files = remoteDataSource.fetchMarkdownFiles().filter { file -> isPublication(file.path) }
        val storedShas = manifest?.blobShas.orEmpty()

        val removed = storedShas.keys - files.map { file -> file.path }.toSet()

        removed.forEach { path -> localDataSource.delete(path) }

        download(files.filter { file -> storedShas[file.path] != file.blobSha })

        // Written last and only on full success, so an interrupted sync re-fetches rather than
        // recording content it never stored.
        localDataSource.writeManifest(
            SyncManifest(
                headSha = headSha,
                blobShas = files.associate { file -> file.path to file.blobSha },
            ),
        )
    }

    private suspend fun download(files: List<RepositoryFile>) = coroutineScope {
        val inFlight = Semaphore(MAX_CONCURRENT_DOWNLOADS)

        files
            .map { file ->
                async {
                    inFlight.withPermit {
                        localDataSource.write(
                            path = file.path,
                            markdown = remoteDataSource.fetchMarkdown(file.blobSha),
                        )
                    }
                }
            }
            .awaitAll()
    }

    private suspend fun summaryOf(path: String): PublicationSummary {
        val markdown = localDataSource.readMarkdown(path)
        val header = mapper.parseHeader(markdown)

        return PublicationSummary(
            id = path,
            type = typeOf(
                header = header,
                path = path,
            ),
            number = header?.number,
            date = dateOf(
                header = header,
                markdown = markdown,
                path = path,
            ),
            previewText = mapper.extractPreview(markdown),
        )
    }

    // Not every markdown file in the repository is a publication: each directory carries a README,
    // the root holds the template a new weekly letter is written from, `ko` holds Korean
    // translations of publications already listed in English, and `.github` holds repository config.
    private fun isPublication(path: String): Boolean {
        val segments = path.split('/')

        val name = segments.last().lowercase()

        if (NON_PUBLICATION_FILES.contains(name)) return false

        if (segments.contains(TRANSLATION_DIRECTORY)) return false

        return segments.first().startsWith('.').not()
    }

    // A weekly letter states its type in its header line; articles and interviews carry no header,
    // so their top-level directory is what identifies them.
    private fun typeOf(
        header: PublicationHeader?,
        path: String,
    ): PublicationType {
        if (header != null) return PublicationType.fromMarker(header.marker)

        return when (path.substringBefore('/')) {
            ARTICLE_DIRECTORY -> PublicationType.ARTICLE
            INTERVIEW_DIRECTORY -> PublicationType.INTERVIEW
            else -> PublicationType.WEEKLY_LETTER
        }
    }

    private fun dateOf(
        header: PublicationHeader?,
        markdown: String,
        path: String,
    ): String = header?.date
        ?: mapper.parseFrontmatterDate(markdown)
        ?: DATE_IN_PATH_REGEX.find(path)?.value.orEmpty()

    private companion object {
        const val MAX_CONCURRENT_DOWNLOADS = 8
        const val ARTICLE_DIRECTORY = "article"
        const val INTERVIEW_DIRECTORY = "interview"
        const val TRANSLATION_DIRECTORY = "ko"
        val NON_PUBLICATION_FILES = setOf(
            "readme.md",
            "template.md",
        )
        val DATE_IN_PATH_REGEX = Regex("""\d{4}-\d{2}-\d{2}""")
    }
}
