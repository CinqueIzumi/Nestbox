package nl.rhaydus.nestbox.core.content.data.datasource

import android.content.Context
import kotlinx.serialization.json.Json
import nl.rhaydus.nestbox.core.content.data.model.SyncManifest
import java.io.File

interface PublicationLocalDataSource {
    /** What the last successful sync produced, or null when nothing is stored yet. */
    suspend fun readManifest(): SyncManifest?

    suspend fun writeManifest(manifest: SyncManifest)

    suspend fun write(
        path: String,
        markdown: String,
    )

    suspend fun delete(path: String)

    /** Repository-relative paths of every stored markdown file, e.g. `2026/2026-06-12-weekly.md`. */
    suspend fun listPaths(): List<String>

    suspend fun readMarkdown(path: String): String

    /** Removes every synced publication and the manifest with it. */
    suspend fun clear()
}

/**
 * The synced markdown on app-private storage. This is the reader's actual source: the content exists
 * nowhere outside the private repository, so once synced it has to stay readable offline.
 *
 * Paths mirror the repository's own layout, which lets a path serve as a publication id without a
 * separate index to keep in step.
 */
internal class PublicationLocalDataSourceImpl(context: Context) : PublicationLocalDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    private val root = File(
        context.filesDir,
        PUBLICATIONS_DIRECTORY,
    )

    private val manifestFile = File(
        root,
        MANIFEST_FILE,
    )

    override suspend fun readManifest(): SyncManifest? {
        if (manifestFile.isFile.not()) return null

        // A manifest from an older schema is not worth migrating: dropping it re-syncs everything.
        return runCatching { json.decodeFromString<SyncManifest>(manifestFile.readText()) }.getOrNull()
    }

    override suspend fun writeManifest(manifest: SyncManifest) {
        root.mkdirs()
        manifestFile.writeText(
            json.encodeToString(
                SyncManifest.serializer(),
                manifest,
            ),
        )
    }

    override suspend fun write(
        path: String,
        markdown: String,
    ) {
        val target = resolve(path)

        target.parentFile?.mkdirs()
        target.writeText(markdown)
    }

    override suspend fun delete(path: String) {
        resolve(path).delete()
    }

    override suspend fun listPaths(): List<String> {
        if (root.isDirectory.not()) return emptyList()

        return root.walkTopDown()
            .filter { file -> file.isFile && isMarkdown(file.name) }
            .map { file -> relativePathOf(file) }
            .toList()
    }

    override suspend fun readMarkdown(path: String): String = resolve(path).readText()

    override suspend fun clear() {
        root.deleteRecursively()
    }

    private fun isMarkdown(path: String): Boolean = path.endsWith(
        MARKDOWN_EXTENSION,
        ignoreCase = true,
    )

    private fun relativePathOf(file: File): String = file.toRelativeString(root).replace(
        File.separatorChar,
        '/',
    )

    // A repository path is server-supplied and could climb out of the destination directory, so
    // every read and write is pinned back to the publications root before it touches the filesystem.
    private fun resolve(path: String): File {
        val target = File(
            root,
            path,
        )

        val isInsideRoot = target.canonicalPath.startsWith(root.canonicalPath + File.separator)

        if (isInsideRoot.not()) error("Publication path '$path' escapes the publications directory.")

        return target
    }

    private companion object {
        const val PUBLICATIONS_DIRECTORY = "publications"
        const val MANIFEST_FILE = "manifest.json"
        const val MARKDOWN_EXTENSION = ".md"
    }
}
