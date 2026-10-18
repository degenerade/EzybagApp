package bag.ezy.ezybagapp.dev

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.Screen

const val DEV_MENU_ENABLED = true

private val devGroups: List<Pair<String, List<Pair<Screen, String>>>> = listOf(
    "Auth" to listOf(
        Screen.SignIn to "Sign in",
        Screen.SignUp to "Sign up"
    ),
    "Customer" to listOf(
        Screen.Bookings to "Bookings",
        Screen.NewBookingDetails to "New booking - 1 details",
        Screen.NewBookingSchedule to "New booking - 2 schedule",
        Screen.Ratings to "Ratings"
    ),
    "Admin" to listOf(
        Screen.AdminDashboard to "Admin Dashboard"
    )
)

@Composable
fun DevMenu(onOpen: (Screen) -> Unit) {
    val listed = devGroups.flatMap { (_, items) -> items.map { it.first } }.toSet()
    val others = Screen.entries.filter { it != Screen.DevMenu && it !in listed }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Dev menu", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Jump to any screen. Tap DEV in the corner to come back.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        devGroups.forEach { (group, items) ->
            DevGroup(group, items, onOpen)
        }
        if (others.isNotEmpty()) {
            DevGroup("Other", others.map { it to it.name }, onOpen)
        }
    }
}

@Composable
private fun DevGroup(title: String, items: List<Pair<Screen, String>>, onOpen: (Screen) -> Unit) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 12.dp)
    )
    items.forEach { (screen, label) ->
        OutlinedButton(onClick = { onOpen(screen) }, modifier = Modifier.fillMaxWidth()) {
            Text(label)
        }
    }
}

@Composable
fun DevMenuButton(onClick: () -> Unit, modifier: Modifier) {
    SmallFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        Text("DEV", style = MaterialTheme.typography.labelSmall)
    }
}