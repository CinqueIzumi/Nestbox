package nl.rhaydus.nestbox.feature.home.presentation.state

import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.nestbox.core.content.domain.model.PublicationType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HomeSelectionTest {
    private fun publication(
        id: String,
        type: PublicationType,
    ) = PublicationSummary(
        id = id,
        type = type,
        number = null,
        date = "2026-01-01",
        title = null,
        previewText = "",
    )

    @Nested
    inner class DeriveHomeSelection {
        @Test
        fun `ALL picks the newest publication overall as the hero, whatever its type`() {
            // ----- Arrange -----
            val newestArticle = publication(
                "article-1",
                PublicationType.ARTICLE,
            )
            val publications = listOf(
                newestArticle,
                publication(
                    "weekly-1",
                    PublicationType.WEEKLY_LETTER,
                ),
                publication(
                    "interview-1",
                    PublicationType.INTERVIEW_PREP,
                ),
            )

            // ----- Act -----
            val selection = deriveHomeSelection(
                publications,
                PublicationFilter.ALL,
            )

            // ----- Assert -----
            assertEquals(
                newestArticle,
                selection.hero,
            )
        }

        @Test
        fun `the hero is excluded from its own section while its siblings remain`() {
            // ----- Arrange -----
            val heroArticle = publication(
                "article-1",
                PublicationType.ARTICLE,
            )
            val siblingArticle = publication(
                "article-2",
                PublicationType.ARTICLE,
            )
            val publications = listOf(
                heroArticle,
                siblingArticle,
                publication(
                    "weekly-1",
                    PublicationType.WEEKLY_LETTER,
                ),
            )

            // ----- Act -----
            val selection = deriveHomeSelection(
                publications,
                PublicationFilter.ALL,
            )

            // ----- Assert -----
            val articleSection = selection.sections.first { section -> section.type == PublicationType.ARTICLE }
            assertEquals(
                listOf(siblingArticle),
                articleSection.publications,
            )
        }

        @Test
        fun `ALL returns sections in the fixed order weekly letter, article, interview prep`() {
            // ----- Arrange -----
            val publications = listOf(
                publication(
                    "weekly-1",
                    PublicationType.WEEKLY_LETTER,
                ),
                publication(
                    "weekly-2",
                    PublicationType.WEEKLY_LETTER,
                ),
                publication(
                    "article-1",
                    PublicationType.ARTICLE,
                ),
                publication(
                    "interview-1",
                    PublicationType.INTERVIEW_PREP,
                ),
            )

            // ----- Act -----
            val selection = deriveHomeSelection(
                publications,
                PublicationFilter.ALL,
            )

            // ----- Assert -----
            assertEquals(
                listOf(PublicationType.WEEKLY_LETTER, PublicationType.ARTICLE, PublicationType.INTERVIEW_PREP),
                selection.sections.map { section -> section.type },
            )
        }

        @Test
        fun `a type with no publications produces no section at all`() {
            // ----- Arrange -----
            val publications = listOf(
                publication(
                    "weekly-1",
                    PublicationType.WEEKLY_LETTER,
                ),
                publication(
                    "article-1",
                    PublicationType.ARTICLE,
                ),
                publication(
                    "article-2",
                    PublicationType.ARTICLE,
                ),
            )

            // ----- Act -----
            val selection = deriveHomeSelection(
                publications,
                PublicationFilter.ALL,
            )

            // ----- Assert -----
            assertTrue(selection.sections.none { section -> section.type == PublicationType.INTERVIEW_PREP })
        }

        @Test
        fun `a single-type filter yields a hero of that type and exactly one section`() {
            // ----- Arrange -----
            val heroArticle = publication(
                "article-1",
                PublicationType.ARTICLE,
            )
            val siblingArticle = publication(
                "article-2",
                PublicationType.ARTICLE,
            )
            val publications = listOf(
                publication(
                    "weekly-1",
                    PublicationType.WEEKLY_LETTER,
                ),
                heroArticle,
                siblingArticle,
                publication(
                    "interview-1",
                    PublicationType.INTERVIEW_PREP,
                ),
            )

            // ----- Act -----
            val selection = deriveHomeSelection(
                publications,
                PublicationFilter.ARTICLES,
            )

            // ----- Assert -----
            assertEquals(
                heroArticle,
                selection.hero,
            )
            assertEquals(
                1,
                selection.sections.size,
            )
            assertEquals(
                PublicationType.ARTICLE,
                selection.sections.single().type,
            )
            assertEquals(
                listOf(siblingArticle),
                selection.sections.single().publications,
            )
        }

        @Test
        fun `a filter matching nothing yields a null hero and empty sections`() {
            // ----- Arrange -----
            val publications = listOf(
                publication(
                    "weekly-1",
                    PublicationType.WEEKLY_LETTER,
                ),
                publication(
                    "article-1",
                    PublicationType.ARTICLE,
                ),
            )

            // ----- Act -----
            val selection = deriveHomeSelection(
                publications,
                PublicationFilter.INTERVIEW_PREP,
            )

            // ----- Assert -----
            assertNull(selection.hero)
            assertTrue(selection.sections.isEmpty())
        }

        @Test
        fun `an empty input list yields a null hero and empty sections`() {
            // ----- Arrange -----
            val publications = emptyList<PublicationSummary>()

            // ----- Act -----
            val selection = deriveHomeSelection(
                publications,
                PublicationFilter.ALL,
            )

            // ----- Assert -----
            assertNull(selection.hero)
            assertTrue(selection.sections.isEmpty())
        }

        @Test
        fun `a type whose only publication became the hero contributes no section`() {
            // ----- Arrange -----
            val heroInterviewPrep = publication(
                "interview-1",
                PublicationType.INTERVIEW_PREP,
            )
            val publications = listOf(
                heroInterviewPrep,
                publication(
                    "weekly-1",
                    PublicationType.WEEKLY_LETTER,
                ),
                publication(
                    "article-1",
                    PublicationType.ARTICLE,
                ),
            )

            // ----- Act -----
            val selection = deriveHomeSelection(
                publications,
                PublicationFilter.ALL,
            )

            // ----- Assert -----
            assertTrue(selection.sections.none { section -> section.type == PublicationType.INTERVIEW_PREP })
        }
    }
}
