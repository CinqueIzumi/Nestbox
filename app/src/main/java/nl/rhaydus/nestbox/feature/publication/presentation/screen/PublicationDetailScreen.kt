package nl.rhaydus.nestbox.feature.publication.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import nl.rhaydus.nestbox.core.content.domain.model.PublicationType
import nl.rhaydus.nestbox.core.presentation.markdown.MarkdownDocument
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography
import nl.rhaydus.nestbox.feature.publication.presentation.action.OpenLinkAction
import nl.rhaydus.nestbox.feature.publication.presentation.action.PublicationDetailAction
import nl.rhaydus.nestbox.feature.publication.presentation.event.PublicationDetailEvent
import nl.rhaydus.nestbox.feature.publication.presentation.screenmodel.PublicationDetailScreenModel
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailUiState
import org.koin.core.parameter.parametersOf

data class PublicationDetailScreen(val publicationId: String) : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<PublicationDetailScreenModel> { parametersOf(publicationId) }
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val uriHandler = LocalUriHandler.current

        LaunchedEffect(screenModel) {
            screenModel.events.collect { event ->
                when (event) {
                    is PublicationDetailEvent.OpenLinkEvent -> uriHandler.openUri(event.url)
                }
            }
        }

        PublicationDetailScreen(
            state = state,
            onBack = navigator::pop,
            runAction = screenModel::runAction,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun PublicationDetailScreen(
        state: PublicationDetailUiState,
        onBack: () -> Unit,
        runAction: (PublicationDetailAction) -> Unit,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val type = state.type
                        val titleText = when {
                            type == null -> "Reading"
                            state.number != null -> "${type.label} #${state.number}"
                            else -> type.label
                        }

                        Text(text = titleText)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    // RootScreen's Scaffold already consumes the status-bar inset for every screen, so
                    // the bar must not re-apply it or the title sits a second inset too low.
                    windowInsets = WindowInsets(
                        0,
                        0,
                        0,
                        0,
                    ),
                )
            },
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                when {
                    state.isLoading -> LoadingState()

                    state.errorMessage != null -> ErrorState(message = state.errorMessage)

                    else -> ReadingView(
                        state = state,
                        runAction = runAction,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun ReadingView(
        state: PublicationDetailUiState,
        runAction: (PublicationDetailAction) -> Unit,
    ) {
        val scrollState = rememberScrollState()

        Column(modifier = Modifier.fillMaxSize()) {
            LinearWavyProgressIndicator(
                progress = {
                    if (scrollState.maxValue > 0) {
                        scrollState.value.toFloat() / scrollState.maxValue
                    } else {
                        0f
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                PublicationHeader(
                    type = state.type,
                    number = state.number,
                    date = state.date,
                )

                Spacer(modifier = Modifier.height(28.dp))

                MarkdownDocument(
                    blocks = state.blocks,
                    onLinkClick = { url -> runAction(OpenLinkAction(url)) },
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    @Composable
    private fun PublicationHeader(
        type: PublicationType?,
        number: Int?,
        date: String?,
    ) {
        val typeLabel = type?.label?.uppercase()
        val kicker = when {
            typeLabel == null -> ""
            number != null -> "$typeLabel #$number"
            else -> typeLabel
        }

        Column {
            if (kicker.isNotEmpty()) {
                Text(
                    text = kicker,
                    style = MaterialTheme.readerTypography.kicker,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "The Doveletter",
                style = MaterialTheme.readerTypography.headline,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (date != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = date,
                    style = MaterialTheme.readerTypography.meta,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun LoadingState() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(24.dp),
        ) {
            CircularWavyProgressIndicator(modifier = Modifier.size(24.dp))

            Text(
                text = "Opening the publication…",
                style = MaterialTheme.readerTypography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    @Composable
    private fun ErrorState(message: String) {
        Text(
            text = message,
            style = MaterialTheme.readerTypography.body,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(24.dp),
        )
    }
}
