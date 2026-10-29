package bag.ezy.ezybagapp.feature.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.di.AppContainer

private val AdminGreen = Color(0xFF1E6F5C)
private val AdminBackground = Color(0xFFF5F7F6)

private enum class AdminSubScreen {
    Bookings,
    Customers,
    Contractors,
    Issues,
    Reports
}

@Composable
private fun AccountAvatar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        contentColor = AdminGreen,
        modifier = modifier.size(42.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Account",
                tint = AdminGreen
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
    LaunchedEffect(Unit) { vm.load() }

    var subScreen by remember { mutableStateOf<AdminSubScreen?>(null) }

    when (subScreen) {
        AdminSubScreen.Bookings -> BookingsScreen(onBack = { subScreen = null })
        AdminSubScreen.Customers -> CustomersScreen(onBack = { subScreen = null })
        AdminSubScreen.Contractors -> ContractorsScreen(onBack = { subScreen = null })
        AdminSubScreen.Issues -> IssuesScreen(onBack = { subScreen = null })
        AdminSubScreen.Reports -> ReportsScreen(onBack = { subScreen = null })
        null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AdminBackground)
                    .safeContentPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Dashboard header with user avatar in top-right corner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = AdminGreen,
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
                                color = Color.White,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Admin Page",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 15.sp
                            )
                        }

                        AccountAvatar(
                            onClick = onOpenAccount
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
                        value = "—",
                        onClick = { subScreen = AdminSubScreen.Bookings },
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Customers",
                        value = "—",
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
                        value = "—",
                        onClick = { subScreen = AdminSubScreen.Contractors },
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Revenue",
                        value = "—",
                        onClick = { subScreen = AdminSubScreen.Reports },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Issues card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Issues",
                        value = "—",
                        onClick = { subScreen = AdminSubScreen.Issues },
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

                // Customer complaints and applications that need approval
                Text(
                    text = "Pending actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = "Reported issues and pending applications will appear here.",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = value,
                color = AdminGreen,
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
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                color = AdminGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
