
package bag.ezy.ezybagapp.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.ui.format.formatDisplayDateTime

@Composable
fun IssuesScreen(
    onBack: () -> Unit = {},
    vm: IssuesViewModel = viewModel { IssuesViewModel() }
) {
    val state = vm.state

    var showPastIssues by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadIssues()
    }

    val displayedIssues = if (showPastIssues) {
        state.issues.filter {
            it.report.status.equals("Resolved", ignoreCase = true)
        }
    } else {
        state.issues.filter {
            !it.report.status.equals("Resolved", ignoreCase = true)
        }
    }

    val pendingCount = state.issues.count {
        !it.report.status.equals("Resolved", ignoreCase = true)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Back to Dashboard")
            }

            Text(
                text = "Issues",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Manage customer reports and support requests.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Issue summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Unresolved issues",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = pendingCount.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Switch between pending and past issues
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!showPastIssues) {
                    Button(
                        onClick = { showPastIssues = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pending ($pendingCount)")
                    }
                } else {
                    OutlinedButton(
                        onClick = { showPastIssues = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pending ($pendingCount)")
                    }
                }

                if (showPastIssues) {
                    Button(
                        onClick = { showPastIssues = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View past issues")
                    }
                } else {
                    OutlinedButton(
                        onClick = { showPastIssues = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View past issues")
                    }
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )

                OutlinedButton(onClick = { vm.loadIssues() }) {
                    Text("Try again")
                }
            }

            if (!state.isLoading && state.error == null) {
                if (displayedIssues.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (showPastIssues) {
                                "There are no resolved issues yet."
                            } else {
                                "There are no unresolved issues."
                            },
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    displayedIssues.forEach { item ->
                        IssueCard(
                            item = item,
                            onMarkResolved = {
                                vm.changeIssueStatus(item.id, "Resolved")
                            },
                            showPastIssues = showPastIssues
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun IssueCard(
    item: bag.ezy.ezybagapp.model.repository.AdminIssue,
    onMarkResolved: () -> Unit,
    showPastIssues: Boolean
) {
    val report = item.report

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = report.category.ifBlank { "Issue report" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Issue ID: ${item.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Customer ID: ${report.customerId}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Submitted: ${formatDisplayDateTime(report.createdAt)
                    .ifBlank { "Date unavailable" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Status: ${report.status}",
                style = MaterialTheme.typography.labelLarge,
                color = if (report.status.equals("Resolved", ignoreCase = true)) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                fontWeight = FontWeight.Bold
            )

            if (!showPastIssues) {
                Button(
                    onClick = onMarkResolved,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mark as resolved")
                }
            }
        }
    }
}
