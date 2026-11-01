package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Review
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock

interface ReviewRepository {
    suspend fun submitReview(
        bookingId: String,
        rating: Int,
        comment: String,
        contractorId: String = ""
    ): Result<Unit>
}

class FirebaseReviewRepository : ReviewRepository {

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    override suspend fun submitReview(
        bookingId: String,
        rating: Int,
        comment: String,
        contractorId: String
    ): Result<Unit> = runCatching {
        require(bookingId.isNotBlank()) {
            "Booking ID is required."
        }
        require(rating in 1..5) {
            "Rating must be between 1 and 5."
        }

        val customerId = auth.currentUser?.uid
            ?: throw IllegalStateException("Please sign in before submitting a review.")

        val doc = firestore.collection("reviews").document
        val now = Clock.System.now().toString()

        val review = Review(
            reviewId = doc.id,
            bookingId = bookingId,
            customerId = customerId,
            contractorId = contractorId,
            stars = rating,
            comment = comment.trim(),
            createdAt = now
        )

        doc.set(Review.serializer(), review)
    }
}
