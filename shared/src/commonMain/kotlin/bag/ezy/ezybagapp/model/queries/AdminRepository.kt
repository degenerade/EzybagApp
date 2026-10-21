package bag.ezy.ezybagapp.model.queries

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class AdminRepository {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    private val adminCollection = firestore.collection("adminOnly")

    suspend fun testUnauthorizedAccess(): Result<String> {
        return try {
            auth.signOut()
            adminCollection.document("test_doc").set(mapOf("action" to "unauthorized_test"))
            Result.success("❌ Error: Unauthorized write succeeded!")
        } catch (e: Exception) {
            Result.success("✅ Success: Unauthorized access correctly blocked!\n(${e.message})")
        }
    }

    suspend fun testCustomerAccess(): Result<String> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not logged in. Log in as customer first.")
            adminCollection.document("test_doc").set(mapOf("uid" to user.uid, "role" to "customer"))
            Result.success("❌ Error: Customer write to admin collection succeeded!")
        } catch (e: Exception) {
            Result.success("✅ Success: Customer access correctly blocked!\n(${e.message})")
        }
    }

    suspend fun testAdminAccess(): Result<String> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not logged in.")
            adminCollection.document("admin_action").set(mapOf("adminUid" to user.uid, "action" to "admin_test"))
            Result.success("✅ Success: Admin access granted and write succeeded!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
