
package bag.ezy.ezybagapp.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.repository.BookingReport
import kotlinx.coroutines.launch

@Composable
fun ReportsScreen(onBack: () -> Unit = {}) {

    var bookings by remember {
        mutableStateOf<List<Booking>>(emptyList())
    }

    var report by remember {
        mutableStateOf<BookingReport?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var generatedReport by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    fun loadBookings() {
        scope.launch {
            isLoading = true
            errorMessage = null

            val result = AppContainer.bookingRepository.getAllBookings()

            result.onSuccess {
                bookings = it
            }

            result.onFailure {
                errorMessage = it.message ?: "Could not load bookings."
            }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadBookings()
    }

    fun generateBookingReport() {
        report = AppContainer.reportRepository
            .generateBookingReport(bookings)

        generatedReport = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Back to Dashboard")
        }

        Text(
            "Reports & Analytics",
            style = MaterialTheme.typography.headlineMedium
        )

        HorizontalDivider()

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (errorMessage != null) {
            Text(
                errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )

            Button(onClick = { loadBookings() }) {
                Text("Try Again")
            }
        }

        // Show generated report
        if (generatedReport && report != null) {

            TextButton(
                onClick = {
                    generatedReport = false
                    report = null
                }
            ) {
                Text("← Back to Reports")
            }

            Text(
                "Booking Report",
                style = MaterialTheme.typography.headlineSmall
            )

            BookingStatistics(report!!)
        }

        // Show report categories when no report has been generated
        else if (!isLoading && errorMessage == null) {

            Text(
                "Available reports",
                style = MaterialTheme.typography.titleLarge
            )

            ReportCard(
                title = "Booking Report",
                description = "Booking activity and booking statuses.",
                buttonText = "Generate Report",
                enabled = true,
                onClick = {
                    generateBookingReport()
                }
            )

            ReportCard(
                title = "Contractor Performance",
                description = "Completed jobs and contractor ratings.",
                buttonText = "Coming soon",
                enabled = false,
                onClick = {}
            )

            ReportCard(
                title = "Customer Activity",
                description = "Customer registrations and booking activity.",
                buttonText = "Coming soon",
                enabled = false,
                onClick = {}
            )
        }
    }
}

@Composable
private fun ReportCard(
    title: String,
    description: String,
    buttonText: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
private fun BookingStatistics(report: BookingReport) {

    val maxValue = maxOf(
        report.completedBookings,
        report.activeBookings,
        report.cancelledBookings,
        report.unassignedBookings,
        1
    )

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Total Bookings",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                report.totalBookings.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            Text(
                "Booking Overview",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                "Number of bookings by status and assignment.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            BookingBar(
                title = "Completed",
                value = report.completedBookings,
                maxValue = maxValue
            )

            BookingBar(
                title = "Active / Upcoming",
                value = report.activeBookings,
                maxValue = maxValue
            )

            BookingBar(
                title = "Cancelled",
                value = report.cancelledBookings,
                maxValue = maxValue
            )

            BookingBar(
                title = "Unassigned",
                value = report.unassignedBookings,
                maxValue = maxValue
            )
        }
    }
}

@Composable
private fun BookingBar(
    title: String,
    value: Int,
    maxValue: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                value.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        value.toFloat() / maxValue.toFloat()
                    )
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}
