package bag.ezy.ezybagapp.model.queries

import bag.ezy.ezybagapp.model.data.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class UserRepository {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    // Updated to match your Firestore rules collection "customers"
    private val customersCollection = firestore.collection("customers")

    // 1. Sign up with email & password
    suspend fun signUpWithEmail(email: String, password: String, fullName: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password)
            val firebaseUser = authResult.user ?: throw Exception("Sign up failed")
            
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                fullName = fullName,
                createdAt = "2025-02-25"
            )
            
            customersCollection.document(user.uid).set(User.serializer(), user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Log in with email & password
    suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password)
            val firebaseUser = authResult.user ?: throw Exception("Login failed")
            
            val doc = customersCollection.document(firebaseUser.uid).get()
            val user = doc.data(User.serializer()) ?: User(uid = firebaseUser.uid, email = email)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3 & 4. Google Sign Up / Log In helper
    suspend fun signInWithGoogleCredential(email: String, fullName: String): Result<User> {
        return try {
            val fakeUid = "google_${email.hashCode()}"
            val doc = customersCollection.document(fakeUid).get()
            val user = if (doc.exists) {
                doc.data(User.serializer()) ?: User(uid = fakeUid, email = email, fullName = fullName)
            } else {
                val newUser = User(uid = fakeUid, email = email, fullName = fullName, createdAt = "2025-02-25")
                customersCollection.document(newUser.uid).set(User.serializer(), newUser)
                newUser
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
