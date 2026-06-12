package nl.rhaydus.nestbox.core.presentation.toad

interface UiAction<D : ActionDependencies, S : UiState, E : UiEvent, V : LocalVariables> {
    suspend fun execute(
        dependencies: D,
        scope: ActionScope<S, E, V>,
    )
}
 