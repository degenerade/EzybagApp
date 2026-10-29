package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Customer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

interface CustomerRepository {
    suspend fun getCustomers(): Result<List<Customer>>
    suspend fun countCustomers(): Result<Int>
    suspend fun deleteCustomer(customerId: String = ""): Result<Unit>
}

class FirebaseCustomerRepository : CustomerRepository {

    private val firestore = Firebase.firestore

    // Show each customer on page where viewing all customers
    override suspend fun getCustomers(): Result<List<Customer>> = runCatching {
        // TODO: Query all customer documents from Firestore
        emptyList()
    }

    // Count total customer count
    override suspend fun countCustomers(): Result<Int> = runCatching {
        // TODO: Get total customer count from Firestore
        0
    }

    // Delete a customer record
    override suspend fun deleteCustomer(customerId: String): Result<Unit> = runCatching {
        // TODO: Delete customer document from Firestore
    }
}
