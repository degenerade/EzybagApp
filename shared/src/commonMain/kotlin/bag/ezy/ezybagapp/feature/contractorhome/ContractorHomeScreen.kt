package bag.ezy.ezybagapp.feature.contractorhome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ContractorHomeScreen(
    onViewJobs: () -> Unit = {},
    vm: ContractorHomeViewModel = viewModel {
        ContractorHomeViewModel()
    }
) {
    val state = vm.state

    LaunchedEffect(Unit) {
        vm.load()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.contractor == null -> {
                ContractorHomeError(
                    message = state.error
                        ?: "Contractor profile could not be found.",
                    onRetry = {
                        vm.load()
                    }
                )
            }

            !state.contractor.isApproved -> {
                PendingContractorHome(
                    firstName = state.contractor.firstName,
                    companyName = state.contractor.companyName
                )
            }

            else -> {
                ApprovedContractorHome(
                    state = state,
                    onFilterSelected = {
                        vm.selectFilter(it)
                    },
                    onDismissApprovalNotice = {
                        vm.dismissApprovalNotice()
                    },
                    onViewJobs = onViewJobs,
                    onRetry = {
                        vm.load()
                    }
                )
            }
        }
    }
}