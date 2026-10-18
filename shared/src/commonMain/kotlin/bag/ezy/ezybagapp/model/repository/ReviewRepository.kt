package bag.ezy.ezybagapp.model.repository

import kotlinx.coroutines.delay

interface ReviewRepository {
    suspend fun submitReview(bookingId: String, rating: Int, comment: String): Result<Unit>
}
class FakeReviewRepository : ReviewRepository {
    override suspend fun submitReview(
        bookingId: String,
        rating: Int,
        comment: String
    ): Result<Unit> {
        delay(600)
        return Result.success(Unit)
    }
}