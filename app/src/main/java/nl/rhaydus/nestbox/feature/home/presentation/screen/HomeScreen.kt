package nl.rhaydus.nestbox.feature.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import nl.rhaydus.designsystem.layout.rememberBottomBarPadding
import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography
import nl.rhaydus.nestbox.core.presentation.widget.PublicationCard
import nl.rhaydus.nestbox.core.presentation.widget.SectionHeader
import nl.rhaydus.nestbox.feature.home.presentation.action.HomeAction
import nl.rhaydus.nestbox.feature.home.presentation.action.OpenPublicationAction
import nl.rhaydus.nestbox.feature.home.presentation.event.HomeEvent
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeScreenModel
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState
import nl.rhaydus.nestbox.feature.publication.presentation.screen.PublicationDetailScreen

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(screenModel) {
            screenModel.events.collect { event ->
                when (event) {
                    // The tab's LocalNavigator only holds Tabs; push onto the parent (root) navigator so
                    // the reading view opens full-screen over the bottom bar.
                    is HomeEvent.OpenPublicationEvent ->
                        navigator.parent?.push(PublicationDetailScreen(event.publicationId))
                }
            }
        }

        HomeScreen(
            state = state,
            runAction = screenModel::runAction,
        )
    }

    @Composable
    internal fun HomeScreen(
        state: HomeUiState,
        runAction: (HomeAction) -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "THE DOVELETTER",
                style = MaterialTheme.readerTypography.kicker,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                state.isLoading -> LoadingState()

                state.errorMessage != null -> ErrorState(message = state.errorMessage)

                else -> PublicationArchive(
                    publications = state.publications,
                    runAction = runAction,
                )
            }

            Spacer(modifier = Modifier.height(rememberBottomBarPadding()))
        }
    }

    @Composable
    private fun PublicationArchive(
        publications: List<PublicationSummary>,
        runAction: (HomeAction) -> Unit,
    ) {
        val latest = publications.firstOrNull() ?: return
        val archive = publications.drop(1)

        LatestPublicationHero(
            publication = latest,
            onOpen = { runAction(OpenPublicationAction(latest.id)) },
        )

        Spacer(modifier = Modifier.height(40.dp))

        SectionHeader(
            kicker = "Archive",
            headline = "Past publications",
        )

        Spacer(modifier = Modifier.height(20.dp))

        archive.forEachIndexed { index, publication ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(12.dp))
            }

            PublicationCard(
                publication = publication,
                onClick = { runAction(OpenPublicationAction(publication.id)) },
            )
        }
    }

    @Composable
    private fun LatestPublicationHero(
        publication: PublicationSummary,
        onOpen: () -> Unit,
    ) {
        val typeLabel = publication.type.label.uppercase()
        val kicker = publication.number?.let { "$typeLabel #$it" } ?: typeLabel

        Column {
            Text(
                text = kicker,
                style = MaterialTheme.readerTypography.kicker,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The Doveletter",
                style = MaterialTheme.readerTypography.headline,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = publication.date,
                style = MaterialTheme.readerTypography.meta,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = publication.previewText,
                style = MaterialTheme.readerTypography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = onOpen) {
                Text(text = "Read publication")
            }
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun LoadingState() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularWavyProgressIndicator(modifier = Modifier.size(24.dp))

            Text(
                text = "Loading the latest publications…",
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
        )
    }
}
