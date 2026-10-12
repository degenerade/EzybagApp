package bag.ezy.ezybagapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import bag.ezy.ezybagapp.model.data.Customer
import bag.ezy.ezybagapp.model.queries.CustomerRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var firebaseStatus by remember { mutableStateOf("Firebase (GitLive KMP): Ready") }
            var operationResult by remember { mutableStateOf("Tap a button below to test CRUD") }
            val repository = remember { CustomerRepository() }
            val coroutineScope = rememberCoroutineScope()

            App(
                firebaseStatus = firebaseStatus,
                operationResult = operationResult,
                onCreateCustomer = {
                    coroutineScope.launch {
                        operationResult = "Creating customer..."
                        val newCustomer = Customer(
                            customerId = "cust_001",
                            fullName = "John Doe",
                            email = "john.doe@example.com",
                            phone = "+44123456789",
                            createdAt = "2025-02-25"
                        )
                        repository.createCustomer(newCustomer)
                            .onSuccess { operationResult = "Created customer: cust_001 in commonMain" }
                            .onFailure { e -> operationResult = "Create failed: ${e.message}" }
                    }
                },
                onReadCustomer = {
                    coroutineScope.launch {
                        operationResult = "Reading customer..."
                        repository.getCustomer("cust_001")
                            .onSuccess { customer ->
                                operationResult = if (customer != null) {
                                    "Read success: ${customer.fullName} (${customer.email})"
                                } else {
                                    "Customer not found!"
                                }
                            }
                            .onFailure { e -> operationResult = "Read failed: ${e.message}" }
                    }
                },
                onUpdateCustomer = {
                    coroutineScope.launch {
                        operationResult = "Updating customer..."
                        val updates = mapOf<String, Any>(
                            "fullName" to "John Updated Doe",
                            "phone" to "+44987654321"
                        )
                        repository.updateCustomer("cust_001", updates)
                            .onSuccess { operationResult = "Updated customer cust_001 successfully" }
                            .onFailure { e -> operationResult = "Update failed: ${e.message}" }
                    }
                },
                onDeleteCustomer = {
                    coroutineScope.launch {
                        operationResult = "Deleting customer..."
                        repository.deleteCustomer("cust_001")
                            .onSuccess { operationResult = "Deleted customer cust_001 successfully" }
                            .onFailure { e -> operationResult = "Delete failed: ${e.message}" }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
