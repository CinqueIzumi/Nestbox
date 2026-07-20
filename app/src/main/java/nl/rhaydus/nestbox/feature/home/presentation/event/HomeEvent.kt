package nl.rhaydus.nestbox.feature.home.presentation.event

import nl.rhaydus.toad.UiEvent

internal sealed interface HomeEvent : UiEvent {

    data class OpenPublicationEvent(val publicationId: String) : HomeEvent
}
