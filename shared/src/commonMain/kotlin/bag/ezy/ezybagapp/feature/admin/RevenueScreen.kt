
package bag.ezy.ezybagapp.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.data.Customer
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Composable
fun RevenueScreen(
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var customers by remember { mutableStateOf<List<Customer>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null

        try {
            coroutineScope {
                val bookingsResult = async {
                    AppContainer.revenueRepository.getSuccessfulBookings()
                }
                val customersResult = async {
                    AppContainer.customerRepository.getAllCustomers()
                }

                bookingsResult.await().fold(
                    onSuccess = { bookings = it },
                    onFailure = { error = it.message ?: "Could not load revenue bookings." }
                )

                customersResult.await().fold(
                    onSuccess = { customers = it },
                    onFailure = { error = it.message ?: "Could not load customers." }
                )
            }
        } catch (e: Exception) {
            error = e.message ?: "Something went wrong."
        } finally {
            isLoading = false
        }
    }

    val customerMap = customers.associateBy { it.customerId }
    val totalRevenue = bookings.sumOf { it.pricing.estimatedQuote }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Revenue Details",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(
                text = error ?: "An error occurred.",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Text(
                text = "Total Revenue: £${totalRevenue}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Successful Payments (${bookings.size})",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (bookings.isEmpty()) {
                Text("No successful payments found.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bookings) { booking ->
                        val customer = customerMap[booking.customerId]
                        val customerName = listOfNotNull(
                            customer?.firstName?.takeIf { it.isNotBlank() },
                            customer?.lastName?.takeIf { it.isNotBlank() }
                        ).joinToString(" ").ifBlank {
                            "Unknown customer"
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = customerName,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text("Booking ID: ${booking.bookingId}")

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Amount")
                                    Text("£${booking.pricing.estimatedQuote}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
