package nl.rhaydus.nestbox.core.content.data.datasource

import android.content.Context

interface PublicationRemoteDataSource {
    suspend fun fetchPublicationMarkdown(publicationId: String): String
}

// Stub for the real GitHub fetch: every id resolves to the one bundled sample publication. A Ktor/GitHub
// source swaps in via a one-line DI change once the private-repo integration exists.
internal class PublicationRemoteDataSourceImpl(private val context: Context) : PublicationRemoteDataSource {
    override suspend fun fetchPublicationMarkdown(publicationId: String): String =
        context.assets.open(SAMPLE_ISSUE_ASSET).bufferedReader().use { reader -> reader.readText() }

    private companion object {
        const val SAMPLE_ISSUE_ASSET = "sample-publication.md"
    }
}
