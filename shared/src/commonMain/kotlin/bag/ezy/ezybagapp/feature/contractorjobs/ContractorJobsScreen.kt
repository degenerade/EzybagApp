package bag.ezy.ezybagapp.feature.contractorjobs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ContractorJobsScreen(
    onOpenJob: (String) -> Unit = {},
    vm: ContractorJobsViewModel = viewModel { ContractorJobsViewModel() }
) {
    ContractorJobsContent(
        state = vm.state,
        onOpenJob = onOpenJob,
        onRefresh = vm::loadJobs,
        onCompleteJob = vm::completeJob
    )
}

@Composable
fun ContractorJobsContent(
    state: ContractorJobsUiState,
    onOpenJob: (String) -> Unit,
    onRefresh: () -> Unit,
    onCompleteJob: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Available") }
    var jobToComplete by remember { mutableStateOf<ContractorJobUiModel?>(null) }

    val displayedJobs = when (selectedTab) {
        "Active" -> state.activeJobs
        "Completed" -> state.completedJobs
        else -> state.jobs
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            text = "My jobs",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (selectedTab) {
                "Active" -> "${state.activeJobs.size} active jobs"
                "Completed" -> "${state.completedJobs.size} completed jobs"
                else -> "${state.jobs.size} available opportunities"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Available", "Active", "Completed").forEach { tab ->
                if (selectedTab == tab) {
                    Button(
                        onClick = { selectedTab = tab },
                        modifier = Modifier.weight(1f)
                    ) { Text(tab) }
                } else {
                    OutlinedButton(
                        onClick = { selectedTab = tab },
                        modifier = Modifier.weight(1f)
                    ) { Text(tab) }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRefresh,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && state.completingBookingId == null &&
                    state.updatingBookingId == null
        ) { Text("Refresh jobs") }

        state.completionError?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(12.dp))
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            state.error != null -> {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onRefresh) { Text("Try again") }
                }
            }
            displayedJobs.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (selectedTab) {
                            "Active" -> "You have no active jobs."
                            "Completed" -> "You have no completed jobs yet."
                            else -> "No available jobs at the moment.\nPlease check again later."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedJobs, key = { it.reference }) { job ->
                        JobCard(
                            job = job,
                            showActions = selectedTab == "Active",
                            isBusy = state.updatingBookingId == job.reference ||
                                    state.completingBookingId == job.reference,
                            onClick = { onOpenJob(job.reference) },
                            onComplete = { jobToComplete = job }
                        )
                    }
                }
            }
        }
    }

    jobToComplete?.let { job ->
        AlertDialog(
            onDismissRequest = {
                if (state.completingBookingId == null) jobToComplete = null
            },
            title = { Text("Complete this job?") },
            text = {
                Text(
                    "Are you sure you have finished job ${job.reference}? " +
                            "It will move to your Completed jobs."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = state.completingBookingId == null,
                    onClick = {
                        onCompleteJob(job.reference)
                        jobToComplete = null
                    }
                ) { Text("Yes, complete job") }
            },
            dismissButton = {
                TextButton(
                    enabled = state.completingBookingId == null,
                    onClick = { jobToComplete = null }
                ) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun JobCard(
    job: ContractorJobUiModel,
    showActions: Boolean,
    isBusy: Boolean,
    onClick: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = job.reference,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = job.price,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = job.wasteType,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = job.time, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = job.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "View job details →",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            if (showActions) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onComplete,
                    enabled = !isBusy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isBusy) CircularProgressIndicator()
                    else Text("Mark as Completed")
                }
            }
        }
    }
}

