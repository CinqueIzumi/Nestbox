package nl.rhaydus.nestbox.core.auth.domain.usecase

import nl.rhaydus.nestbox.core.auth.domain.model.DeviceAuthorization
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository
import nl.rhaydus.nestbox.core.common.runCatchingCancellable

class StartGitHubAuthorizationUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(): Result<DeviceAuthorization> =
        runCatchingCancellable { accountRepository.startDeviceAuthorization() }
}
