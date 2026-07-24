package nl.rhaydus.nestbox.feature.profile.presentation.screen

import android.content.ClipData
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.coroutines.launch
import nl.rhaydus.designsystem.layout.rememberBottomBarPadding
import nl.rhaydus.designsystem.modifier.pressScaleClickable
import nl.rhaydus.nestbox.BuildConfig
import nl.rhaydus.nestbox.core.auth.domain.model.GitHubAccount
import nl.rhaydus.nestbox.core.presentation.theme.nestboxColors
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography
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
    internal fun ProfileScreen(
        state: ProfileUiState,
        runAction: (ProfileAction) -> Unit,
    ) {
        val link = state.link

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Profile",
                style = MaterialTheme.readerTypography.pageTitle,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (link is GitHubLinkState.Connected) {
                Spacer(modifier = Modifier.height(18.dp))

                IdentityCard(account = link.account)
            }

            Spacer(modifier = Modifier.height(26.dp))

            SectionHeader(kicker = "Source access")

            Spacer(modifier = Modifier.height(8.dp))

            SourceAccessCard {
                when (link) {
                    is GitHubLinkState.Loading -> LoadingRows()

                    is GitHubLinkState.Disconnected -> DisconnectedRows(
                        error = link.error,
                        onConnect = { runAction(StartGitHubLinkAction) },
                    )

                    is GitHubLinkState.Connecting -> ConnectingRows(
                        link = link,
                        onCheckAgain = { runAction(CheckGitHubAuthorizationAction) },
                        onCancel = { runAction(CancelGitHubLinkAction) },
                    )

                    is GitHubLinkState.Connected -> ConnectedRows(account = link.account)
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            ProfileFooter(
                isConnected = link is GitHubLinkState.Connected,
                onSignOut = { runAction(DisconnectGitHubAction) },
            )

            Spacer(modifier = Modifier.height(40.dp))

            Spacer(modifier = Modifier.height(rememberBottomBarPadding()))
        }
    }

    @Composable
    private fun IdentityCard(account: GitHubAccount) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                AvatarInitial(login = account.login)

                Text(
                    text = "@${account.login}",
                    style = MaterialTheme.readerTypography.identityName,
                    color = MaterialTheme.colorScheme.onSurface,
                )
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
                .background(MaterialTheme.colorScheme.inverseSurface),
        ) {
            Text(
                text = initial,
                style = MaterialTheme.readerTypography.avatarInitial,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }
    }

    @Composable
    private fun SourceAccessCard(content: @Composable ColumnScope.() -> Unit) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(content = content)
        }
    }

    @Composable
    private fun SourceAccessRow(content: @Composable ColumnScope.() -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content,
        )
    }

    @Composable
    private fun SourceAccessDivider() {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp,
        )
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun LoadingRows() {
        SourceAccessRow {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularWavyProgressIndicator(modifier = Modifier.size(20.dp))

                Text(
                    text = "Checking your GitHub connection…",
                    style = MaterialTheme.readerTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    @Composable
    private fun DisconnectedRows(
        error: String?,
        onConnect: () -> Unit,
    ) {
        SourceAccessRow {
            Text(
                text = "Not connected",
                style = MaterialTheme.readerTypography.rowTitle,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sign in with the GitHub account that has access to the Doveletter's " +
                    "private repository to load the latest publications.",
                style = MaterialTheme.readerTypography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error,
                    style = MaterialTheme.readerTypography.meta,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        SourceAccessDivider()

        SourceAccessRow {
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
    private fun ConnectingRows(
        link: GitHubLinkState.Connecting,
        onCheckAgain: () -> Unit,
        onCancel: () -> Unit,
    ) {
        val clipboard = LocalClipboard.current
        val uriHandler = LocalUriHandler.current
        val coroutineScope = rememberCoroutineScope()

        SourceAccessRow {
            Text(
                text = "Enter this code on GitHub",
                style = MaterialTheme.readerTypography.kickerSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = link.userCode,
                style = MaterialTheme.readerTypography.statNumber,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Open GitHub, paste the code above, and approve access. We check " +
                    "automatically when you return.",
                style = MaterialTheme.readerTypography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (link.isChecking) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularWavyProgressIndicator(modifier = Modifier.size(16.dp))

                    Text(
                        text = "Checking with GitHub…",
                        style = MaterialTheme.readerTypography.meta,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }

        SourceAccessDivider()

        SourceAccessRow {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val clip = ClipData.newPlainText(
                            "GitHub code",
                            link.userCode,
                        )
                        clipboard.setClipEntry(ClipEntry(clip))
                    }

                    uriHandler.openUri(link.verificationUri)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Copy code & open GitHub")
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
    private fun ConnectedRows(account: GitHubAccount) {
        SourceAccessRow {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                GhBadge()

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GitHub connected",
                        style = MaterialTheme.readerTypography.rowTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "@${account.login}",
                        style = MaterialTheme.readerTypography.identifierSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }

                ActiveDot()
            }
        }

        SourceAccessDivider()

        SourceAccessRow {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = BuildConfig.DOVELETTER_REPOSITORY,
                    style = MaterialTheme.readerTypography.identifier,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "READ ACCESS",
                    style = MaterialTheme.readerTypography.metaStrong,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }

    @Composable
    private fun GhBadge() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.inverseSurface),
        ) {
            Text(
                text = "GH",
                style = MaterialTheme.readerTypography.badgeGlyph,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }
    }

    @Composable
    private fun ActiveDot() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
            )

            Text(
                text = "ACTIVE",
                style = MaterialTheme.readerTypography.metaStrong,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }

    @Composable
    private fun ProfileFooter(
        isConnected: Boolean,
        onSignOut: () -> Unit,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isConnected) {
                Text(
                    text = "SIGN OUT",
                    style = MaterialTheme.readerTypography.controlLabel,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.pressScaleClickable(onClick = onSignOut),
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            Text(
                text = "NESTBOX 1.0 · FED BY DOVE LETTER",
                style = MaterialTheme.readerTypography.meta,
                color = MaterialTheme.nestboxColors.listFooterMuted,
            )
        }
    }
}
