package nl.rhaydus.nestbox.core.content.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import nl.rhaydus.nestbox.core.auth.data.datasource.TokenLocalDataSource
import nl.rhaydus.nestbox.core.auth.domain.model.UnauthorizedException
import nl.rhaydus.nestbox.core.content.data.model.CommitResponse
import nl.rhaydus.nestbox.core.content.data.model.RepositoryFile
import nl.rhaydus.nestbox.core.content.data.model.TreeResponse

interface PublicationRemoteDataSource {
    /** The commit the branch currently points at: one small request that answers "anything new?". */
    suspend fun fetchHeadSha(): String

    /** Every markdown file in the repository with the blob sha of its current content. */
    suspend fun fetchMarkdownFiles(): List<RepositoryFile>

    /** One file's markdown, addressed by content rather than by path. */
    suspend fun fetchMarkdown(blobSha: String): String
}

/**
 * Reads the publications out of a private GitHub repository, markdown only.
 *
 * The repository carries far more than prose (assets outweigh the markdown several times over), so
 * this deliberately never touches the repository archive: the tree lists what exists, and blobs are
 * fetched individually. Addressing content by blob sha rather than by path also sidesteps escaping
 * every path segment, and pairs with the stored manifest to skip files that have not changed.
 *
 * The token is a subscriber's own personal access token rather than an OAuth app token: the
 * `doveletter` organisation disallows OAuth apps, so an app-minted token cannot see the repository
 * at all. Any token that authenticates a member works here, whatever minted it.
 */
internal class PublicationRemoteDataSourceImpl(
    private val client: HttpClient,
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val repository: String,
    private val branch: String,
) : PublicationRemoteDataSource {
    override suspend fun fetchHeadSha(): String {
        val response = client.get("$API_BASE/repos/${requireRepository()}/commits") {
            githubHeaders(requireToken())
            parameter(
                "sha",
                branch,
            )
            parameter(
                "per_page",
                1,
            )
        }

        response.requireSuccess(
            attempt = "read the branch head",
            missingMeansNoAccess = true,
        )

        val commits: List<CommitResponse> = response.body()

        return commits.firstOrNull()?.sha
            ?: error("Branch '$branch' of $repository has no commits.")
    }

    override suspend fun fetchMarkdownFiles(): List<RepositoryFile> {
        val response = client.get("$API_BASE/repos/${requireRepository()}/git/trees/$branch") {
            githubHeaders(requireToken())
            parameter(
                "recursive",
                1,
            )
        }

        response.requireSuccess(
            attempt = "list the repository tree",
            missingMeansNoAccess = true,
        )

        val tree: TreeResponse = response.body()

        // A truncated tree would silently hide publications, which reads as "these do not exist"
        // rather than as a failure. Better to fail than to render an archive with holes in it.
        if (tree.truncated) error("The repository tree came back truncated; it is too large to list in one call.")

        return tree.tree
            .filter { entry -> entry.type == BLOB_TYPE && isMarkdown(entry.path) }
            .map { entry ->
                RepositoryFile(
                    path = entry.path,
                    blobSha = entry.sha,
                )
            }
    }

    private fun isMarkdown(path: String): Boolean = path.endsWith(
        MARKDOWN_EXTENSION,
        ignoreCase = true,
    )

    override suspend fun fetchMarkdown(blobSha: String): String {
        val response = client.get("$API_BASE/repos/${requireRepository()}/git/blobs/$blobSha") {
            githubHeaders(requireToken())
            header(
                HttpHeaders.Accept,
                RAW_MEDIA_TYPE,
            )
        }

        response.requireSuccess("download blob $blobSha")

        return response.bodyAsText()
    }

    private fun HttpRequestBuilder.githubHeaders(token: String) {
        header(
            HttpHeaders.Authorization,
            "Bearer $token",
        )
        header(
            HttpHeaders.Accept,
            GITHUB_MEDIA_TYPE,
        )
        header(
            API_VERSION_HEADER,
            API_VERSION,
        )
    }

    private suspend fun requireToken(): String = tokenLocalDataSource.getToken()
        ?: error("No GitHub token stored. Link an account, or set DOVELETTER_TOKEN in local.properties.")

    private fun requireRepository(): String {
        if (repository.isBlank()) {
            error("DOVELETTER_REPOSITORY is not set in local.properties; expected \"owner/name\".")
        }

        return repository
    }

    /**
     * [missingMeansNoAccess] marks the repository-level calls, where a 404 is GitHub declining to
     * confirm the repository exists to someone who cannot read it, and so means access is gone. It
     * stays false for a single blob, where a 404 is a content anomaly rather than a lost credential,
     * and must not be grounds for deleting everything already synced.
     */
    private suspend fun HttpResponse.requireSuccess(
        attempt: String,
        missingMeansNoAccess: Boolean = false,
    ) {
        if (status.isSuccess()) return

        // The body carries GitHub's own explanation ("Bad credentials", "Not Found"), which is the
        // difference between a wrong token and a token the organisation refuses to honour.
        val detail = "Could not $attempt: HTTP $status. ${bodyAsText().take(BODY_EXCERPT_LENGTH)}"

        val hasLostAccess = status == HttpStatusCode.Unauthorized ||
            status == HttpStatusCode.Forbidden ||
            (missingMeansNoAccess && status == HttpStatusCode.NotFound)

        if (hasLostAccess) throw UnauthorizedException(detail)

        error(detail)
    }

    private companion object {
        const val API_BASE = "https://api.github.com"
        const val GITHUB_MEDIA_TYPE = "application/vnd.github+json"
        const val RAW_MEDIA_TYPE = "application/vnd.github.raw"
        const val API_VERSION_HEADER = "X-GitHub-Api-Version"
        const val API_VERSION = "2022-11-28"
        const val BLOB_TYPE = "blob"
        const val MARKDOWN_EXTENSION = ".md"
        const val BODY_EXCERPT_LENGTH = 200
    }
}
