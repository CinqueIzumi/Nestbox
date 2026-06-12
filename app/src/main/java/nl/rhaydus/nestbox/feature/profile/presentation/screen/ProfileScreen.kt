package nl.rhaydus.nestbox.feature.profile.presentation.screen

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.coroutines.launch
import nl.rhaydus.nestbox.feature.profile.presentation.action.CancelGitHubLinkAction
import nl.rhaydus.nestbox.feature.profile.presentation.action.CheckGitHubAuthorizationAction
import nl.rhaydus.nestbox.feature.profile.presentation.action.DisconnectGitHubAction
import nl.rhaydus.nestbox.feature.profile.presentation.action.ProfileAction
import nl.rhaydus.nestbox.feature.profile.presentation.action.StartGitHubLinkAction
import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileScreenModel
import nl.rhaydus.nestbox.feature.profile.presentation.state.GitHubLinkState
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileUiState

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val state by screenModel.state.collectAsState()

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            screenModel.runAction(CheckGitHubAuthorizationAction)
        }

        ProfileScreen(
            state = state,
            runAction = screenModel::runAction,
        )
    }

    @Composable
    fun ProfileScreen(
        state: ProfileUiState,
        runAction: (ProfileAction) -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
            )

            when (val link = state.link) {
                is GitHubLinkState.Loading -> LoadingCard()

                is GitHubLinkState.Disconnected -> DisconnectedCard(
                    error = link.error,
                    onConnect = { runAction(StartGitHubLinkAction) },
                )

                is GitHubLinkState.Connecting -> ConnectingCard(
                    link = link,
                    onCheckAgain = { runAction(CheckGitHubAuthorizationAction) },
                    onCancel = { runAction(CancelGitHubLinkAction) },
                )

                is GitHubLinkState.Connected -> ConnectedCard(
                    link = link,
                    onDisconnect = { runAction(DisconnectGitHubAction) },
                )
            }
        }
    }

    @Composable
    private fun LoadingCard() {
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))

                Text(text = "Checking GitHub connection…")
            }
        }
    }

    @Composable
    private fun DisconnectedCard(
        error: String?,
        onConnect: () -> Unit,
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Connect GitHub",
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = "Link your GitHub account to view private repositories you have access to.",
                    style = MaterialTheme.typography.bodyMedium,
                )

                if (error != null) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Button(
                    onClick = onConnect,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Connect GitHub")
                }
            }
        }
    }

    @Composable
    private fun ConnectingCard(
        link: GitHubLinkState.Connecting,
        onCheckAgain: () -> Unit,
        onCancel: () -> Unit,
    ) {
        val clipboard = LocalClipboard.current
        val uriHandler = LocalUriHandler.current
        val coroutineScope = rememberCoroutineScope()

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Enter this code on GitHub",
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = link.userCode,
                    style = MaterialTheme.typography.headlineLarge,
                )

                Text(
                    text = "Open GitHub, paste the code above and approve access. We check automatically when you return — or tap Check again if you approved on another device.",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val clip = ClipData.newPlainText("GitHub code", link.userCode)
                            clipboard.setClipEntry(ClipEntry(clip))
                        }

                        uriHandler.openUri(link.verificationUri)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Copy code & open GitHub")
                }

                if (link.isChecking) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))

                        Text(
                            text = "Checking with GitHub…",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                OutlinedButton(
                    onClick = onCheckAgain,
                    enabled = link.isChecking.not(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Check again")
                }

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    }

    @Composable
    private fun ConnectedCard(
        link: GitHubLinkState.Connected,
        onDisconnect: () -> Unit,
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                    )

                    Column {
                        Text(
                            text = "Connected to GitHub",
                            style = MaterialTheme.typography.labelMedium,
                        )

                        Text(
                            text = link.account.login,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                OutlinedButton(
                    onClick = onDisconnect,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Disconnect")
                }
            }
        }
    }
}
