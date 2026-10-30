
package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.queries.GoogleAuthHandler
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock

data class CustomerProfile(
    val name: String,
    val email: String,
    val phone: String,
    val address: String
)

interface AuthRepository {

    suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<Unit>

    suspend fun signInWithGoogle(): Result<Unit>

    suspend fun sendPasswordReset(
        email: String
    ): Result<Unit>

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit>

    /**
     * Deletes the signed-in customer's Firestore profile
     * and Firebase Authentication account.
     *
     * Other Firestore records are preserved.
     * This implementation supports email/password accounts.
     */
    suspend fun deleteCurrentAccount(
        currentPassword: String
    ): Result<Unit>

    suspend fun getCurrentUserName(): String

    suspend fun getCurrentCustomerProfile(): CustomerProfile?

    suspend fun updateCurrentCustomerAddress(
        address: String
    ): Result<Unit>

    suspend fun updateCurrentCustomerPhone(
        phone: String
    ): Result<Unit>

    fun currentUserId(): String?

    suspend fun signOut()
}

class FirebaseAuthRepository : AuthRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val customers = firestore.collection("customers")

    // EMAIL / PASSWORD LOGIN
    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        require(email.trim().isNotEmpty()) {
            "Email is required."
        }

        require(password.isNotEmpty()) {
            "Password is required."
        }

        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        )
    }

    // EMAIL / PASSWORD REGISTRATION
    override suspend fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        val first = firstName.trim()
        val last = lastName.trim()
        val normalizedEmail = email.trim()

        require(first.isNotEmpty()) {
            "First name is required."
        }

        require(last.isNotEmpty()) {
            "Last name is required."
        }

        require(normalizedEmail.isNotEmpty()) {
            "Email is required."
        }

        require(password.length >= 8) {
            "Password must contain at least 8 characters."
        }

        val authResult = auth.createUserWithEmailAndPassword(
            normalizedEmail,
            password
        )

        val user = authResult.user
            ?: error("Unable to retrieve the new account.")

        val now = Clock.System.now().toString()

        val profile = mapOf(
            "uid" to user.uid,
            "firstName" to first,
            "lastName" to last,
            "email" to normalizedEmail,
            "phone" to "",
            "address" to "",
            "createdAt" to now,
            "updatedAt" to now
        )

        try {
            customers.document(user.uid).set(profile)

            // Require email/password users to log in after registration.
            auth.signOut()
        } catch (e: Exception) {
            // Do not silently leave a partially registered account.
            // The Authentication account may still exist if profile
            // creation or sign-out failed, so report the failure.
            throw e
        }
    }

    // GOOGLE SIGN-IN / REGISTRATION
    override suspend fun signInWithGoogle(): Result<Unit> {
        var googleAuthenticated = false

        return try {
            val (_, displayName) =
                GoogleAuthHandler
                    .onGoogleSignInRequested()
                    .getOrThrow()

            googleAuthenticated = true

            val user = auth.currentUser
                ?: error("Firebase did not return an authenticated user.")

            val customerRef = customers.document(user.uid)
            val customerDocument = customerRef.get()

            // Create a profile only for a new customer.
            // Never overwrite an existing phone number or address.
            if (!customerDocument.exists) {
                val names = displayName.trim()
                    .split(Regex("\\s+"), limit = 2)

                val firstName = names.firstOrNull().orEmpty()
                val lastName = names.getOrNull(1).orEmpty()
                val now = Clock.System.now().toString()

                customerRef.set(
                    mapOf(
                        "uid" to user.uid,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to user.email.orEmpty(),
                        "phone" to "",
                        "address" to "",
                        "createdAt" to now,
                        "updatedAt" to now
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            if (googleAuthenticated) {
                try {
                    auth.signOut()
                } catch (_: Exception) {
                    // Preserve the original error.
                }
            }

            Result.failure(e)
        }
    }

    // PASSWORD RESET
    override suspend fun sendPasswordReset(
        email: String
    ): Result<Unit> = runCatching {
        require(email.trim().isNotEmpty()) {
            "Email is required."
        }

        auth.sendPasswordResetEmail(email.trim())
    }

    // CHANGE PASSWORD
    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = runCatching {
        val user = auth.currentUser
            ?: error("You must sign in to change your password.")

        val email = user.email?.takeIf { it.isNotBlank() }
            ?: error("No email address is associated with this account.")

        require(currentPassword.isNotBlank()) {
            "Enter your current password."
        }

        require(newPassword.length >= 8) {
            "Your new password must contain at least 8 characters."
        }

        require(currentPassword != newPassword) {
            "Your new password must be different from your current password."
        }

        val credential = EmailAuthProvider.credential(
            email = email,
            password = currentPassword
        )

        // Verify the current password before making the change.
        user.reauthenticate(credential)

        // Update the Firebase Authentication password.
        user.updatePassword(newPassword)
    }

    // DELETE CURRENT ACCOUNT
    override suspend fun deleteCurrentAccount(
        currentPassword: String
    ): Result<Unit> = runCatching {
        val user = auth.currentUser
            ?: error("You must sign in before deleting your account.")

        val uid = user.uid

        val email = user.email?.takeIf { it.isNotBlank() }
            ?: error(
                "This account requires provider-specific reauthentication."
            )

        require(currentPassword.isNotBlank()) {
            "Enter your current password to continue."
        }

        val credential = EmailAuthProvider.credential(
            email = email,
            password = currentPassword
        )

        // Step 1: Verify the customer's identity.
        user.reauthenticate(credential)

        // Step 2: Delete only the customer's profile document.
        // This uses the UID, not the email address.
        // No bookings, reviews, contractor records, or support
        // reports are deleted by this operation.
        customers.document(uid).delete()

        // Step 3: Delete the Authentication account last.
        // If Firestore deletion fails, execution stops here and
        // the Authentication account is not deleted.
        user.delete()
    }

    // GET CURRENT CUSTOMER NAME
    override suspend fun getCurrentUserName(): String {
        val user = auth.currentUser ?: return ""

        return try {
            val document = customers.document(user.uid).get()

            val firstName =
                document.get<String?>("firstName").orEmpty()

            val lastName =
                document.get<String?>("lastName").orEmpty()

            listOf(firstName, lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { user.email.orEmpty() }
        } catch (_: Exception) {
            user.email.orEmpty()
        }
    }

    // GET CURRENT CUSTOMER PROFILE
    override suspend fun getCurrentCustomerProfile(): CustomerProfile? {
        val user = auth.currentUser ?: return null

        return try {
            val document = customers.document(user.uid).get()

            val firstName =
                document.get<String?>("firstName").orEmpty()

            val lastName =
                document.get<String?>("lastName").orEmpty()

            val name = listOf(firstName, lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { user.email.orEmpty() }

            val email = document.get<String?>("email")
                ?.takeIf { it.isNotBlank() }
                ?: user.email.orEmpty()

            val phone = document.get<String?>("phone").orEmpty()
            val address = document.get<String?>("address").orEmpty()

            CustomerProfile(
                name = name,
                email = email,
                phone = phone,
                address = address
            )
        } catch (_: Exception) {
            CustomerProfile(
                name = user.email.orEmpty(),
                email = user.email.orEmpty(),
                phone = "",
                address = ""
            )
        }
    }

    // UPDATE CURRENT CUSTOMER ADDRESS
    override suspend fun updateCurrentCustomerAddress(
        address: String
    ): Result<Unit> = runCatching {
        val user = auth.currentUser
            ?: error("You must sign in to update your address.")

        val normalizedAddress = address.trim()

        require(normalizedAddress.isNotBlank()) {
            "Address cannot be empty."
        }

        customers.document(user.uid).update(
            mapOf(
                "address" to normalizedAddress,
                "updatedAt" to Clock.System.now().toString()
            )
        )
    }

    // UPDATE CURRENT CUSTOMER PHONE
    override suspend fun updateCurrentCustomerPhone(
        phone: String
    ): Result<Unit> = runCatching {
        val user = auth.currentUser
            ?: error("You must sign in to update your phone number.")

        val normalizedPhone = phone.trim()

        require(normalizedPhone.isNotBlank()) {
            "Phone number cannot be empty."
        }

        require(normalizedPhone.length in 7..20) {
            "Please enter a valid phone number."
        }

        customers.document(user.uid).update(
            mapOf(
                "phone" to normalizedPhone,
                "updatedAt" to Clock.System.now().toString()
            )
        )
    }

    // CURRENT FIREBASE USER ID
    override fun currentUserId(): String? {
        return auth.currentUser?.uid
    }

    // LOGOUT
    override suspend fun signOut() {
        auth.signOut()
    }
}
