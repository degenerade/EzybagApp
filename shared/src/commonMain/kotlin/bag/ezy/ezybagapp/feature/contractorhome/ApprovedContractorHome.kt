package bag.ezy.ezybagapp.feature.contractorhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ApprovedContractorHome(
    state: ContractorHomeUiState,
    onFilterSelected: (
        ContractorHomeJobFilter
    ) -> Unit,
    onDismissApprovalNotice: () -> Unit,
    onViewJobs: () -> Unit,
    onRetry: () -> Unit
) {
    val contractor = state.contractor
        ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hi ${
                contractor.firstName
                    .ifBlank { "there" }
            }",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        if (contractor.companyName.isNotBlank()) {
            Text(
                text = contractor.companyName,
                fontSize = 14.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (state.showApprovalNotice) {
            ApprovalNotice(
                onDismiss =
                    onDismissApprovalNotice
            )
        }

        Text(
            text = "Your jobs",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        JobFilterButtons(
            selectedFilter = state.filter,
            onFilterSelected =
                onFilterSelected
        )

        if (state.error != null) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {
                    Text(
                        text = state.error,
                        color =
                            MaterialTheme.colorScheme.error
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    OutlinedButton(
                        onClick = onRetry
                    ) {
                        Text("Try again")
                    }
                }
            }
        } else if (state.visibleJobs.isEmpty()) {
            EmptyJobsCard(
                filter = state.filter,
                onViewJobs = onViewJobs
            )
        } else {
            state.visibleJobs.forEach { job ->
                ContractorHomeJobCard(
                    job = job
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )
    }
}