package nl.rhaydus.nestbox.feature.profile.presentation.collector

import nl.rhaydus.nestbox.feature.profile.presentation.event.ProfileEvent
import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileDependencies
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileLocalVariables
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileUiState
import nl.rhaydus.toad.Collector

sealed interface ProfileCollector : Collector<
        ProfileUiState,
        ProfileEvent,
        ProfileDependencies,
        ProfileLocalVariables,
        >
