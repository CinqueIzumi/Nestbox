package nl.rhaydus.nestbox.core.content.domain.model

enum class PublicationType(
    val label: String,
    val sectionKicker: String,
    val sectionHeadline: String,
) {
    WEEKLY_LETTER(
        label = "Weekly letter",
        sectionKicker = "Weekly letters",
        sectionHeadline = "Past editions",
    ),
    ARTICLE(
        label = "Article",
        sectionKicker = "Articles",
        sectionHeadline = "Long reads",
    ),
    INTERVIEW_PREP(
        label = "Interview prep",
        sectionKicker = "Interview prep",
        sectionHeadline = "Get ready",
    ),
    ;

    companion object {
        fun fromMarker(marker: String): PublicationType = when (marker.trim().lowercase()) {
            "weekly", "weekly letter" -> WEEKLY_LETTER
            "article" -> ARTICLE
            "interview" -> INTERVIEW_PREP
            else -> WEEKLY_LETTER
        }
    }
}
