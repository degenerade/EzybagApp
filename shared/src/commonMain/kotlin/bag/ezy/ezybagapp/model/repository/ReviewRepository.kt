
package bag.ezy.ezybagapp.model.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

interface ReviewRepository {
    suspend fun submitReview(
        bookingId: String,
        rating: Int,
        comment: String
    ): Result<Unit>
}

class FirebaseReviewRepository : ReviewRepository {

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    override suspend fun submitReview(
        bookingId: String,
        rating: Int,
        comment: String
    ): Result<Unit> = runCatching {
        require(bookingId.isNotBlank()) {
            "Booking ID is required."
        }
        require(rating in 1..5) {
            "Rating must be between 1 and 5."
        }

        val customerId = auth.currentUser?.uid
            ?: throw IllegalStateException("Please sign in before submitting a review.")

        val review = mapOf(
            "bookingId" to bookingId,
            "customerId" to customerId,
            "rating" to rating,
            "comment" to comment.trim(),
            "createdAt" to ""
        )

        firestore.collection("reviews").document
            .set(review)
    }
}
