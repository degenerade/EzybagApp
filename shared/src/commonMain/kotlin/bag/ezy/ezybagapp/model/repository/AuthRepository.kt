
package bag.ezy.ezybagapp.model.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

interface AuthRepository {
    suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<Unit>

    suspend fun sendPasswordReset(
        email: String
    ): Result<Unit>

    suspend fun getCurrentUserName(): String
}

class FirebaseAuthRepository : AuthRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> = try {
        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(
            Exception(e.message ?: "Unable to sign in. Please try again.")
        )
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<Unit> = try {
        val normalizedEmail = email.trim()

        val authResult = auth.createUserWithEmailAndPassword(
            normalizedEmail,
            password
        )

        val user = authResult.user
            ?: throw Exception("Unable to retrieve the new account.")

        val profile = mapOf(
            "uid" to user.uid,
            "fullName" to name.trim(),
            "email" to normalizedEmail,
            "role" to "customer",
            "status" to "active"
        )

        firestore.collection("users")
            .document(user.uid)
            .set(profile)

        firestore.collection("customers")
            .document(user.uid)
            .set(profile)

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(
            Exception(e.message ?: "Unable to create your account.")
        )
    }

    override suspend fun sendPasswordReset(
        email: String
    ): Result<Unit> = try {
        auth.sendPasswordResetEmail(email.trim())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(
            Exception(e.message ?: "Unable to send the password reset email.")
        )
    }

    override suspend fun getCurrentUserName(): String {
        val user = auth.currentUser ?: return "Customer"

        return try {
            val document = firestore.collection("users")
                .document(user.uid)
                .get()

            document.get<String>("fullName")
                .takeIf { it.isNotBlank() }
                ?: user.email?.substringBefore("@")
                ?: "Customer"
        } catch (e: Exception) {
            user.email?.substringBefore("@") ?: "Customer"
        }
    }
}
