package bag.ezy.ezybagapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
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

enum class MainTab(
    val title: String,
    val icon: ImageVector
) {
    Home("Home", Icons.Default.Home),
    CreatePickup("Book", Icons.Default.AddCircle),
    Support("Support", Icons.Default.SupportAgent),
    Account("Account", Icons.Default.Person)
}

@Composable
fun MainContainer(
    modifier: Modifier = Modifier,
    initialTab: MainTab = MainTab.Home
) {
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail {
                    MainTab.entries.forEach { tab ->
                        NavigationRailItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(text = tab.title) }
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    ScreenContent(selectedTab)
                }
            }
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        MainTab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
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
                    ScreenContent(selectedTab)
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(selectedTab: MainTab) {
    when (selectedTab) {
        MainTab.Home -> HomeScreen()
        MainTab.CreatePickup -> CreatePickupScreen()
        MainTab.Support -> SupportScreen()
        MainTab.Account -> AccountScreen()
    }
}
