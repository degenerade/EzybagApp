
package bag.ezy.ezybagapp.feature.dbdevpage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import bag.ezy.ezybagapp.model.data.Review
import bag.ezy.ezybagapp.model.queries.AdminRepository
import bag.ezy.ezybagapp.model.queries.ReviewRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class DbTestViewModel {

    private val reviewRepository = ReviewRepository()
    private val adminRepository = AdminRepository()
    private val scope = CoroutineScope(Dispatchers.Main)

    var statusMessage by mutableStateOf(
        "Ready. Test reviews and access permissions below."
    )
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun signOut() {
        scope.launch {
            isLoading = true

            try {
                Firebase.auth.signOut()
                statusMessage = "Signed out successfully."
            } catch (e: Exception) {
                statusMessage = "Sign-out failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun leaveReview() {
        scope.launch {
            isLoading = true
            statusMessage = "Attempting to leave review..."

            try {
                val review = Review(
                    reviewId = "rev_${Random.nextInt(100, 999)}",
                    bookingId = "booking_1",
                    contractorId = "cont_1",
                    stars = 5,
                    comment = "Development test review",
                    createdAt = "2025-02-25"
                )

                reviewRepository.leaveReview(review)
                    .onSuccess {
                        val email = Firebase.auth.currentUser?.email
                            ?: "Unknown user"

                        statusMessage =
                            "Review posted successfully.\nSigned-in user: $email"
                    }
                    .onFailure { error ->
                        statusMessage =
                            "Review failed: ${error.message}"
                    }
            } catch (e: Exception) {
                statusMessage = "Review failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun testUnauthorizedAccess() {
        runTest("Testing unauthenticated access") {
            adminRepository.testUnauthorizedAccess()
        }
    }

    fun testCustomerAccess() {
        runTest("Testing customer access to admin data") {
            adminRepository.testCustomerAccess()
        }
    }

    fun testAdminAccess() {
        runTest("Testing admin access") {
            adminRepository.testAdminAccess()
        }
    }

    private fun runTest(
        message: String,
        action: suspend () -> Result<String>
    ) {
        scope.launch {
            isLoading = true
            statusMessage = message

            try {
                action()
                    .onSuccess { result ->
                        statusMessage = result
                    }
                    .onFailure { error ->
                        statusMessage =
                            "Access test failed: ${error.message}"
                    }
            } catch (e: Exception) {
                statusMessage =
                    "Access test failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
