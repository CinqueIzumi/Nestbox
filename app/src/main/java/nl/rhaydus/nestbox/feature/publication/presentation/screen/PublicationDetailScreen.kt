package nl.rhaydus.nestbox.feature.publication.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import java.time.LocalDate
import java.time.format.TextStyle as JavaTimeTextStyle
import java.util.Locale
import kotlin.math.roundToInt
import nl.rhaydus.nestbox.core.content.domain.model.PublicationType
import nl.rhaydus.nestbox.core.presentation.markdown.markdownDocument
import nl.rhaydus.nestbox.core.presentation.markdown.markdownPlainText
import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownBlock
import nl.rhaydus.nestbox.core.presentation.publicationDisplayTitle
import nl.rhaydus.nestbox.core.presentation.theme.nestboxColors
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

    @Composable
    internal fun PublicationDetailScreen(
        state: PublicationDetailUiState,
        onBack: () -> Unit,
        runAction: (PublicationDetailAction) -> Unit,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceBright,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                state.isLoading -> LoadingState()

                state.errorMessage != null -> ErrorState(message = state.errorMessage)

                else -> ReadingView(
                    state = state,
                    onBack = onBack,
                    runAction = runAction,
                )
            }
        }
    }

    @Composable
    private fun ReadingView(
        state: PublicationDetailUiState,
        onBack: () -> Unit,
        runAction: (PublicationDetailAction) -> Unit,
    ) {
        val listState = rememberLazyListState()
        val progress by rememberReadingProgress(listState)
        val displayTitle = publicationDisplayTitle(state.title)
        val blocks = remember(state.blocks, displayTitle) {
            dropDuplicateLeadingTitle(
                state.blocks,
                displayTitle,
            )
        }

        // The spec's 72/120 scroll padding is measured from the design frame's own safe area, not the
        // physical screen edge - RootScreen's Scaffold reserves system-bar space with a plain
        // `.padding(it)` rather than `consumeWindowInsets`, so it isn't marked "used" for a descendant
        // pushed screen like this one. Query the live insets here and add them on top so the kicker
        // clears TopChrome's scrim and the footer clears gesture nav.
        val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val navigationBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = 26.dp,
                    end = 26.dp,
                    top = 72.dp + statusBarTop,
                    bottom = 120.dp + navigationBarBottom,
                ),
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    ArticleHead(state = state)
                }

                item {
                    Spacer(modifier = Modifier.height(22.dp))
                }

                markdownDocument(
                    blocks = blocks,
                    onLinkClick = { url -> runAction(OpenLinkAction(url)) },
                )

                item {
                    ReaderFooter()
                }
            }

            TopChrome(
                progressPercent = (progress * 100).roundToInt().coerceIn(
                    0,
                    100,
                ),
                onBack = onBack,
            )

            ScrollProgressBar(progress = progress)
        }
    }

    @Composable
    private fun ArticleHead(state: PublicationDetailUiState) {
        val typeLabel = state.type?.label?.uppercase()
        val kicker = when {
            typeLabel == null -> ""
            state.number != null -> "$typeLabel № ${state.number}"
            else -> typeLabel
        }
        val metaText = listOfNotNull(formatReaderDate(state.date), typeLabel).joinToString(separator = " · ")

        Column {
            if (kicker.isNotEmpty()) {
                Text(
                    text = kicker,
                    style = MaterialTheme.readerTypography.kicker,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = publicationDisplayTitle(state.title),
                style = MaterialTheme.readerTypography.headline,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (metaText.isNotEmpty()) {
                Text(
                    text = metaText,
                    style = MaterialTheme.readerTypography.meta,
                    color = MaterialTheme.colorScheme.outline,
                )

                Spacer(modifier = Modifier.height(18.dp))
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }

    @Composable
    private fun ReaderFooter() {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 36.dp),
        )
    }

    @Composable
    private fun TopChrome(
        progressPercent: Int,
        onBack: () -> Unit,
    ) {
        val scrim = Brush.verticalGradient(
            0f to MaterialTheme.colorScheme.surfaceBright,
            0.72f to MaterialTheme.colorScheme.surfaceBright,
            1f to MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0f),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(scrim)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 14.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
        ) {
            ChromeCircleButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart),
            )

            Text(
                text = "READING · $progressPercent%",
                style = MaterialTheme.readerTypography.kickerSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }

    @Composable
    private fun ChromeCircleButton(
        icon: ImageVector,
        contentDescription: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                MaterialTheme.nestboxColors.noteRule,
            ),
            modifier = modifier.size(34.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }

    // Deliberately not inset-padded: the design system calls this "full-bleed at the very top," and a
    // 3dp color sliver reads fine under the status bar icons (it's a passive flourish, not interactive
    // chrome), so it's pinned to the true physical edge rather than dropped below TopChrome with the
    // rest of the reader's chrome.
    @Composable
    private fun ScrollProgressBar(progress: Float) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(
                        0f,
                        1f,
                    ),)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }

    @Composable
    private fun rememberReadingProgress(listState: LazyListState): State<Float> = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount

            when {
                totalItems == 0 -> 0f
                listState.canScrollForward.not() -> 1f
                else -> {
                    val firstIndex = listState.firstVisibleItemIndex
                    val firstItemSize = listState.layoutInfo.visibleItemsInfo
                        .firstOrNull { it.index == firstIndex }
                        ?.size
                        ?.takeIf { it > 0 }
                    val withinItem = if (firstItemSize != null) {
                        listState.firstVisibleItemScrollOffset.toFloat() / firstItemSize
                    } else {
                        0f
                    }

                    ((firstIndex + withinItem) / totalItems.toFloat()).coerceIn(
                        0f,
                        1f,
                    )
                }
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

/**
 * Drops a leading level-1 heading from [blocks] when it just restates [displayTitle] (trimmed,
 * case-insensitive), since `ArticleHead` already draws that title as the H1 — otherwise the document's
 * own `# Title` line duplicates it immediately below. Only the leading block is ever checked, so a
 * level-1 heading appearing mid-document still renders.
 */
private fun dropDuplicateLeadingTitle(
    blocks: List<MarkdownBlock>,
    displayTitle: String,
): List<MarkdownBlock> {
    val leading = blocks.firstOrNull()

    if (leading is MarkdownBlock.Heading && leading.level == 1) {
        val headingText = markdownPlainText(leading.inlines).trim()

        if (headingText.equals(
            displayTitle.trim(),
            ignoreCase = true,
        )) {
            return blocks.drop(1)
        }
    }

    return blocks
}

/**
 * The reader's meta-line date (design system §C): `JUL 14, 2026` from a `PublicationRepositoryImpl`
 * date string that is regex-shaped as `yyyy-MM-dd` but never calendar-validated at the source, and can
 * be blank when none of its three sources match. Falls back to the raw value on any parse failure
 * rather than crashing or silently blanking the meta line.
 */
private fun formatReaderDate(raw: String?): String? {
    if (raw.isNullOrBlank()) {
        return null
    }

    val parsed = runCatching { LocalDate.parse(raw) }.getOrNull() ?: return raw
    val month = parsed.month.getDisplayName(
        JavaTimeTextStyle.SHORT,
        Locale.US,
    ).uppercase()

    return "$month ${parsed.dayOfMonth}, ${parsed.year}"
}
