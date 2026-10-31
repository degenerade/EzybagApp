package bag.ezy.ezybagapp.feature.admin.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.feature.admin.BookingsScreen
import bag.ezy.ezybagapp.feature.admin.customers.CustomersAdminScreen
import bag.ezy.ezybagapp.feature.admin.IssuesScreen
import bag.ezy.ezybagapp.feature.admin.ReportsScreen
import bag.ezy.ezybagapp.feature.admin.contractors.ContractorsAdminScreen
import bag.ezy.ezybagapp.feature.admin.contractors.ContractorsTab
import bag.ezy.ezybagapp.feature.admin.Settings.SettingsScreen
import bag.ezy.ezybagapp.ui.format.formatPrice
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private enum class AdminSubScreen {
    Bookings,
    Customers,
    Contractors,
    Issues,
    Reports,
    Settings
}

@Composable
private fun AccountAvatar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = modifier.size(42.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Account",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AdminDashboardScreen(
    onOpenAccount: () -> Unit = {},
    vm: AdminDashboardViewModel = viewModel {
        AdminDashboardViewModel(AppContainer.authRepository)
    }
) {
    val state = vm.state

    var subScreen by remember { mutableStateOf<AdminSubScreen?>(null) }
    var contractorInitialTab by remember { mutableStateOf(ContractorsTab.All) }

    // Auto-refresh counts whenever returning to Dashboard and every 30 seconds
    LaunchedEffect(subScreen) {
        while (isActive) {
            if (subScreen == null) {
                vm.load()
            }
            delay(30_000L)
        }
    }

    when (subScreen) {
        AdminSubScreen.Bookings -> BookingsScreen(onBack = { subScreen = null })
        AdminSubScreen.Customers -> CustomersAdminScreen(onBack = { subScreen = null })
        AdminSubScreen.Contractors -> ContractorsAdminScreen(
            initialTab = contractorInitialTab,
            onBack = { subScreen = null }
        )
        AdminSubScreen.Issues -> IssuesScreen(
            onBack = { subScreen = null }
        )
        AdminSubScreen.Reports -> ReportsScreen(onBack = { subScreen = null })
        AdminSubScreen.Settings -> SettingsScreen(
            onSignOut = onOpenAccount,
            onBack = { subScreen = null }
        )
        null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .safeContentPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Dashboard header with user avatar in top-right corner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = state.userName.ifBlank { "Admin" },
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Admin Page",
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                fontSize = 15.sp
                            )
                        }

                        AccountAvatar(
                            onClick = { subScreen = AdminSubScreen.Settings }
                        )
                    }
                }

                // First row: bookings and customers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Bookings",
                        value = state.totalBookings?.toString() ?: "—",
                        onClick = { subScreen = AdminSubScreen.Bookings },
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Customers",
                        value = state.totalCustomers?.toString() ?: "—",
                        onClick = { subScreen = AdminSubScreen.Customers },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Second row: contractors and revenue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Contractors",
                        value = state.totalContractors?.toString() ?: "—",
                        onClick = {
                            contractorInitialTab = ContractorsTab.All
                            subScreen = AdminSubScreen.Contractors
                        },
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Revenue",
                        value = state.totalRevenue?.let { formatPrice(it) } ?: "—",
                        onClick = { subScreen = AdminSubScreen.Reports },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Reports navigation card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NavigationCard(
                        title = "Reports",
                        onClick = { subScreen = AdminSubScreen.Reports },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val unresolvedCount = state.totalUnresolvedIssues ?: 0
                val unverifiedCount = state.totalUnverifiedContractors ?: 0

                if (unresolvedCount > 0 || unverifiedCount > 0) {
                    Text(
                        text = "Pending actions",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Show unresolved issues only when there are issues to handle
                if (unresolvedCount > 0) {
                    val issueText = if (unresolvedCount == 1) {
                        "1 unresolved issue"
                    } else {
                        "Unresolved Issues ($unresolvedCount)"
                    }
                    PendingActionCard(
                        text = issueText,
                        onClick = { subScreen = AdminSubScreen.Issues }
                    )
                }

                // Show unverified contractors pending approval
                if (unverifiedCount > 0) {
                    val contractorText = if (unverifiedCount == 1) {
                        "1 contractor is waiting for approval"
                    } else {
                        "$unverifiedCount contractors are waiting for approval"
                    }
                    PendingActionCard(
                        text = contractorText,
                        onClick = {
                            contractorInitialTab = ContractorsTab.Pending
                            subScreen = AdminSubScreen.Contractors
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingActionCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier.Companion
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = value,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NavigationCard(
    title: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier.Companion
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
