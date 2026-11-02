package bag.ezy.ezybagapp.feature.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.ui.format.formatDisplayDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    vm: SupportViewModel = viewModel { SupportViewModel() }
) {
    val state = vm.state

    LaunchedEffect(Unit) {
        vm.loadReports()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Support",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Our team is here Monday to Saturday, 8am to 6pm.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            CallUs(companyPhoneNumber = "+44 20 0000 0000")

            Spacer(modifier = Modifier.height(16.dp))

            EmailUs(companyEmail = "support@ezybag.example")

            Text(
                text = "We usually reply within one working day.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            onClick = { vm.setCategoryMenuExpanded(true) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(9.dp)
                        ) {
                            Text(
                                text = state.selectedCategory,
                                modifier = Modifier.weight(1f)
                            )
                            Text("▼")
                        }

                        DropdownMenu(
                            expanded = state.categoryMenuExpanded,
                            onDismissRequest = {
                                vm.setCategoryMenuExpanded(false)
                            }
                        ) {
                            issueCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        vm.onCategorySelected(category)
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = vm::onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Describe the issue or details") },
                        placeholder = { Text("Explain what happened...") },
                        minLines = 4,
                        maxLines = 7,
                        shape = RoundedCornerShape(9.dp),
                        enabled = !state.submitting
                    )

                    state.message?.let { currentMessage ->
                        Text(
                            text = currentMessage,
                            color = if (state.isError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = vm::submitReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(9.dp),
                        enabled = !state.submitting
                    ) {
                        Text(
                            text = if (state.submitting) "Submitting..." else "Submit",
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
                    text = "My Issues",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                OutlinedButton(
                    onClick = vm::loadReports,
                    enabled = !state.loadingReports
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh issues"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refresh")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.loadingReports && state.reports.isEmpty()) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
            } else if (state.reportsError != null) {
                Text(
                    text = state.reportsError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton(
                    onClick = vm::loadReports
                ) {
                    Text("Try again")
                }
            } else if (state.reports.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "You haven't submitted any issues yet.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                state.reports.forEach { item ->
                    ReportCard(item = item)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.loadingReports) {
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
                    formatDisplayDateTime(report.createdAt)
                        .ifBlank { "Date unavailable" }
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
