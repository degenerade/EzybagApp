package bag.ezy.ezybagapp.feature.contractorhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ApprovalNotice(
    onDismiss: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 10.dp,
                    bottom = 10.dp,
                    end = 8.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    text = "✓ Account approved",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "You can now access available jobs.",
                    fontSize = 13.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = onDismiss
            ) {
                Text("Dismiss")
            }
        }
    }
}

@Composable
fun JobFilterButtons(
    selectedFilter: ContractorHomeJobFilter,
    onFilterSelected: (
        ContractorHomeJobFilter
    ) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {
        ContractorHomeJobFilter.entries
            .forEach { filter ->

                val selected =
                    selectedFilter == filter

                Button(
                    onClick = {
                        onFilterSelected(filter)
                    },
                    modifier =
                        Modifier.weight(1f),
                    contentPadding =
                        PaddingValues(
                            horizontal = 4.dp,
                            vertical = 8.dp
                        ),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                if (selected) {
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .primaryContainer
                                },
                            contentColor =
                                if (selected) {
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimary
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimaryContainer
                                }
                        ),
                    shape =
                        RoundedCornerShape(9.dp)
                ) {
                    Text(
                        text = filter.name,
                        fontSize = 11.sp,
                        fontWeight =
                            FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
    }
}

@Composable
fun ContractorHomeJobCard(
    job: ContractorHomeJobUiModel
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = job.wasteType
                        .ifBlank {
                            "Collection job"
                        },
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text = job.price,
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text = job.status,
                fontSize = 13.sp,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = job.dateTime.ifBlank {
                    "Schedule not provided"
                },
                fontSize = 13.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text = job.address.ifBlank {
                    "Address not provided"
                },
                fontSize = 13.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = job.id,
                fontSize = 11.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyJobsCard(
    filter: ContractorHomeJobFilter,
    onViewJobs: () -> Unit
) {
    val title = when (filter) {
        ContractorHomeJobFilter.Upcoming ->
            "No upcoming jobs"

        ContractorHomeJobFilter.Active ->
            "No active jobs"

        ContractorHomeJobFilter.Completed ->
            "No completed jobs"
    }

    val description = when (filter) {
        ContractorHomeJobFilter.Upcoming ->
            "Jobs assigned to you will appear here."

        ContractorHomeJobFilter.Active ->
            "Jobs currently in progress will appear here."

        ContractorHomeJobFilter.Completed ->
            "Finished jobs will appear here."
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(24.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (
                filter !=
                ContractorHomeJobFilter.Completed
            ) {
                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Button(
                    onClick = onViewJobs
                ) {
                    Text(
                        text = "Browse available jobs"
                    )
                }
            }
        }
    }
}

@Composable
fun ContractorHomeError(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text = message,
            color =
                MaterialTheme.colorScheme.error
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Button(
            onClick = onRetry
        ) {
            Text("Try again")
        }
    }
}