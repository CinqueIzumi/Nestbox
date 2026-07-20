package nl.rhaydus.nestbox.core.content.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import nl.rhaydus.nestbox.core.content.data.datasource.PublicationRemoteDataSource
import nl.rhaydus.nestbox.core.content.data.mapper.PublicationMarkdownMapper
import nl.rhaydus.nestbox.core.content.domain.model.Publication
import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.nestbox.core.content.domain.model.PublicationType
import nl.rhaydus.nestbox.core.content.domain.repository.PublicationRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal class PublicationRepositoryImpl(
    private val remoteDataSource: PublicationRemoteDataSource,
    private val mapper: PublicationMarkdownMapper,
    private val ioDispatcher: CoroutineDispatcher,
) : PublicationRepository {
    override suspend fun getPublications(): List<PublicationSummary> = withContext(ioDispatcher) {
        val markdown = remoteDataSource.fetchPublicationMarkdown(NEWEST_ID)
        val preview = mapper.extractPreview(markdown)

        mockArchive(markdown).mapIndexed { index, (type, number, date) ->
            PublicationSummary(
                id = index.toString(),
                type = type,
                number = number,
                date = date,
                previewText = preview,
            )
        }
    }

    override suspend fun getPublication(id: String): Publication = withContext(ioDispatcher) {
        val markdown = remoteDataSource.fetchPublicationMarkdown(id)
        val archive = mockArchive(markdown)
        val index = id.toIntOrNull()?.takeIf { it in archive.indices } ?: 0
        val (type, number, date) = archive[index]

        Publication(
            id = id,
            type = type,
            number = number,
            date = date,
            markdown = mapper.stripHeader(markdown),
        )
    }

    // Mock fan-out: until the private-repo listing exists, synthesise a small archive from the one
    // sample. The newest entry's type is parsed from the sample header; the rest are varied so all
    // three publication types show. Every entry resolves to the same content when opened.
    private fun mockArchive(markdown: String): List<Triple<PublicationType, Int?, String>> {
        val header = mapper.parseHeader(markdown)
        val latestDate = header?.second ?: DEFAULT_DATE
        val newestType =
            header?.let { PublicationType.fromMarker(it.third) } ?: PublicationType.WEEKLY_LETTER

        val shape = listOf(
            newestType to 5,
            PublicationType.ARTICLE to null,
            PublicationType.WEEKLY_LETTER to 4,
            PublicationType.INTERVIEW to null,
            PublicationType.WEEKLY_LETTER to 3,
        )

        return shape.mapIndexed { index, (type, number) ->
            Triple(
                type,
                number,
                shiftDate(
                    latestDate,
                    weeksBack = index,
                ),
            )
        }
    }

    // Calendar/SimpleDateFormat rather than java.time, which would need core-library desugaring below API 26.
    private fun shiftDate(
        date: String,
        weeksBack: Int,
    ): String {
        val format = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US,
        )
        val parsed = runCatching { format.parse(date) }.getOrNull() ?: return date

        val calendar = Calendar.getInstance().apply {
            time = parsed
            add(
                Calendar.DAY_OF_YEAR,
                -DAYS_PER_WEEK * weeksBack,
            )
        }

        return format.format(calendar.time)
    }

    private companion object {
        const val NEWEST_ID = "0"
        const val DAYS_PER_WEEK = 7
        const val DEFAULT_DATE = "2026-06-12"
    }
}
