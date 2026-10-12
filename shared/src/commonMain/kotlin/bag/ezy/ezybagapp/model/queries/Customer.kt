package bag.ezy.ezybagapp.model.queries

import bag.ezy.ezybagapp.model.data.Customer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class CustomerRepository {
    private val firestore = Firebase.firestore
    private val customersCollection = firestore.collection("customers")

    // Create
    suspend fun createCustomer(customer: Customer): Result<Unit> {
        return try {
            customersCollection
                .document(customer.customerId)
                .set(Customer.serializer(), customer)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Read
    suspend fun getCustomer(customerId: String): Result<Customer?> {
        return try {
            val document = customersCollection
                .document(customerId)
                .get()
            val customer = document.data(Customer.serializer())
            Result.success(customer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update
    suspend fun updateCustomer(customerId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            customersCollection
                .document(customerId)
                .update(updates)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete
    suspend fun deleteCustomer(customerId: String): Result<Unit> {
        return try {
            customersCollection
                .document(customerId)
                .delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
