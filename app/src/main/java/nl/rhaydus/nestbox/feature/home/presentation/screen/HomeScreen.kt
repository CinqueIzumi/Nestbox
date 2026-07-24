package nl.rhaydus.nestbox.feature.home.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import nl.rhaydus.designsystem.component.StaggeredEntryCoordinator
import nl.rhaydus.designsystem.component.rememberStaggeredEntryCoordinator
import nl.rhaydus.designsystem.component.staggeredEntry
import nl.rhaydus.designsystem.layout.rememberBottomBarPadding
import nl.rhaydus.designsystem.modifier.pressScaleClickable
import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.nestbox.core.content.domain.model.PublicationType
import nl.rhaydus.nestbox.core.presentation.publicationDisplayTitle
import nl.rhaydus.nestbox.core.presentation.theme.nestboxColors
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography
import nl.rhaydus.nestbox.core.presentation.widget.EmptyState
import nl.rhaydus.nestbox.core.presentation.widget.PillChip
import nl.rhaydus.nestbox.core.presentation.widget.SectionHeader
import nl.rhaydus.nestbox.feature.home.presentation.action.ConnectGithubAction
import nl.rhaydus.nestbox.feature.home.presentation.action.HomeAction
import nl.rhaydus.nestbox.feature.home.presentation.action.OpenPublicationAction
import nl.rhaydus.nestbox.feature.home.presentation.action.SelectPublicationFilterAction
import nl.rhaydus.nestbox.feature.home.presentation.event.HomeEvent
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeScreenModel
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState
import nl.rhaydus.nestbox.feature.home.presentation.state.PublicationFilter
import nl.rhaydus.nestbox.feature.home.presentation.state.PublicationSection
import nl.rhaydus.nestbox.feature.profile.presentation.screen.ProfileTab
import nl.rhaydus.nestbox.feature.publication.presentation.screen.PublicationDetailScreen

private val ledgerMonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
    "MMM",
    Locale.ENGLISH,
)

object HomeScreen : Screen {
    private const val MASTHEAD_KEY = "masthead"
    private const val FACET_STRIP_KEY = "facet_strip"
    private const val LOADING_KEY = "loading"
    private const val ERROR_KEY = "error"
    private const val EMPTY_KEY = "empty"
    private const val HERO_KEY = "hero"
    private const val FOOTER_KEY = "footer"
    private const val WHATS_INSIDE_HEADER_KEY = "whats_inside_header"

    /**
     * Static "what's inside" preview shown in place of the archive on the signed-out (locked-nest)
     * surface (design system §B(alt)). Carries no state of its own — the three publication types are
     * a fixed catalog fact, not something the screen model tracks.
     */
    private data class WhatsInsideItem(
        val code: String,
        val title: String,
        val description: String,
    )

    private val whatsInsideItems: List<WhatsInsideItem> = listOf(
        WhatsInsideItem(
            code = "WKLY",
            title = "Weekly Letters",
            description = "The week's happenings, relevant data and a few deeper cuts, every week.",
        ),
        WhatsInsideItem(
            code = "DEEP",
            title = "Articles",
            description = "Deep dives into one topic at a time, down to the internals.",
        ),
        WhatsInsideItem(
            code = "PREP",
            title = "Interview Prep",
            description = "Focused preparation pieces, in the same spirit as the articles.",
        ),
    )

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current

        LaunchedEffect(screenModel) {
            screenModel.events.collect { event ->
                when (event) {
                    // The tab's LocalNavigator only holds Tabs; push onto the parent (root) navigator so
                    // the reading view opens full-screen over the bottom bar.
                    is HomeEvent.OpenPublicationEvent ->
                        navigator.parent?.push(PublicationDetailScreen(event.publicationId))

                    HomeEvent.NavigateToProfileEvent ->
                        tabNavigator.current = ProfileTab
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
        val entryCoordinator = rememberStaggeredEntryCoordinator(key = this)

        // Lazy rather than a scrolling Column: the archive runs to hundreds of publications, and
        // composing every row up front is what makes the list stutter.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 22.dp,
                top = 22.dp,
                end = 22.dp,
                bottom = rememberBottomBarPadding(),
            ),
        ) {
            item(key = MASTHEAD_KEY) {
                Masthead()
            }

            item(key = FACET_STRIP_KEY) {
                Column {
                    Spacer(modifier = Modifier.height(26.dp))

                    FacetStrip(
                        selectedFilter = state.selectedFilter,
                        onSelect = { filter -> runAction(SelectPublicationFilterAction(filter)) },
                    )

                    Spacer(modifier = Modifier.height(26.dp))
                }
            }

            when {
                state.isLoading -> item(key = LOADING_KEY) { LoadingState() }

                errorMessage != null -> {
                    item(key = ERROR_KEY) {
                        LockedNestState(
                            message = errorMessage,
                            onConnectGithub = { runAction(ConnectGithubAction) },
                        )
                    }

                    whatsInsideList(entryCoordinator = entryCoordinator)

                    item(key = FOOTER_KEY) { ArchiveFooter(text = "SIGN IN TO READ") }
                }

                state.hero != null || state.sections.isNotEmpty() -> {
                    publicationArchive(
                        hero = state.hero,
                        sections = state.sections,
                        runAction = runAction,
                        entryCoordinator = entryCoordinator,
                    )

                    item(key = FOOTER_KEY) { ArchiveFooter() }
                }

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
    private fun Masthead(modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            Text(
                text = "FED BY DOVE LETTER",
                style = MaterialTheme.readerTypography.kicker,
                color = MaterialTheme.colorScheme.outline,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Nestbox",
                style = MaterialTheme.readerTypography.masthead,
                color = MaterialTheme.colorScheme.onSurface,
            )
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
                    label = chipLabel(filter),
                    isSelected = filter == selectedFilter,
                    onClick = { onSelect(filter) },
                )
            }
        }
    }

