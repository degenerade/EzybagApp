package bag.ezy.ezybagapp.di

import bag.ezy.ezybagapp.model.repository.FirebaseAuthRepository
import bag.ezy.ezybagapp.model.repository.FirebaseBookingRepository
import bag.ezy.ezybagapp.model.repository.FirebaseContractorRepository
import bag.ezy.ezybagapp.model.repository.FirebaseReviewRepository

object AppContainer {
    val authRepository = FirebaseAuthRepository()
    val bookingRepository = FirebaseBookingRepository()
    val reviewRepository = FirebaseReviewRepository()
    val contractorRepository = FirebaseContractorRepository()
}
