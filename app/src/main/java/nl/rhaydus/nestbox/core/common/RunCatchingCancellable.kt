package nl.rhaydus.nestbox.core.common

import kotlinx.coroutines.CancellationException

// runCatching, but never swallows structured-concurrency cancellation. Plain runCatching catches
// CancellationException too, so using it in suspend code turns a cancelled coroutine into a
// Result.failure and breaks cancellation. Use this wherever a suspend call is wrapped in a Result.
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    runCatching(block).onFailure { if (it is CancellationException) throw it }
