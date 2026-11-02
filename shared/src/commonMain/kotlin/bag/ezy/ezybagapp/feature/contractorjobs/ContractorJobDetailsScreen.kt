


package bag.ezy.ezybagapp.feature.contractorjobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.model.data.Booking

@Composable
fun ContractorJobDetailsScreen(
    bookingId: String,
    onBack: () -> Unit,
    vm: ContractorJobDetailsViewModel =
        viewModel { ContractorJobDetailsViewModel() }
) {
    LaunchedEffect(bookingId) {
        vm.loadBooking(bookingId)
    }

    val state = vm.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Screen heading
        Text(
            text = "Job details",
            style = MaterialTheme.typography.headlineMedium
        )

        // Back button
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to jobs")
        }

        when {
            // Loading booking
            state.isLoading -> {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Booking loading error
            state.error != null -> {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                val booking = state.booking

                if (booking == null) {
                    Text("Booking details could not be found.")
                } else {

                    // Scrollable job details
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        JobDetailsContent(booking)
                    }

                    // Fixed bottom acceptance area
                    when {
                        state.acceptSuccess -> {
                            Text(
                                text = "Job accepted successfully. This job is now assigned to you.",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = "Status: Active",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        booking.contractorId != null -> {
                            Text(
                                text = "This job has already been assigned.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        booking.status == "Upcoming" && !booking.assigned -> {
                            Button(
                                onClick = {
                                    vm.acceptJob(bookingId)
                                },
                                enabled = !state.isAccepting,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (state.isAccepting) {
                                    CircularProgressIndicator()
                                } else {
                                    Text("Accept Job")
                                }
                            }

                            state.acceptError?.let { message ->
                                Text(
                                    text = message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        else -> {
                            Text(
                                text = "This job is not currently available for acceptance.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JobDetailsContent(booking: Booking) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        DetailSection(title = "Collection details") {
            DetailRow(
                "Address",
                listOf(
                    booking.collectionAddress.street,
                    booking.collectionAddress.city,
                    booking.collectionAddress.postcode
                ).filter { it.isNotBlank() }.joinToString(", ")
            )

            DetailRow(
                "Date",
                booking.schedule.preferredDate
            )

            DetailRow(
                "Time",
                booking.schedule.preferredTime
            )

            DetailRow(
                "Customer phone",
                booking.customerPhone
            )
        }

        DetailSection(title = "Waste details") {
            DetailRow(
                "Waste type",
                booking.wasteDetails.type
            )

            DetailRow(
                "Description",
                booking.wasteDetails.description
            )

            DetailRow(
                "Estimated volume",
                booking.wasteDetails.estimatedVolume
            )

            DetailRow(
                "Bag required",
                if (booking.wasteDetails.bagRequired) "Yes" else "No"
            )

            booking.wasteDetails.bagSize?.let {
                DetailRow("Bag size", it)
            }
        }

        DetailSection(title = "Access and instructions") {
            DetailRow(
                "Access information",
                booking.accessInfo
            )

            DetailRow(
                "Special instructions",
                booking.specialInstructions
            )
        }

        DetailSection(title = "Pricing") {
            DetailRow(
                "Estimated quote",
                "${booking.pricing.currency} ${booking.pricing.estimatedQuote}"
            )

            DetailRow(
                "Final quote requested",
                if (booking.pricing.finalQuoteRequested) "Yes" else "No"
            )
        }

        DetailSection(title = "Customer photos") {
            if (booking.media.photos.isEmpty()) {
                Text("No customer photos have been added.")
            } else {
                Text(
                    "${booking.media.photos.size} photo reference(s) available. " +
                            "Photo display will be added with the upload feature."
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    if (value.isNotBlank()) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
