package nl.rhaydus.nestbox.feature.home.presentation.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
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
import nl.rhaydus.nestbox.core.presentation.publicationDisplayTitle
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography
import nl.rhaydus.nestbox.core.presentation.widget.EmptyState
import nl.rhaydus.nestbox.core.presentation.widget.PillChip
import nl.rhaydus.nestbox.core.presentation.widget.PublicationCard
import nl.rhaydus.nestbox.core.presentation.widget.SectionHeader
import nl.rhaydus.nestbox.feature.home.presentation.action.HomeAction
import nl.rhaydus.nestbox.feature.home.presentation.action.OpenPublicationAction
import nl.rhaydus.nestbox.feature.home.presentation.action.SelectPublicationFilterAction
import nl.rhaydus.nestbox.feature.home.presentation.event.HomeEvent
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeScreenModel
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState
import nl.rhaydus.nestbox.feature.home.presentation.state.PublicationFilter
import nl.rhaydus.nestbox.feature.home.presentation.state.PublicationSection
import nl.rhaydus.nestbox.feature.publication.presentation.screen.PublicationDetailScreen

object HomeScreen : Screen {
    private const val MASTHEAD_KEY = "masthead"
    private const val FACET_STRIP_KEY = "facet_strip"
    private const val LOADING_KEY = "loading"
    private const val ERROR_KEY = "error"
    private const val EMPTY_KEY = "empty"
    private const val HERO_KEY = "hero"

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
        val errorMessage = state.errorMessage

        // Lazy rather than a scrolling Column: the archive runs to hundreds of publications, and
        // composing every card up front is what makes the list stutter.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = 16.dp,
                end = 24.dp,
                bottom = rememberBottomBarPadding(),
            ),
        ) {
            item(key = MASTHEAD_KEY) {
                Column {
                    Text(
                        text = "THE DOVELETTER",
                        style = MaterialTheme.readerTypography.kicker,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item(key = FACET_STRIP_KEY) {
                Column {
                    FacetStrip(
                        selectedFilter = state.selectedFilter,
                        onSelect = { filter -> runAction(SelectPublicationFilterAction(filter)) },
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            when {
                state.isLoading -> item(key = LOADING_KEY) { LoadingState() }

                errorMessage != null -> item(key = ERROR_KEY) { ErrorState(message = errorMessage) }

                state.hero != null || state.sections.isNotEmpty() ->
                    publicationArchive(
                        hero = state.hero,
                        sections = state.sections,
                        runAction = runAction,
                    )

                state.selectedFilter == PublicationFilter.ALL ->
                    item(key = EMPTY_KEY) {
                        EmptyState(
                            icon = Icons.Outlined.Inbox,
                            headline = "Nothing has synced from the Doveletter yet.",
                        )
                    }

                else -> item(key = EMPTY_KEY) { EmptyFacetState(filter = state.selectedFilter) }
            }
        }
    }

    @Composable
    private fun FacetStrip(
        selectedFilter: PublicationFilter,
        onSelect: (PublicationFilter) -> Unit,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            PublicationFilter.entries.forEach { filter ->
                PillChip(
                    label = filter.label,
                    isSelected = filter == selectedFilter,
                    onClick = { onSelect(filter) },
                )
            }
        }
    }

    private fun LazyListScope.publicationArchive(
        hero: PublicationSummary?,
        sections: List<PublicationSection>,
        runAction: (HomeAction) -> Unit,
    ) {
        if (hero != null) {
            item(key = HERO_KEY) {
                LatestPublicationHero(
                    publication = hero,
                    onOpen = { runAction(OpenPublicationAction(hero.id)) },
                )
            }
        }

        sections.forEachIndexed { index, section ->
            publicationSection(
                section = section,
                hasLeadingGap = hero != null || index > 0,
                runAction = runAction,
            )
        }
    }

    private fun LazyListScope.publicationSection(
        section: PublicationSection,
        hasLeadingGap: Boolean,
        runAction: (HomeAction) -> Unit,
    ) {
        item(key = "section_header_${section.type.name}") {
            Column {
                if (hasLeadingGap) {
                    Spacer(modifier = Modifier.height(40.dp))
                }

                SectionHeader(
                    kicker = section.type.sectionKicker,
                    headline = section.type.sectionHeadline,
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        itemsIndexed(
            items = section.publications,
            key = { _, publication -> publication.id },
        ) { index, publication ->
            PublicationCard(
                publication = publication,
                onClick = { runAction(OpenPublicationAction(publication.id)) },
                modifier = Modifier.padding(top = if (index > 0) 12.dp else 0.dp),
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
                text = publicationDisplayTitle(publication.title),
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
                Text(text = "Read the ${publication.type.label.lowercase()}")
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

    @Composable
    private fun EmptyFacetState(filter: PublicationFilter) {
        Text(
            text = "No ${filter.label.lowercase()} yet.",
            style = MaterialTheme.readerTypography.pullQuote,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
