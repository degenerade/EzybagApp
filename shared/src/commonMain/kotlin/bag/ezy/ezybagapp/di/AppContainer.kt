package bag.ezy.ezybagapp.di

import bag.ezy.ezybagapp.model.repository.AuthRepository
import bag.ezy.ezybagapp.model.repository.BookingRepository
import bag.ezy.ezybagapp.model.repository.FakeAuthRepository
import bag.ezy.ezybagapp.model.repository.FakeBookingRepository
import bag.ezy.ezybagapp.model.repository.FakeReviewRepository
import bag.ezy.ezybagapp.model.repository.ReviewRepository

object AppContainer {
    val authRepository: AuthRepository = FakeAuthRepository()
    val bookingRepository: BookingRepository = FakeBookingRepository()
    val reviewRepository: ReviewRepository = FakeReviewRepository()
}