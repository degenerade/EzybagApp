package bag.ezy.ezybagapp.feature.contractorjobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme

@Composable
fun ContractorJobsScreen(
    onOpenJob: (String) -> Unit = {},
    vm: ContractorJobsViewModel = viewModel {
        ContractorJobsViewModel()
    }
) {
    ContractorJobsContent(
        state = vm.state,
        onOpenJob = onOpenJob
    )
}

@Composable
fun ContractorJobsContent(
    state: ContractorJobsUiState,
    onOpenJob: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {

        Text(
            text = "Available jobs",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Within ${state.radiusMiles} miles",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "${state.nearbyCount} nearby opportunities",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "List / map · ${state.area} · all waste types",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        MapPlaceholder(
            radiusMiles = state.radiusMiles
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                state.jobs.forEach { job ->
                    JobRow(job)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                state.nearestJob?.let {
                    onOpenJob(it.reference)
                }
            }
        ) {
            Text("Open nearest job")
        }
    }
}

@Composable
private fun MapPlaceholder(
    radiusMiles: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Map / route preview",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = "Live route · $radiusMiles min away",
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun JobRow(
    job: ContractorJobUiModel
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${job.reference} · ${job.time}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = job.price,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "${job.wasteType} · ${job.distanceMiles} mi",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private val previewState = ContractorJobsUiState(
    radiusMiles = 18,
    nearbyCount = 5,
    area = "South London",
    jobs = listOf(
        ContractorJobUiModel(
            "CC-10518",
            "Today 16:00",
            "Furniture",
            6.1,
            "£88.40"
        ),
        ContractorJobUiModel(
            "CC-10522",
            "Tomorrow 09:00",
            "Construction",
            10.4,
            "£144.70"
        )
    )
)

@Preview
@Composable
private fun ContractorJobsPreview() {
    ClearawayTheme {
        ContractorJobsContent(
            state = previewState,
            onOpenJob = {}
        )
    }
}