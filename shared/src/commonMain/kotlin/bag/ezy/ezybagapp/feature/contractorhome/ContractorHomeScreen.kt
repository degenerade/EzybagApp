package bag.ezy.ezybagapp.feature.contractorhome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.model.data.Contractor

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

            state.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { vm.load() }
                    ) {
                        Text("Try again")
                    }
                }
            }

            state.contractor != null -> {
                ContractorHomeContent(
                    contractor = state.contractor,
                    onViewJobs = onViewJobs
                )
            }
        }
    }
}

@Composable
private fun ContractorHomeContent(
    contractor: Contractor,
    onViewJobs: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hi ${contractor.firstName.ifBlank { "there" }}",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = contractor.companyName.ifBlank {
                "Contractor account"
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp
        )

        ApprovalStatusCard(
            isApproved = contractor.isApproved
        )

        if (contractor.isApproved) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Available jobs",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "View collection jobs available in your service area.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onViewJobs,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View available jobs")
                    }
                }
            }
        } else {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                )
            ) {
                Text(
                    text = "Available jobs will become accessible once your contractor application has been approved.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }

        Text(
            text = "Account details",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ContractorDetail(
                    label = "Company",
                    value = contractor.companyName
                )

                ContractorDetail(
                    label = "Name",
                    value = listOf(
                        contractor.firstName,
                        contractor.lastName
                    ).filter {
                        it.isNotBlank()
                    }.joinToString(" ")
                )

                ContractorDetail(
                    label = "Email",
                    value = contractor.email
                )

                ContractorDetail(
                    label = "Phone",
                    value = contractor.phone
                )

                ContractorDetail(
                    label = "Service areas",
                    value = contractor.serviceAreas
                )

                ContractorDetail(
                    label = "Vehicle",
                    value = contractor.vehicleType
                )

                ContractorDetail(
                    label = "Vehicle registration",
                    value = contractor.vehicleRegistrationNumber
                )

                ContractorDetail(
                    label = "Waste carrier licence",
                    value = contractor.wasteCarrierLicenceDetails
                )

                ContractorDetail(
                    label = "Insurance provider",
                    value = contractor.insuranceProvider
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ApprovalStatusCard(
    isApproved: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isApproved) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Application status",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (isApproved) {
                    "Approved"
                } else {
                    "Pending approval"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isApproved) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (isApproved) {
                    "Your contractor account has been approved. You can now view and accept available jobs."
                } else {
                    "Your application is being reviewed. You can view your account details while you wait."
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ContractorDetail(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value.ifBlank {
                "Not provided"
            },
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}