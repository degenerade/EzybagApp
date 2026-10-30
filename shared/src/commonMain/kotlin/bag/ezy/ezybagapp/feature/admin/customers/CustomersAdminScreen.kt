package bag.ezy.ezybagapp.feature.admin.customers

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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.di.AppContainer
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private fun formatCreatedDateTime(raw: String): String {
    if (raw.isBlank()) return ""
    val millis = raw.toLongOrNull()
    if (millis != null) {
        return runCatching {
            val instant = Instant.fromEpochMilliseconds(millis)
            val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val monthStr = months.getOrNull(dt.month.ordinal) ?: ""
            val minStr = dt.minute.toString().padStart(2, '0')
            val hourStr = dt.hour.toString().padStart(2, '0')
            "${dt.day} $monthStr ${dt.year}, $hourStr:$minStr"
        }.getOrDefault(raw)
    }
    val instant = runCatching { Instant.parse(raw) }.getOrNull()
    if (instant != null) {
        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val monthStr = months.getOrNull(dt.month.ordinal) ?: ""
        val minStr = dt.minute.toString().padStart(2, '0')
        val hourStr = dt.hour.toString().padStart(2, '0')
        return "${dt.day} $monthStr ${dt.year}, $hourStr:$minStr"
    }
    return raw
}

@Composable
fun CustomersAdminScreen(
    onBack: () -> Unit = {},
    vm: CustomersAdminViewModel = viewModel {
        CustomersAdminViewModel(
            AppContainer.customerRepository,
            AppContainer.adminRepository
        )
    }
) {
    LaunchedEffect(Unit) { vm.load() }

    CustomersScreenContent(
        state = vm.state,
        onRetry = vm::load,
        onBack = onBack
    )
}

@Composable
private fun CustomersScreenContent(
    state: CustomersAdminUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit = {},
) {
    val visible = state.customers
    val customerCountText =
        if (visible.size == 1) "1 customer" else "${visible.size} customers"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back to Dashboard")
        }
        Text(
            text = "Customers",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(20.dp))
        when {
            state.isLoading && state.customers.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = onRetry) {
                        Text("Try again")
                    }
                }
            }
            else -> {
                Text(
                    text = customerCountText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visible, key = { it.customerId.ifBlank { it.email } }) { customer ->
                        CustomerAdminCard(
                            customer = customer,
                            onClick = { }
                        )
                    }

                    if (visible.isEmpty()) {
                        item {
                            Text(
                                text = "No customers yet",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerAdminCard(
    customer: CustomersAdminUiModel,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = customer.fullName.ifBlank { customer.email },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (customer.email.isNotBlank()) {
                Text(
                    text = customer.email,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            if (customer.phone.isNotBlank()) {
                Text(
                    text = "Phone: ${customer.phone}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (customer.createdAt.isNotBlank()) {
                Text(
                    text = "Joined: ${formatCreatedDateTime(customer.createdAt)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
