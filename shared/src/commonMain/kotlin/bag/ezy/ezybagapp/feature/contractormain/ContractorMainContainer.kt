package bag.ezy.ezybagapp.feature.contractormain

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.feature.contractoraccount.ContractorAccountScreen
import bag.ezy.ezybagapp.feature.contractorhome.ContractorHomeScreen
import bag.ezy.ezybagapp.feature.contractorjobs.ContractorJobsScreen
import bag.ezy.ezybagapp.feature.support.SupportScreen
import ezybagapp.shared.generated.resources.Res
import ezybagapp.shared.generated.resources.ic_support_agent
import org.jetbrains.compose.resources.vectorResource

enum class ContractorTab(
    val title: String,
    val icon: @Composable () -> ImageVector
) {
    Home("Home", { Icons.Default.Home }),
    Jobs("Jobs", { Icons.AutoMirrored.Filled.List }),
    Support("Support", { vectorResource(Res.drawable.ic_support_agent) }),
    Account("Account", { Icons.Default.Person })
}

@Composable
fun ContractorMainContainer(
    selectedTab: ContractorTab,
    isApproved: Boolean,
    onTabSelected: (ContractorTab) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                NavigationRail {
                    ContractorTab.entries.forEach { tab ->
                        val enabled =
                            tab != ContractorTab.Jobs || isApproved

                        NavigationRailItem(
                            selected = selectedTab == tab,
                            enabled = enabled,
                            onClick = {
                                if (enabled) {
                                    onTabSelected(tab)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon(),
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(tab.title)
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ContractorScreenContent(
                        selectedTab = selectedTab,
                        isApproved = isApproved,
                        onTabSelected = onTabSelected,
                        onSignOut = onSignOut
                    )
                }
            }
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        ContractorTab.entries.forEach { tab ->
                            val enabled =
                                tab != ContractorTab.Jobs || isApproved

                            NavigationBarItem(
                                selected = selectedTab == tab,
                                enabled = enabled,
                                onClick = {
                                    if (enabled) {
                                        onTabSelected(tab)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon(),
                                        contentDescription = tab.title
                                    )
                                },
                                label = {
                                    Text(tab.title)
                                }
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
                    ContractorScreenContent(
                        selectedTab = selectedTab,
                        isApproved = isApproved,
                        onTabSelected = onTabSelected,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}

@Composable
private fun ContractorScreenContent(
    selectedTab: ContractorTab,
    isApproved: Boolean,
    onTabSelected: (ContractorTab) -> Unit,
    onSignOut: () -> Unit
) {
    when (selectedTab) {
        ContractorTab.Home -> ContractorHomeScreen(
            onViewJobs = {
                if (isApproved) {
                    onTabSelected(
                        ContractorTab.Jobs
                    )
                }
            }
        )

        ContractorTab.Jobs -> {
            if (isApproved) {
                ContractorJobsScreen(
                    onOpenJob = { }
                )
            } else {
                ContractorHomeScreen(
                    onViewJobs = { }
                )
            }
        }

        ContractorTab.Support ->
            SupportScreen()

        ContractorTab.Account ->
            ContractorAccountScreen(
                onSignOut = onSignOut
            )
    }
}