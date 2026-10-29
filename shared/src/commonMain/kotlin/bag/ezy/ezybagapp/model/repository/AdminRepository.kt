package bag.ezy.ezybagapp.model.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

interface AdminRepository {
    suspend fun changeCustomerToAdmin(userId: String = ""): Result<Unit>
    suspend fun changeAdminToCustomer(adminId: String = ""): Result<Unit>
    suspend fun deleteAdmin(adminId: String = ""): Result<Unit>
}

class FirebaseAdminRepository : AdminRepository {

    private val firestore = Firebase.firestore

    override suspend fun changeCustomerToAdmin(userId: String): Result<Unit> = runCatching {
        // TODO: Update user role to 'admin' in Firestore
    }

    override suspend fun changeAdminToCustomer(adminId: String): Result<Unit> = runCatching {
        // TODO: Update user role to 'customer' in Firestore
    }

    override suspend fun deleteAdmin(adminId: String): Result<Unit> = runCatching {
        // TODO: Remove admin user document from Firestore
    }
}