    /**
     * The hero, when present, is the newest publication matching the active filter and is already
     * excluded from [sections] by the selection logic upstream. Per design system §5 ("The newest
     * publication"), it simply leads the ledger as an ordinary row — there is no dedicated hero-card
     * treatment left in this system.
     */
    private fun LazyListScope.publicationArchive(
        hero: PublicationSummary?,
        sections: List<PublicationSection>,
        runAction: (HomeAction) -> Unit,
        entryCoordinator: StaggeredEntryCoordinator,
    ) {
        var entryIndex = 0

        if (hero != null) {
            item(key = HERO_KEY) {
                LedgerRow(
                    publication = hero,
                    onClick = { runAction(OpenPublicationAction(hero.id)) },
                    modifier = Modifier.staggeredEntry(coordinator = entryCoordinator, index = 0),
                )
            }

            entryIndex = 1
        }

        sections.forEachIndexed { sectionIndex, section ->
            item(key = "section_header_${section.type.name}") {
                LedgerSectionHeader(
                    section = section,
                    hasLeadingGap = hero != null || sectionIndex > 0,
                )
            }

            val sectionStartIndex = entryIndex

            itemsIndexed(
                items = section.publications,
                key = { _, publication -> publication.id },
            ) { rowIndex, publication ->
                LedgerRow(
                    publication = publication,
                    onClick = { runAction(OpenPublicationAction(publication.id)) },
                    modifier = Modifier.staggeredEntry(
                        coordinator = entryCoordinator,
                        index = sectionStartIndex + rowIndex,
                    ),
                )
            }

            entryIndex += section.publications.size
        }
    }

    @Composable
    private fun LedgerSectionHeader(
        section: PublicationSection,
        hasLeadingGap: Boolean,
        modifier: Modifier = Modifier,
    ) {
        Column(modifier = modifier) {
            if (hasLeadingGap) {
                Spacer(modifier = Modifier.height(26.dp))
            }

            SectionHeader(kicker = section.type.sectionKicker)
        }
    }

