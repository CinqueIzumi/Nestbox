package nl.rhaydus.nestbox.core.auth.domain.usecase

import nl.rhaydus.common.runCatchingCancellable
import nl.rhaydus.nestbox.core.auth.domain.model.DeviceAuthorization
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository

class StartGitHubAuthorizationUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(): Result<DeviceAuthorization> =
        runCatchingCancellable { accountRepository.startDeviceAuthorization() }
}
