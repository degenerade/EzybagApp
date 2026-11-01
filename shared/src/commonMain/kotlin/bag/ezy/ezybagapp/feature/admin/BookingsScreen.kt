
package bag.ezy.ezybagapp.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.ui.components.SearchField

private val bookingStatuses = listOf(
    "All statuses",
    "Pending",
    "Upcoming",
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

    var selectedStatus by remember {
        mutableStateOf("All statuses")
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    var statusMenuExpanded by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

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

    // Filter bookings by selected status and search query.
    val filteredBookings = bookings.filter { booking ->

        val matchesStatus =
            selectedStatus == "All statuses" ||
                    booking.status.equals(selectedStatus, ignoreCase = true)

        val matchesSearch =
            searchQuery.isBlank() ||
                    booking.bookingId.contains(searchQuery, ignoreCase = true) ||
                    booking.collectionAddress.street.contains(searchQuery, ignoreCase = true) ||
                    booking.collectionAddress.postcode.contains(searchQuery, ignoreCase = true) ||
                    booking.wasteDetails.type.contains(searchQuery, ignoreCase = true) ||
                    booking.status.contains(searchQuery, ignoreCase = true)

        matchesStatus && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
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
            text = "Bookings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Search field
        SearchField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search booking ID, address, waste type..."
        )

        // Status dropdown
        Box {
            OutlinedButton(
                onClick = { statusMenuExpanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = selectedStatus,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select booking status"
                )
            }

            DropdownMenu(
                expanded = statusMenuExpanded,
                onDismissRequest = {
                    statusMenuExpanded = false
                }
            ) {
                bookingStatuses.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            selectedStatus = status
                            statusMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Booking count with subtle yellow background
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .background(
                    color = Color(0xFFFFF3CD),
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (filteredBookings.size == 1) {
                    "1 booking"
                } else {
                    "${filteredBookings.size} bookings"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF795900)
            )
        }

        // Loading, error and booking list
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Something went wrong.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (filteredBookings.isEmpty()) {
                        item {
                            Text(
                                text = if (bookings.isEmpty()) {
                                    "No bookings found."
                                } else {
                                    "No bookings match your search or selected status."
                                },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    items(
                        items = filteredBookings,
                        key = { it.bookingId }
                    ) { booking ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    text = "Booking ID: ${booking.bookingId}",
                                    style = MaterialTheme.typography.titleMedium
                                )

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
                            }
                        }
                    }
                }
            }
        }
    }
}