    @Composable
    private fun LedgerRow(
        publication: PublicationSummary,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .pressScaleClickable(onClick = onClick)
                    .padding(vertical = 15.dp),
            ) {
                LedgerDateColumn(
                    date = publication.date,
                    modifier = Modifier.alignByBaseline(),
                )

                LedgerTitleColumn(
                    publication = publication,
                    modifier = Modifier
                        .weight(1f)
                        .alignByBaseline(),
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }

    @Composable
    private fun LedgerDateColumn(
        date: String,
        modifier: Modifier = Modifier,
    ) {
        val (month, day) = ledgerDateParts(date)

        Column(
            horizontalAlignment = Alignment.End,
            modifier = modifier.width(30.dp),
        ) {
            Text(
                text = month,
                style = MaterialTheme.readerTypography.meta,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.End,
            )

            Text(
                text = day,
                style = MaterialTheme.readerTypography.meta,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.End,
            )
        }
    }

    @Composable
    private fun LedgerTitleColumn(
        publication: PublicationSummary,
        modifier: Modifier = Modifier,
    ) {
        Column(modifier = modifier) {
            Text(
                text = publicationDisplayTitle(publication.title),
                style = MaterialTheme.readerTypography.articleTitle,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = ledgerTypeLabel(publication),
                style = MaterialTheme.readerTypography.meta,
                color = if (publication.type == PublicationType.WEEKLY_LETTER) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
            )
        }
    }

    @Composable
    private fun ArchiveFooter(
        text: String = "— END OF NEST —",
        modifier: Modifier = Modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.readerTypography.kickerSmall,
            color = MaterialTheme.nestboxColors.listFooterMuted,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    top = 18.dp,
                    bottom = 8.dp,
                ),
        )
    }

    /**
     * The signed-out preview of the archive (design system §B(alt)): a "WHAT'S INSIDE" group header
     * followed by one static row per publication type, reusing [LedgerRow]'s anatomy (16dp gap, a
     * fixed-width leading column, a hairline divider) with a type code standing in for the date.
     */
    private fun LazyListScope.whatsInsideList(entryCoordinator: StaggeredEntryCoordinator) {
        item(key = WHATS_INSIDE_HEADER_KEY) {
            Column {
                Spacer(modifier = Modifier.height(26.dp))

                SectionHeader(kicker = "WHAT'S INSIDE")
            }
        }

        itemsIndexed(
            items = whatsInsideItems,
            key = { _, item -> item.code },
        ) { index, item ->
            WhatsInsideRow(
                item = item,
                modifier = Modifier.staggeredEntry(coordinator = entryCoordinator, index = index),
            )
        }
    }

    @Composable
    private fun WhatsInsideRow(
        item: WhatsInsideItem,
        modifier: Modifier = Modifier,
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 15.dp),
            ) {
                Text(
                    text = item.code,
                    style = MaterialTheme.readerTypography.meta,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .width(30.dp)
                        .alignByBaseline(),
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .alignByBaseline(),
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.readerTypography.articleTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = item.description,
                        style = MaterialTheme.readerTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
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

    /**
     * Design system §B(alt) ("Home, signed out"): the ink card, the dashed stamp, the kicker,
     * [message] ([HomeUiState.errorMessage]) as the headline, the "CONNECT GITHUB" call-to-action
     * ([onConnectGithub], dispatching [ConnectGithubAction]) and the read-only caption beneath it.
     */
    @Composable
    private fun LockedNestState(
        message: String,
        onConnectGithub: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val paper = MaterialTheme.colorScheme.inverseOnSurface

        Surface(
            color = MaterialTheme.colorScheme.inverseSurface,
            contentColor = paper,
            shape = RoundedCornerShape(18.dp),
            modifier = modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier.padding(top = 22.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
            ) {
                LockedStamp(modifier = Modifier.align(Alignment.TopEnd))

                Column {
                    Text(
                        text = "THE NEST IS LOCKED",
                        style = MaterialTheme.readerTypography.kickerSmall,
                        color = lerp(
                            MaterialTheme.colorScheme.primary,
                            paper,
                            0.7f,
                        ),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.readerTypography.headlineSmall,
                        color = paper,
                        modifier = Modifier.padding(end = 44.dp),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ConnectGithubButton(onClick = onConnectGithub)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "READ-ONLY · WE NEVER WRITE TO YOUR REPOS",
                        style = MaterialTheme.readerTypography.meta,
                        color = paper.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    /**
     * The locked-nest card's call to action (design system §B(alt)): a full-width paper pill on the
     * ink card, inverting the card's own ink/paper roles so it reads as the one live control. The
     * leading circle re-inverts back to ink so the "GH" glyph keeps the badge treatment used
     * elsewhere (e.g. Profile's connected-account row).
     */
    @Composable
    private fun ConnectGithubButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val ink = MaterialTheme.colorScheme.inverseSurface
        val paper = MaterialTheme.colorScheme.inverseOnSurface

        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(percent = 50),
            color = paper,
            contentColor = ink,
            modifier = modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 20.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(ink),
                ) {
                    Text(
                        text = "GH",
                        style = MaterialTheme.readerTypography.badgeGlyph,
                        color = paper,
                    )
                }

                Text(
                    text = "CONNECT GITHUB",
                    style = MaterialTheme.readerTypography.controlLabel,
                    color = ink,
                )
            }
        }
    }

    @Composable
    private fun LockedStamp(modifier: Modifier = Modifier) {
        val strokeColor = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.35f)

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(
                    width = 34.dp,
                    height = 40.dp,
                )
                .rotate(4f)
                .drawBehind {
                    drawRoundRect(
                        color = strokeColor,
                        cornerRadius = CornerRadius(3.dp.toPx()),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx())),
                        ),
                    )
                },
        ) {
            Text(
                text = "×",
                style = MaterialTheme.readerTypography.headlineSmall,
                color = strokeColor,
            )
        }
    }

    @Composable
    private fun EmptyFacetState(filter: PublicationFilter) {
        Text(
            text = "No ${filter.label.lowercase()} yet.",
            style = MaterialTheme.readerTypography.pullQuote,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    /**
     * The type-facet strip (design system §3.3) uses short chip labels — `ALL` / `LETTERS` /
     * `ARTICLES` / `PREP` — distinct from [PublicationFilter.label]'s full descriptive form used
     * elsewhere (e.g. [EmptyFacetState]). Purely a display mapping; it adds no state.
     */
    private fun chipLabel(filter: PublicationFilter): String = when (filter) {
        PublicationFilter.ALL -> "ALL"
        PublicationFilter.WEEKLY_LETTERS -> "LETTERS"
        PublicationFilter.ARTICLES -> "ARTICLES"
        PublicationFilter.INTERVIEW_PREP -> "PREP"
    }

    private fun ledgerTypeLabel(publication: PublicationSummary): String {
        val label = publication.type.label.uppercase(Locale.ENGLISH)

        if (publication.type != PublicationType.WEEKLY_LETTER) {
            return label
        }

        val number = publication.number ?: return label

        return "$label № $number"
    }

    private fun ledgerDateParts(date: String): Pair<String, String> {
        val parsed = runCatching { LocalDate.parse(date) }.getOrNull() ?: return "" to ""

        return parsed.format(ledgerMonthFormatter).uppercase(Locale.ENGLISH) to parsed.dayOfMonth.toString()
    }
}
