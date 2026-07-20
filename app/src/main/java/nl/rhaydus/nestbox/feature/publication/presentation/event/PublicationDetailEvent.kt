package nl.rhaydus.nestbox.feature.publication.presentation.event

import nl.rhaydus.toad.UiEvent

sealed interface PublicationDetailEvent : UiEvent {

    data class OpenLinkEvent(val url: String) : PublicationDetailEvent
}
