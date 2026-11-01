
package bag.ezy.ezybagapp.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.data.Customer
import bag.ezy.ezybagapp.ui.components.SearchField
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Composable
fun RevenueScreen(
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var bookings by remember {
        mutableStateOf<List<Booking>>(emptyList())
    }

    var customers by remember {
        mutableStateOf<List<Customer>>(emptyList())
    }

    var searchQuery by remember { mutableStateOf("") }

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
                    onFailure = {
                        error = it.message ?: "Could not load revenue bookings."
                    }
                )

                customersResult.await().fold(
                    onSuccess = { customers = it },
                    onFailure = {
                        error = it.message ?: "Could not load customers."
                    }
                )
            }
        } catch (e: Exception) {
            error = e.message ?: "Something went wrong."
        } finally {
            isLoading = false
        }
    }

    val customerMap = customers.associateBy { it.customerId }

    val totalRevenue = bookings.sumOf {
        it.pricing.estimatedQuote
    }

    // Filter payments by customer name or booking ID
    val visibleBookings = bookings.filter { booking ->
        val customer = customerMap[booking.customerId]

        val customerName = listOfNotNull(
            customer?.firstName?.takeIf { it.isNotBlank() },
            customer?.lastName?.takeIf { it.isNotBlank() }
        ).joinToString(" ")

        booking.bookingId.contains(searchQuery.trim(), ignoreCase = true) ||
                customerName.contains(searchQuery.trim(), ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        // Back to Dashboard
        TextButton(
            onClick = onBack,
            contentPadding = PaddingValues(
                horizontal = 0.dp,
                vertical = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Back to Dashboard",
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Page title
        Text(
            text = "Revenue",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error ?: "Something went wrong.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {

                // Compact total revenue summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Revenue",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "£$totalRevenue",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search by customer name or booking ID
                SearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search customer name or booking ID…"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Successful payment count
                Text(
                    text = if (searchQuery.isBlank()) {
                        "Successful Payments (${bookings.size})"
                    } else {
                        "Search Results (${visibleBookings.size})"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (visibleBookings.isEmpty()) {
                    Text(
                        text = if (bookings.isEmpty()) {
                            "No successful payments found."
                        } else {
                            "No matching payments found."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = visibleBookings,
                            key = { it.bookingId }
                        ) { booking ->

                            val customer = customerMap[booking.customerId]

                            val customerName = listOfNotNull(
                                customer?.firstName?.takeIf { it.isNotBlank() },
                                customer?.lastName?.takeIf { it.isNotBlank() }
                            ).joinToString(" ").ifBlank {
                                "Unknown customer"
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = customerName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    Text(
                                        text = "Booking ID: ${booking.bookingId}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Amount",
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Text(
                                            text = "£${booking.pricing.estimatedQuote}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
