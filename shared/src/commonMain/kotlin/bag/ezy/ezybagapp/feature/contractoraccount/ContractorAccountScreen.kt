package bag.ezy.ezybagapp.feature.contractoraccount

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
fun ContractorAccountScreen(
    onSignOut: () -> Unit,
    vm: ContractorAccountViewModel = viewModel {
        ContractorAccountViewModel()
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

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Button(
                        onClick = {
                            vm.load()
                        }
                    ) {
                        Text("Try again")
                    }
                }
            }

            state.contractor != null -> {
                ContractorAccountContent(
                    contractor = state.contractor,
                    onSignOut = onSignOut
                )
            }
        }
    }
}

@Composable
private fun ContractorAccountContent(
    contractor: Contractor,
    onSignOut: () -> Unit
) {
    val fullName = listOf(
        contractor.firstName,
        contractor.lastName
    )
        .filter { it.isNotBlank() }
        .joinToString(" ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 18.dp
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contractor.firstName
                            .firstOrNull()
                            ?.uppercase()
                            ?: "C",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fullName.ifBlank {
                        "Contractor"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = contractor.companyName.ifBlank {
                        "Company not provided"
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = if (contractor.isApproved) {
                        "Approved contractor"
                    } else {
                        "Application pending"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (contractor.isApproved) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
            }
        }

        Text(
            text = "Contact details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        AccountCard {
            ContractorDetail(
                label = "Name",
                value = fullName
            )

            ContractorDetail(
                label = "Email",
                value = contractor.email
            )

            ContractorDetail(
                label = "Phone",
                value = contractor.phone
            )
        }

        Text(
            text = "Business details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        AccountCard {
            ContractorDetail(
                label = "Company",
                value = contractor.companyName
            )

            ContractorDetail(
                label = "Service areas",
                value = contractor.serviceAreas
            )

            ContractorDetail(
                label = "Waste carrier licence",
                value = contractor.wasteCarrierLicenceDetails
            )
        }

        Text(
            text = "Insurance",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        AccountCard {
            ContractorDetail(
                label = "Insurance provider",
                value = contractor.insuranceProvider
            )

            ContractorDetail(
                label = "Policy number",
                value = contractor.policyNumber
            )
        }

        Text(
            text = "Vehicle",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        AccountCard {
            ContractorDetail(
                label = "Vehicle type",
                value = contractor.vehicleType
            )

            ContractorDetail(
                label = "Registration number",
                value = contractor.vehicleRegistrationNumber
            )
        }

        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text(
                text = "Sign out",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
private fun AccountCard(
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            content()
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