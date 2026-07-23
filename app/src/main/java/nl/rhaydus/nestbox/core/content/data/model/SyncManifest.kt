package nl.rhaydus.nestbox.core.content.data.model

import kotlinx.serialization.Serializable

/**
 * What the last successful sync produced: the commit it came from, and the blob sha of every stored
 * markdown file keyed by its repository-relative path.
 *
 * [headSha] short-circuits the common case (nothing changed since last time) in a single request.
 * [blobShas] narrows the uncommon case to the handful of files that actually differ.
 */
@Serializable
data class SyncManifest(
    val headSha: String,
    val blobShas: Map<String, String>,
)
