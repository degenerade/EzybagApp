
package bag.ezy.ezybagapp.feature.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.time.Clock

private val issueCategories = listOf(
    "Booking",
    "Payment",
    "App problem",
    "Contractor",
    "Account",
    "Other"
)

@Serializable
private data class IssueReport(
    val customerId: String = "",
    val category: String = "",
    val description: String = "",
    val status: String = "Open",
    val createdAt: Long = 0L
)

private data class DisplayIssueReport(
    val id: String,
    val report: IssueReport
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen() {
    var selectedCategory by remember { mutableStateOf("Booking") }
    var description by remember { mutableStateOf("") }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    var reports by remember { mutableStateOf<List<DisplayIssueReport>>(emptyList()) }
    var loadingReports by remember { mutableStateOf(false) }
    var reportsError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    suspend fun loadReports() {
        val userId = Firebase.auth.currentUser?.uid

        if (userId.isNullOrBlank()) {
            reports = emptyList()
            reportsError = "Please sign in to view your reports."
            return
        }

        loadingReports = true
        reportsError = null

        try {
            val snapshot = Firebase.firestore
                .collection("issueReports")
                .where {
                    "customerId" equalTo userId
                }
                .get()

            reports = snapshot.documents.mapNotNull { document ->
                runCatching {
                    DisplayIssueReport(
                        id = document.id,
                        report = document.data<IssueReport>()
                    )
                }.getOrNull()
            }.sortedByDescending { it.report.createdAt }
        } catch (e: Exception) {
            reportsError = "Could not load your reports. Please try again."
        } finally {
            loadingReports = false
        }
    }

    LaunchedEffect(Unit) {
        loadReports()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Support",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Our team is here Monday to Saturday, 8am to 6pm.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CallUs(companyPhoneNumber = "+44 20 0000 0000")

            Spacer(modifier = Modifier.height(16.dp))

            EmailUs(companyEmail = "support@clearaway.example")

            Text(
                text = "We usually reply within one working day.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Report an issue form
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Report an Issue",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Tell us what went wrong. Your report will be sent to our support team.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Issue category",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Column {
                        OutlinedButton(
                            onClick = { categoryMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(9.dp)
                        ) {
                            Text(
                                text = selectedCategory,
                                modifier = Modifier.weight(1f)
                            )
                            Text("▼")
                        }

                        DropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = {
                                categoryMenuExpanded = false
                            }
                        ) {
                            issueCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        categoryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            message = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Describe the issue") },
                        placeholder = { Text("Explain what happened...") },
                        minLines = 4,
                        maxLines = 7,
                        shape = RoundedCornerShape(9.dp),
                        enabled = !submitting
                    )

                    message?.let { currentMessage ->
                        Text(
                            text = currentMessage,
                            color = if (isError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            val userId = Firebase.auth.currentUser?.uid
                            val issueDescription = description.trim()

                            when {
                                userId.isNullOrBlank() -> {
                                    isError = true
                                    message = "Please sign in before submitting a report."
                                }

                                issueDescription.length < 10 -> {
                                    isError = true
                                    message =
                                        "Please describe the issue in at least 10 characters."
                                }

                                issueDescription.length > 3000 -> {
                                    isError = true
                                    message =
                                        "The description cannot exceed 3,000 characters."
                                }

                                else -> {
                                    submitting = true
                                    message = null

                                    scope.launch {
                                        try {
                                            val reportId =
                                                "report-${Random.nextLong()}"

                                            Firebase.firestore
                                                .collection("issueReports")
                                                .document(reportId)
                                                .set(
                                                    mapOf(
                                                        "customerId" to userId,
                                                        "category" to selectedCategory,
                                                        "description" to issueDescription,
                                                        "status" to "Open",
                                                        "createdAt" to Clock.System
                                                            .now()
                                                            .toEpochMilliseconds()
                                                    )
                                                )

                                            description = ""
                                            isError = false
                                            message =
                                                "Your report was submitted successfully."

                                            // Refresh My Reports after submission.
                                            loadReports()
                                        } catch (e: Exception) {
                                            isError = true
                                            message =
                                                "Could not submit your report: " +
                                                        (e.message ?: "Please try again.")
                                        } finally {
                                            submitting = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(9.dp),
                        enabled = !submitting
                    ) {
                        Text(
                            text = if (submitting) "Submitting..." else "Submit report",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // My Reports
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Reports",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                OutlinedButton(
                    onClick = {
                        scope.launch { loadReports() }
                    },
                    enabled = !loadingReports
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh reports"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refresh")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (loadingReports && reports.isEmpty()) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
            } else if (reportsError != null) {
                Text(
                    text = reportsError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton(
                    onClick = {
                        scope.launch { loadReports() }
                    }
                ) {
                    Text("Try again")
                }
            } else if (reports.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "You haven't submitted any reports yet.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                reports.forEach { item ->
                    ReportCard(item = item)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (loadingReports) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReportCard(item: DisplayIssueReport) {
    val report = item.report

    val statusColor = when (report.status.lowercase()) {
        "resolved" -> MaterialTheme.colorScheme.primary
        "in progress" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = report.category.ifBlank { "Issue report" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Submitted: ${
                    if (report.createdAt > 0L) {
                        formatReportDate(report.createdAt)
                    } else {
                        "Date unavailable"
                    }
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Status:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = report.status,
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatReportDate(timestamp: Long): String {
    return try {
        val dateTime = Instant
            .fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val months = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val month = months[dateTime.monthNumber - 1]
        val year = dateTime.year
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')

        "$day $month $year, $hour:$minute"
    } catch (_: Exception) {
        "Date unavailable"
    }
}

@Composable
private fun CallUs(companyPhoneNumber: String) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.fillMaxWidth()) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone",
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Call us",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(text = companyPhoneNumber, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                runCatching {
                    uriHandler.openUri(
                        "tel:" + companyPhoneNumber.filterNot { it == ' ' }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text(
                text = "Call now",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmailUs(companyEmail: String) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.fillMaxWidth()) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Email us",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(text = companyEmail, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                runCatching {
                    uriHandler.openUri("mailto:$companyEmail")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text(
                text = "Send an email",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
