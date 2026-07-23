package nl.rhaydus.nestbox.core.content.data.model

/**
 * One markdown file in the repository. [blobSha] identifies the *content*, so it changes whenever
 * the file is edited and stays put when it is not: that is what makes an incremental sync possible.
 */
data class RepositoryFile(
    val path: String,
    val blobSha: String,
)
