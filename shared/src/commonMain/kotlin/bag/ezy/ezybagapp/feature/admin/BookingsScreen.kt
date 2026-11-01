package bag.ezy.ezybagapp.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Booking

private val bookingStatuses = listOf(
    "All",
    "Pending",
    "Upcoming",
    "Active",
    "En Route",
    "On Site",
    "Completed",
    "Cancelled"
)

@Composable
fun BookingsScreen(onBack: () -> Unit = {}) {
    var bookings by remember {
        mutableStateOf<List<Booking>>(emptyList())
    }
    var selectedStatus by remember { mutableStateOf("All") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true

        val result = AppContainer.bookingRepository.getAllBookings()

        if (result.isSuccess) {
            bookings = result.getOrDefault(emptyList())
            errorMessage = null
        } else {
            errorMessage = result.exceptionOrNull()?.message
                ?: "Could not load bookings."
        }

        isLoading = false
    }

    val filteredBookings = if (selectedStatus == "All") {
        bookings
    } else {
        bookings.filter {
            it.status.equals(selectedStatus, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back to Dashboard")
        }

        // Green header matching the dashboard's visual style
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF1E6F5C),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Bookings",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Text(
                text = "Total bookings: ${bookings.size}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Status filters appear directly below the header.
            // The "Filter by status" label has been removed.
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    bookingStatuses.chunked(3).forEach { statusRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            statusRow.forEach { status ->
                                FilterChip(
                                    selected = selectedStatus == status,
                                    onClick = { selectedStatus = status },
                                    label = { Text(status) }
                                )
                            }
                        }
                    }
                }
            }

            when {
                isLoading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }

                errorMessage != null -> {
                    item {
                        Text(
                            text = "Error: $errorMessage",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                filteredBookings.isEmpty() -> {
                    item {
                        Text(
                            text = if (bookings.isEmpty()) {
                                "No bookings found."
                            } else {
                                "No bookings with status \"$selectedStatus\"."
                            },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                else -> {
                    items(
                        items = filteredBookings,
                        key = { it.bookingId }
                    ) { booking ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Booking ID: ${booking.bookingId}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text("Status: ${booking.status}")

                                Text(
                                    text = "Address: " +
                                            "${booking.collectionAddress.street}, " +
                                            booking.collectionAddress.postcode
                                )

                                Text(
                                    text = "Date: ${booking.schedule.preferredDate}"
                                )

                                Text(
                                    text = "Time: ${booking.schedule.preferredTime}"
                                )

                                Text(
                                    text = "Waste type: ${booking.wasteDetails.type}"
                                )

                                Text(
                                    text = "Estimated volume: " +
                                            booking.wasteDetails.estimatedVolume
                                )

                                if (booking.wasteDetails.description.isNotBlank()) {
                                    Text(
                                        text = "Description: " +
                                                booking.wasteDetails.description
                                    )
                                }

                                Text(
                                    text = "Bag required: " +
                                            if (booking.wasteDetails.bagRequired) {
                                                "Yes"
                                            } else {
                                                "No"
                                            }
                                )

                                if (booking.wasteDetails.bagRequired) {
                                    Text(
                                        text = "Bag size: " +
                                                (booking.wasteDetails.bagSize
                                                    ?: "Not specified")
                                    )
                                }

                                Text(
                                    text = "Estimated quote: " +
                                            "${booking.pricing.currency} " +
                                            booking.pricing.estimatedQuote
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}