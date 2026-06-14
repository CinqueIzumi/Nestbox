package nl.rhaydus.nestbox.core.content.domain.model

enum class PublicationType(val label: String) {
    WEEKLY_LETTER("Weekly letter"),
    ARTICLE("Article"),
    INTERVIEW("Interview"),
    ;

    companion object {
        fun fromMarker(marker: String): PublicationType = when (marker.trim().lowercase()) {
            "weekly", "weekly letter" -> WEEKLY_LETTER
            "article" -> ARTICLE
            "interview" -> INTERVIEW
            else -> WEEKLY_LETTER
        }
    }
}
