package nl.rhaydus.nestbox.core.auth.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import nl.rhaydus.nestbox.core.auth.data.model.AccessTokenResponse
import nl.rhaydus.nestbox.core.auth.data.model.DeviceCodeResponse
import nl.rhaydus.nestbox.core.auth.data.model.GitHubUserResponse

interface GitHubAuthRemoteDataSource {
    suspend fun requestDeviceCode(clientId: String, scope: String): DeviceCodeResponse

    suspend fun requestAccessToken(clientId: String, deviceCode: String): AccessTokenResponse

    suspend fun fetchUser(token: String): GitHubUserResponse
}

// The `Accept: application/json` header on each call is required — without it GitHub returns
// form-encoded bodies that the JSON deserializer can't parse.
class GitHubAuthRemoteDataSourceImpl(private val client: HttpClient) : GitHubAuthRemoteDataSource {

    override suspend fun requestDeviceCode(clientId: String, scope: String): DeviceCodeResponse =
        client.post("https://github.com/login/device/code") {
            header(HttpHeaders.Accept, "application/json")
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("client_id", clientId)
                        append("scope", scope)
                    },
                ),
            )
        }.body()

    override suspend fun requestAccessToken(clientId: String, deviceCode: String): AccessTokenResponse =
        client.post("https://github.com/login/oauth/access_token") {
            header(HttpHeaders.Accept, "application/json")
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("client_id", clientId)
                        append("device_code", deviceCode)
                        append("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
                    },
                ),
            )
        }.body()

    override suspend fun fetchUser(token: String): GitHubUserResponse =
        client.get("https://api.github.com/user") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.Accept, "application/vnd.github+json")
        }.body()
}
