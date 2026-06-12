package nl.rhaydus.nestbox.feature.profile.presentation.screen

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.coroutines.launch
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography
import nl.rhaydus.nestbox.core.presentation.util.rememberBottomBarPadding
import nl.rhaydus.nestbox.core.presentation.widget.SectionHeader
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ProfileHeader()

            Spacer(modifier = Modifier.height(40.dp))

            SectionHeader(
                kicker = "Connection",
                headline = "GitHub",
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (val link = state.link) {
                is GitHubLinkState.Loading -> LoadingPanel()

                is GitHubLinkState.Disconnected -> DisconnectedPanel(
                    error = link.error,
                    onConnect = { runAction(StartGitHubLinkAction) },
                )

                is GitHubLinkState.Connecting -> ConnectingPanel(
                    link = link,
                    onCheckAgain = { runAction(CheckGitHubAuthorizationAction) },
                    onCancel = { runAction(CancelGitHubLinkAction) },
                )

                is GitHubLinkState.Connected -> ConnectedPanel(
                    link = link,
                    onDisconnect = { runAction(DisconnectGitHubAction) },
                )
            }

            Spacer(modifier = Modifier.height(rememberBottomBarPadding()))
        }
    }

    @Composable
    private fun ProfileHeader() {
        Column {
            Text(
                text = "YOUR ACCOUNT",
                style = MaterialTheme.readerTypography.kicker,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Profile",
                style = MaterialTheme.readerTypography.pageTitle,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }

    @Composable
    private fun SectionPanel(content: @Composable ColumnScope.() -> Unit) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                content = content,
            )
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun LoadingPanel() {
        SectionPanel {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularWavyProgressIndicator(modifier = Modifier.size(24.dp))

                Text(
                    text = "Checking your GitHub connection…",
                    style = MaterialTheme.readerTypography.body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    @Composable
    private fun DisconnectedPanel(
        error: String?,
        onConnect: () -> Unit,
    ) {
        SectionPanel {
            Text(
                text = "Read the Doveletter",
                style = MaterialTheme.readerTypography.articleTitle,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The Doveletter lives in a private repository open to its subscribers. Sign in with the GitHub account that has access to it to load the latest issues.",
                style = MaterialTheme.readerTypography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = error,
                    style = MaterialTheme.readerTypography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onConnect,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Connect GitHub")
            }
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun ConnectingPanel(
        link: GitHubLinkState.Connecting,
        onCheckAgain: () -> Unit,
        onCancel: () -> Unit,
    ) {
        val clipboard = LocalClipboard.current
        val uriHandler = LocalUriHandler.current
        val coroutineScope = rememberCoroutineScope()

        SectionPanel {
            Text(
                text = "ENTER THIS CODE ON GITHUB",
                style = MaterialTheme.readerTypography.kickerSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = link.userCode,
                style = MaterialTheme.readerTypography.statNumber,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Open GitHub, paste the code above, and approve access. We check automatically when you return. Tap Check again if you approved on another device.",
                style = MaterialTheme.readerTypography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularWavyProgressIndicator(modifier = Modifier.size(16.dp))

                    Text(
                        text = "Checking with GitHub…",
                        style = MaterialTheme.readerTypography.meta,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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

    @Composable
    private fun ConnectedPanel(
        link: GitHubLinkState.Connected,
        onDisconnect: () -> Unit,
    ) {
        SectionPanel {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AvatarInitial(login = link.account.login)

                Column {
                    Text(
                        text = "CONNECTED",
                        style = MaterialTheme.readerTypography.kickerSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "@${link.account.login}",
                        style = MaterialTheme.readerTypography.articleTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onDisconnect,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Disconnect")
            }
        }
    }

    @Composable
    private fun AvatarInitial(login: String) {
        val initial = login.firstOrNull()?.uppercase() ?: "?"

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Text(
                text = initial,
                style = MaterialTheme.readerTypography.statNumber.copy(
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
