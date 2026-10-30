package bag.ezy.ezybagapp.model.queries

import bag.ezy.ezybagapp.model.data.Review
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class ReviewRepository {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    private val reviewsCollection = firestore.collection("reviews")

    suspend fun leaveReview(review: Review): Result<Unit> {
        return try {
            val currentUser = auth.currentUser 
                ?: return Result.failure(Exception("User not authenticated! Please sign in or sign up first."))

            val reviewToSave = review.copy(customerId = currentUser.uid)
            reviewsCollection.document(review.reviewId).set(Review.serializer(), reviewToSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
