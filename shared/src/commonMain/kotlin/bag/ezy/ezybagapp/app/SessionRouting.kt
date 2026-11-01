package bag.ezy.ezybagapp.app

import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.queries.AdminRepository
import bag.ezy.ezybagapp.model.repository.FirebaseContractorRepository

enum class UserRole { SignedOut, Admin, Contractor, PendingContractor, Customer }

suspend fun resolveCurrentRole(): Result<UserRole> = runCatching {
    if (AppContainer.authRepository.currentUserId() == null) return@runCatching UserRole.SignedOut
    if (AdminRepository().isCurrentUserAdmin().getOrThrow()) return@runCatching UserRole.Admin

    val contractor = FirebaseContractorRepository().getCurrentContractor().getOrThrow()
    when {
        contractor == null      -> UserRole.Customer
        contractor.isApproved   -> UserRole.Contractor
        else                    -> UserRole.PendingContractor
    }
}