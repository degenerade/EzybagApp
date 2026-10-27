package bag.ezy.ezybagapp.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.feature.account.AccountScreen
import bag.ezy.ezybagapp.feature.bookings.BookingsScreen
import bag.ezy.ezybagapp.feature.home.HomeScreen
import bag.ezy.ezybagapp.feature.support.SupportScreen
import ezybagapp.shared.generated.resources.Res
import org.jetbrains.compose.resources.vectorResource
import ezybagapp.shared.generated.resources.ic_support_agent

enum class MainTab(
    val title: String,
    val icon: @Composable () -> ImageVector
) {
    Home("Home", { Icons.Default.Home }),
    Bookings("Bookings", { Icons.AutoMirrored.Filled.List }),
    Support("Support", { vectorResource(Res.drawable.ic_support_agent) }),
    Account("Account", { Icons.Default.Person })
}

@Composable
fun MainContainer(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    onStartBooking: (wasteType: String?) -> Unit,
    onOpenBooking: (bookingId: String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail {
                    MainTab.entries.forEach { tab ->
                        NavigationRailItem(
                            selected = selectedTab == tab,
                            onClick = { onTabSelected(tab) },
                            icon = {
                                Icon(
                                    imageVector = tab.icon(),
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(text = tab.title) }
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    ScreenContent(
                        selectedTab = selectedTab,
                        onStartBooking = onStartBooking,
                        onOpenBooking = onOpenBooking,
                        onSignOut = onSignOut
                    )
                }
            }
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        MainTab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = selectedTab == tab,
                                onClick = { onTabSelected(tab) },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon(),
                                        contentDescription = tab.title
                                    )
                                },
                                label = { Text(text = tab.title) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenContent(
                        selectedTab = selectedTab,
                        onStartBooking = onStartBooking,
                        onOpenBooking = onOpenBooking,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    selectedTab: MainTab,
    onStartBooking: (String?) -> Unit,
    onOpenBooking: (String) -> Unit,
    onSignOut: () -> Unit,
) {
    when (selectedTab) {
        MainTab.Home -> HomeScreen(onStartBooking = onStartBooking, onOpenBooking = onOpenBooking)
        MainTab.Bookings -> BookingsScreen(
            onBookAnother = {onStartBooking(null) },
            onBookingClick = onOpenBooking)
        MainTab.Support -> SupportScreen()
        MainTab.Account -> AccountScreen(onSignOut = onSignOut)
    }
}