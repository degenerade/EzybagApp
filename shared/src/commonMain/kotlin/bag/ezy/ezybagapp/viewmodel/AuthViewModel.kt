package bag.ezy.ezybagapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import bag.ezy.ezybagapp.model.data.Review
import bag.ezy.ezybagapp.model.queries.AdminRepository
import bag.ezy.ezybagapp.model.queries.GoogleAuthHandler
import bag.ezy.ezybagapp.model.queries.ReviewRepository
import bag.ezy.ezybagapp.model.queries.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class AuthViewModel {
    private val userRepository = UserRepository()
    private val reviewRepository = ReviewRepository()
    private val adminRepository = AdminRepository()
    private val scope = CoroutineScope(Dispatchers.Main)

    var statusMessage by mutableStateOf("Ready. Tap a button below to test Auth, Reviews & RBAC.")
        private set

    var isLoading by mutableStateOf(false)
        private set

    // 1. Sign up with mock data
    fun signUpWithMockData() {
        scope.launch {
            isLoading = true
            statusMessage = "Signing up mock user..."
            val mockEmail = "mock_user_${Random.nextInt(1000, 9999)}@example.com"
            userRepository.signUpWithEmail(mockEmail, "password123", "Mock User")
                .onSuccess { user -> statusMessage = "✅ Sign Up Success!\nEmail: ${user.email}\nUID: ${user.uid}" }
                .onFailure { e -> statusMessage = "❌ Sign Up Failed: ${e.message}" }
            isLoading = false
        }
    }

    // 2. Log in by mail/password
    fun loginWithEmail(email: String, pass: String) {
        scope.launch {
            isLoading = true
            statusMessage = "Logging in..."
            userRepository.loginWithEmail(email, pass)
                .onSuccess { user -> statusMessage = "✅ Login Success!\nEmail: ${user.email}" }
                .onFailure { e -> statusMessage = "❌ Login Failed: ${e.message}" }
            isLoading = false
        }
    }

    // 3. Log in by Google
    fun loginWithGoogle() {
        scope.launch {
            isLoading = true
            statusMessage = "Opening Google Sign-In..."
            val result = GoogleAuthHandler.onGoogleSignInRequested()
            result.onSuccess { (email, name) ->
                userRepository.signInWithGoogleCredential(email, name)
                    .onSuccess { user -> statusMessage = "✅ Google Login Success!\nEmail: ${user.email}" }
                    .onFailure { e -> statusMessage = "❌ Google Firestore Save Failed: ${e.message}" }
            }.onFailure { e ->
                statusMessage = "❌ Google Sign-In Cancelled or Failed: ${e.message}"
            }
            isLoading = false
        }
    }

    // Sign Out
    fun signOut() {
        scope.launch {
            try {
                Firebase.auth.signOut()
                statusMessage = "🔒 Signed Out Successfully. You are now unauthenticated."
            } catch (e: Exception) {
                statusMessage = "❌ Sign Out Failed: ${e.message}"
            }
        }
    }

    // 4. Leave Review (Checks if user is authenticated)
    fun leaveReview() {
        scope.launch {
            isLoading = true
            statusMessage = "Attempting to leave review..."
            val review = Review(
                reviewId = "rev_${Random.nextInt(100, 999)}",
                bookingId = "booking_1",
                contractorId = "cont_1",
                stars = 5,
                comment = "Amazing experience!",
                createdAt = "2025-02-25"
            )
            reviewRepository.leaveReview(review)
                .onSuccess { 
                    val userEmail = Firebase.auth.currentUser?.email ?: "Unknown User"
                    statusMessage = "✅ Review Posted Successfully!\nAuth User Email: $userEmail" 
                }
                .onFailure { e -> statusMessage = "❌ Failed: ${e.message}" }
            isLoading = false
        }
    }

    // 5. RBAC: Unauthorized Access Test
    fun testUnauthorizedAccess() {
        scope.launch {
            isLoading = true
            statusMessage = "Testing Unauthorized access to Admin collection..."
            adminRepository.testUnauthorizedAccess()
                .onSuccess { msg -> statusMessage = msg }
                .onFailure { e -> statusMessage = "Error: ${e.message}" }
            isLoading = false
        }
    }

    // 6. RBAC: Customer Access Test
    fun testCustomerAccess() {
        scope.launch {
            isLoading = true
            statusMessage = "Testing Customer access to Admin collection..."
            adminRepository.testCustomerAccess()
                .onSuccess { msg -> statusMessage = msg }
                .onFailure { e -> statusMessage = "Error: ${e.message}" }
            isLoading = false
        }
    }

    // 7. RBAC: Admin Access Test
    fun testAdminAccess() {
        scope.launch {
            isLoading = true
            statusMessage = "Testing Admin access..."
            adminRepository.testAdminAccess()
                .onSuccess { msg -> statusMessage = msg }
                .onFailure { e -> statusMessage = "❌ Admin Access Denied (${e.message})" }
            isLoading = false
        }
    }
}
