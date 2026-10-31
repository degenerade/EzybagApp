package bag.ezy.ezybagapp.feature.admin.contractors.contractordetailsadmin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Contractor
import bag.ezy.ezybagapp.ui.theme.EzybagTheme

@Composable
fun ContractorDetailsAdminScreen(
    contractorId: String,
    onBack: () -> Unit,
    vm: ContractorDetailsAdminViewModel = viewModel(key = "contractor-$contractorId") {
        ContractorDetailsAdminViewModel(
            contractorId,
            AppContainer.contractorRepository,
            AppContainer.bookingRepository,
            AppContainer.adminRepository,
        )
    },
) {
    ContractorDetailsAdminContent(
        state = vm.state,
        onBack = onBack,
        onRetry = vm::load,
        onToggleApproval = vm::toggleApproval,
    )
}

@Composable
fun ContractorDetailsAdminContent(
    state: ContractorDetailsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggleApproval: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val contractor = state.contractor

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Back to Contractors") }

        when {
            state.isLoading && contractor == null -> Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            contractor == null -> Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(state.error ?: "Contractor not found", color = MaterialTheme.colorScheme.error)
                TextButton(onClick = onRetry) { Text("Try again") }
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ContractorHeader(contractor)

                StatsRow(state)

                Section("Contact") {
                    InfoRow("Name", contractor.fullName())
                    InfoRow(
                        label = "Email",
                        value = contractor.email,
                        onClick = if (contractor.email.isBlank()) null else {
                            { runCatching { uriHandler.openUri("mailto:${contractor.email}") } }
                        },
                    )
                    InfoRow(
                        label = "Phone",
                        value = contractor.phone,
                        onClick = if (contractor.phone.isBlank()) null else {
                            { runCatching { uriHandler.openUri("tel:" + contractor.phone.filterNot { it == ' ' }) } }
                        },
                    )
                }

                Section("Business") {
                    InfoRow("Company", contractor.companyName)
                    InfoRow("Service areas", contractor.serviceAreas)
                    // TODO: confirm the unit with whoever built contractor sign-up
                    InfoRow("Service radius", "${contractor.serviceRadius.toInt()} miles")
                }

                Section("Vehicle") {
                    InfoRow("Type", contractor.vehicleType)
                    InfoRow("Registration", contractor.vehicleRegistrationNumber)
                }

                Section("Compliance") {
                    InfoRow("Waste carrier licence", contractor.wasteCarrierLicenceDetails)
                    InfoRow("Insurance provider", contractor.insuranceProvider)
                    InfoRow("Policy number", contractor.policyNumber)
                    DocumentRow("Proof of insurance", contractor.proofOfInsuranceUrl) { url ->
                        runCatching { uriHandler.openUri(url) }
                    }
                    DocumentRow("Proof of identity", contractor.proofOfIdentityUrl) { url ->
                        runCatching { uriHandler.openUri(url) }
                    }
                }

                state.actionError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = onToggleApproval,
                    enabled = !state.isUpdating,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (contractor.isApproved) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.primary
                    ),
                ) {
                    if (state.isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            if (contractor.isApproved) "Unapprove contractor" else "Approve contractor",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private fun Contractor.fullName(): String = "$firstName $lastName".trim()

@Composable
private fun ContractorHeader(contractor: Contractor) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = contractor.companyName.ifBlank { contractor.fullName() }.ifBlank { contractor.email },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        if (contractor.companyName.isNotBlank() && contractor.fullName().isNotBlank()) {
            Text(contractor.fullName(), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(
                text = if (contractor.isApproved) "Approved" else "Pending approval",
                highlighted = contractor.isApproved,
            )
            StatusChip(text = contractor.status, highlighted = contractor.status == "Online")
        }
        Text(
            text = "ID: ${contractor.uid}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatusChip(text: String, highlighted: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (highlighted) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (highlighted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
@Composable
private fun StatsRow(state: ContractorDetailsUiState) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatTile("Jobs", state.totalJobs.toString(), Modifier.weight(1f))
        StatTile("Active", state.activeJobs.toString(), Modifier.weight(1f))
        StatTile("Completed", "${state.completedJobs} (${state.completionRate}%)", Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value.ifBlank { "—" },
            fontWeight = FontWeight.Medium,
            color = if (onClick != null) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun DocumentRow(label: String, url: String?, onOpen: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (url.isNullOrBlank()) {
            Text("Not uploaded", color = MaterialTheme.colorScheme.error)
        } else {
            TextButton(onClick = { onOpen(url) }) { Text("View") }
        }
    }
}

private val previewContractor = Contractor(
    uid = "abc123",
    firstName = "Jake", lastName = "Rowe",
    email = "jake@dropwaste.example", phone = "+44 7700 900123",
    companyName = "Drop Waste Ltd",
    serviceAreas = "Truro, Falmouth, Redruth",
    vehicleType = "Luton van", vehicleRegistrationNumber = "WK21 ABC",
    wasteCarrierLicenceDetails = "CBDU123456",
    insuranceProvider = "Hiscox", policyNumber = "HX-99812",
    isApproved = false, status = "Offline",
)

@Preview
@Composable
private fun ContractorDetailsPreview() {
    EzybagTheme {
        ContractorDetailsAdminContent(
            state = ContractorDetailsUiState(
                isLoading = false, contractor = previewContractor,
                totalJobs = 12, activeJobs = 1, completedJobs = 10,
            ),
            onBack = {}, onRetry = {}, onToggleApproval = {},
        )
    }
}

@Preview
@Composable
private fun ContractorDetailsNotFoundPreview() {
    EzybagTheme {
        ContractorDetailsAdminContent(
            state = ContractorDetailsUiState(isLoading = false, error = "Contractor not found"),
            onBack = {}, onRetry = {}, onToggleApproval = {},
        )
    }
}