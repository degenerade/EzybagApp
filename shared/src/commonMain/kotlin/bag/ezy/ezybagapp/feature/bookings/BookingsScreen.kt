package bag.ezy.ezybagapp.feature.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme

@Composable
fun BookingsScreen(
    onBookAnother: () -> Unit,
    onBookingClick: (String) -> Unit,
    vm: BookingsViewModel = viewModel {
        BookingsViewModel(AppContainer.authRepository, AppContainer.bookingRepository)
    }
) {
    LaunchedEffect(Unit) { vm.load() }

    BookingsContent(
        state = vm.state,
        onTabSelected = vm::selectTab,
        onRetry = vm::load,
        onBookAnother = onBookAnother,
        onBookingClick = onBookingClick
    )
}

@Composable
fun BookingsContent(
    state: BookingsUiState,
    onTabSelected: (BookingTab) -> Unit,
    onRetry: () -> Unit,
    onBookAnother: () -> Unit,
    onBookingClick: (String) -> Unit
) {
    val visible = state.visibleBookings
    val bookingCountText =
        if (visible.size == 1) "1 booking" else "${visible.size} bookings"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            text = "Bookings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        BookingTabs(
            selectedTab = state.selectedTab,
            onTabSelected = onTabSelected
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            state.isLoading && state.bookings.isEmpty() -> {
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
                    text = bookingCountText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visible, key = { it.reference }) { booking ->
                        BookingCard(
                            booking = booking,
                            onClick = { onBookingClick(booking.reference) }
                        )
                    }

                    if (visible.isEmpty() && state.selectedTab.name != "All") {
                        item {
                            Text(
                                text = "No ${state.selectedTab.name.lowercase()} bookings",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (visible.isEmpty() && state.selectedTab.name == "All") {
                        item {
                            Text(
                                text = "Your bookings will appear here when you make them!",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (state.selectedTab == BookingTab.All && !visible.isEmpty()) {
                        item { CancelledCard(count = state.cancelledCount) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBookAnother,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Book another")
        }
    }
}

@Composable
private fun BookingTabs(
    selectedTab: BookingTab,
    onTabSelected: (BookingTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BookingTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab

            Text(
                text = tab.name,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent,
                        shape = MaterialTheme.shapes.small
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun BookingCard(
    booking: BookingUiModel,
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
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = booking.reference,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = booking.wasteType,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = booking.date,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = booking.status,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CancelledCard(count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Cancelled",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = when (count) {
                    0 -> "No bookings"
                    1 -> "1 booking"
                    else -> "$count bookings"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

private val previewBookings = listOf(
    BookingUiModel("CC-10482", "Mixed waste", "En route", "2 Oct", BookingTab.Active),
    BookingUiModel("CC-10511", "Garden waste", "Upcoming", "13 Oct", BookingTab.Upcoming),
    BookingUiModel("CC-10274", "Furniture", "Completed", "18 Sep", BookingTab.Completed),
    BookingUiModel("CC-10199", "Household", "Cancelled", "4 Sep", null)
)

@Preview
@Composable
private fun BookingsLoadedPreview() {
    ClearawayTheme {
        BookingsContent(
            BookingsUiState(isLoading = false, bookings = previewBookings),
            {}, {}, {}, {}
        )
    }
}

@Preview
@Composable
private fun BookingsCompletedTabPreview() {
    ClearawayTheme {
        BookingsContent(
            BookingsUiState(
                isLoading = false,
                bookings = previewBookings,
                selectedTab = BookingTab.Completed
            ),
            {}, {}, {}, {}
        )
    }
}

@Preview
@Composable
private fun BookingsLoadingPreview() {
    ClearawayTheme {
        BookingsContent(
            BookingsUiState(isLoading = false),
            {}, {}, {}, {}
        )
    }
}

@Preview
@Composable
private fun BookingsErrorPreview() {
    ClearawayTheme {
        BookingsContent(
            BookingsUiState(
                isLoading = false,
                error = "Couldn't load bookings"
            ),
            {}, {}, {}, {}
        )
    }
}
