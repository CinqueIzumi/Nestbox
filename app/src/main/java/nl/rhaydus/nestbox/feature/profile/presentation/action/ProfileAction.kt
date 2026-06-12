package nl.rhaydus.nestbox.feature.profile.presentation.action

import nl.rhaydus.nestbox.core.presentation.toad.UiAction
import nl.rhaydus.nestbox.feature.profile.presentation.event.ProfileEvent
import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileDependencies
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileLocalVariables
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileUiState

sealed interface ProfileAction : UiAction<
        ProfileDependencies,
        ProfileUiState,
        ProfileEvent,
        ProfileLocalVariables,
        >
