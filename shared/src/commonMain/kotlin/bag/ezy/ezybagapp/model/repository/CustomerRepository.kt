package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Customer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

interface CustomerRepository {
    suspend fun getCurrentCustomer(): Result<Customer?>
    suspend fun getAllCustomers(): Result<List<Customer>>
    suspend fun countCustomers(): Result<Int>
    suspend fun deleteCustomer(customerId: String = ""): Result<Unit>
}

class FirebaseCustomerRepository : CustomerRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val customers = firestore.collection("customers")

    override suspend fun getCurrentCustomer(): Result<Customer?> = runCatching {
        val user = auth.currentUser ?: return@runCatching null
        val document = customers.document(user.uid).get()
        if (!document.exists) null else document.data<Customer>()
    }


    override suspend fun getAllCustomers(): Result<List<Customer>> = runCatching {
        val snapshot = customers.get()
        snapshot.documents.mapNotNull { document ->
            runCatching { document.data<Customer>() }.getOrNull()
        }
    }

    // Count total customer count
    override suspend fun countCustomers(): Result<Int> = runCatching {
        val snapshot = customers.get()
        snapshot.documents.size
    }

    // Delete a customer record
    override suspend fun deleteCustomer(customerId: String): Result<Unit> = runCatching {
        require(customerId.isNotBlank()) { "Customer ID is required." }
        customers.document(customerId).delete()
    }
}
