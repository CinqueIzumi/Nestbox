package nl.rhaydus.nestbox.feature.publication.presentation.event

import nl.rhaydus.nestbox.core.presentation.toad.UiEvent

sealed interface PublicationDetailEvent : UiEvent {

    data class OpenLinkEvent(val url: String) : PublicationDetailEvent
}
