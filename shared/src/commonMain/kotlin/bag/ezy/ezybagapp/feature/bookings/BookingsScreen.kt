package bag.ezy.ezybagapp.feature.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme

private data class BookingUiModel(
    val reference: String,
    val wasteType: String,
    val status: String,
    val date: String,
    val category: String
)

@Composable
fun BookingsScreen() {

    val selectedTab = remember {
        mutableStateOf("All")
    }

    val bookings = listOf(
        BookingUiModel(
            reference = "CC-10482",
            wasteType = "Mixed waste",
            status = "En route",
            date = "Fri 2 Oct",
            category = "Active"
        ),
        BookingUiModel(
            reference = "CC-10511",
            wasteType = "Garden waste",
            status = "Upcoming",
            date = "13 Oct",
            category = "Upcoming"
        ),
        BookingUiModel(
            reference = "CC-10274",
            wasteType = "Furniture",
            status = "Completed",
            date = "18 Sep",
            category = "Completed"
        )
    )

    val visibleBookings =
        if (selectedTab.value == "All") {
            bookings
        } else {
            bookings.filter {
                it.category == selectedTab.value
            }
        }

    val bookingCountText =
        if (visibleBookings.size == 1) {
            "1 booking"
        } else {
            "${visibleBookings.size} bookings"
        }

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

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        BookingTabs(
            selectedTab = selectedTab.value,
            onTabSelected = {
                selectedTab.value = it
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = bookingCountText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(visibleBookings) { booking ->
                BookingCard(booking)
            }

            item {
                CancelledCard()
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = { },
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
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {

    val tabs = listOf(
        "All",
        "Upcoming",
        "Active",
        "Completed"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        tabs.forEach { tab ->

            val isSelected = tab == selectedTab

            Text(
                text = tab,
                fontSize = 13.sp,
                fontWeight = if (isSelected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        },
                        shape = MaterialTheme.shapes.small
                    )
                    .clickable {
                        onTabSelected(tab)
                    }
                    .padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    )
            )
        }
    }
}

@Composable
private fun BookingCard(
    booking: BookingUiModel
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Text(
                text = booking.reference,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

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

            Spacer(
                modifier = Modifier.height(6.dp)
            )

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
private fun CancelledCard() {

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
                text = "No bookings",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
fun BookingsScreenPreview() {
    ClearawayTheme {
        BookingsScreen()
    }
}