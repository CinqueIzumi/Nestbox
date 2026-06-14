package nl.rhaydus.nestbox.feature.home.presentation.event

import nl.rhaydus.nestbox.core.presentation.toad.UiEvent

sealed interface HomeEvent : UiEvent {

    data class OpenPublicationEvent(val publicationId: String) : HomeEvent
}
