
package bag.ezy.ezybagapp.model.queries

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class AdminRepository {

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    private val admins = firestore.collection("admins")
    private val adminOnly = firestore.collection("adminOnly")

    /**
     * Checks whether the current user is an active administrator.
     */
    suspend fun isCurrentUserAdmin(): Result<Boolean> = runCatching {
        val user = auth.currentUser
            ?: return@runCatching false

        val document = admins.document(user.uid).get()

        if (!document.exists) {
            return@runCatching false
        }

        val role = document.get<String?>("role")
        val active = document.get<Boolean?>("active")

        role == "admin" && active == true
    }


    suspend fun testUnauthorizedAccess(): Result<String> {
        if (auth.currentUser != null) {
            return Result.failure(
                IllegalStateException(
                    "Sign out before testing unauthorized access."
                )
            )
        }

        return try {
            adminOnly.document("unauthorized-test").set(
                mapOf("message" to "Unauthorized access test")
            )

            Result.success(
                "FAIL: An unauthenticated user was able to write to adminOnly."
            )
        } catch (e: Exception) {
            val message = e.message.orEmpty()

            if (
                message.contains("PERMISSION_DENIED", ignoreCase = true) ||
                message.contains("permission", ignoreCase = true)
            ) {
                Result.success(
                    "PASS: Firestore denied the unauthenticated write."
                )
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun testCustomerAccess(): Result<String> {
        if (auth.currentUser == null) {
            return Result.failure(
                IllegalStateException(
                    "Sign in as a customer before testing customer access."
                )
            )
        }

        val adminResult = isCurrentUserAdmin()
        if (adminResult.isFailure) {
            return Result.failure(
                adminResult.exceptionOrNull()
                    ?: IllegalStateException("Unable to verify admin status.")
            )
        }

        if (adminResult.getOrDefault(false)) {
            return Result.failure(
                IllegalStateException(
                    "Sign in as a non-admin customer for this test."
                )
            )
        }

        return try {
            adminOnly.document("customer-test").set(
                mapOf("message" to "Customer access test")
            )

            Result.success(
                "FAIL: A non-admin customer was able to write to adminOnly."
            )
        } catch (e: Exception) {
            val message = e.message.orEmpty()

            if (
                message.contains("PERMISSION_DENIED", ignoreCase = true) ||
                message.contains("permission", ignoreCase = true)
            ) {
                Result.success(
                    "PASS: Firestore denied the non-admin customer's write."
                )
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun testAdminAccess(): Result<String> {
        val adminResult = isCurrentUserAdmin()

        if (adminResult.isFailure) {
            return Result.failure(
                adminResult.exceptionOrNull()
                    ?: IllegalStateException("Unable to verify admin status.")
            )
        }

        if (!adminResult.getOrDefault(false)) {
            return Result.failure(
                IllegalStateException(
                    "Sign in as an active administrator before testing admin access."
                )
            )
        }

        return try {
            adminOnly.document("admin-test").set(
                mapOf("message" to "Admin access test")
            )

            Result.success(
                "PASS: The active administrator successfully wrote to adminOnly."
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    }