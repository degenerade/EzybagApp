package bag.ezy.ezybagapp.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bag.ezy.ezybagapp.Booking
import ezybagapp.shared.generated.resources.Res
import ezybagapp.shared.generated.resources.ic_bungalow
import ezybagapp.shared.generated.resources.ic_calendar_month
import ezybagapp.shared.generated.resources.ic_construction
import ezybagapp.shared.generated.resources.ic_weekend
import ezybagapp.shared.generated.resources.ic_grass
import ezybagapp.shared.generated.resources.no_completed_booking
import ezybagapp.shared.generated.resources.no_upcoming_booking
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

enum class BookingTab { Upcoming, Completed }

@Composable
fun HomeScreen(
    onStartBooking: (wasteType: String?) -> Unit = {},
    onOpenBooking: (bookingId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(BookingTab.Upcoming) }
    var upcomingBookings by remember {
        mutableStateOf(
            listOf(
                Booking(
                    id = "1",
                    jobStatus = "On site",
                    wasteType = "Construction waste",
                    estimatedVolume = "Full van",
                    address = "12 Elm Road, M1 4BT",
                    date = "Thu 8 Oct",
                    time = "9am-12pm",
                    price = "£145"
                )
            )
        )
    }
    var completedBookings by remember { mutableStateOf(emptyList<Booking>()) }

    Scaffold(modifier = modifier.safeDrawingPadding()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            WelcomeMessage(userName = "John")
            DashBoard()
            Spacer(modifier = Modifier.height(12.dp))
            WasteTypeRow()
            Spacer(modifier = Modifier.height(16.dp))

            BookingsDisplayColumn(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                upcomingBookings = upcomingBookings,
                completedBookings = completedBookings,
                onBookingClick = { onOpenBooking(it.id) }
            )
        }
    }
}

@Composable
private fun BookingsDisplayColumn(
    selectedTab: BookingTab,
    onTabSelected: (BookingTab) -> Unit,
    upcomingBookings: List<Booking>,
    completedBookings: List<Booking>,
    onBookingClick: (Booking) -> Unit = {},
    modifier: Modifier = Modifier.Companion
) {
    Column(modifier = modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
        BookingsButtonsRow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )
        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            BookingTab.Upcoming -> {
                if (upcomingBookings.isEmpty()) {
                    EmptyUpcomingBooking()
                } else {
                    upcomingBookings.forEach { booking ->
                        BookingItem(
                            booking = booking,
                            onClick = { onBookingClick(booking) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            BookingTab.Completed -> {
                if (completedBookings.isEmpty()) {
                    EmptyCompletedBooking()
                } else {
                    completedBookings.forEach { booking ->
                        BookingItem(
                            booking = booking,
                            onClick = { onBookingClick(booking) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountAvatar(
    initial: String = "J",
    hasUnread: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier.Companion
) {
    BadgedBox(
        badge = {
            if (hasUnread) {
                Badge(
                    containerColor = Color.Yellow,
                    modifier = Modifier
                        .size(11.dp)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                )
            }
        },
        modifier = modifier
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initial,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun WelcomeMessage(
    userName: String,
    modifier: Modifier = Modifier.Companion
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Good morning",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Hi $userName",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        AccountAvatar()
    }
}

@Composable
private fun DashBoard(modifier: Modifier = Modifier.Companion) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Need something taken away?",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Get an instant price in under a minute.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text(
                    text = "Book a pickup",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun WasteTypeRow(modifier: Modifier = Modifier.Companion) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WasteTypeCard(
            icon = vectorResource(Res.drawable.ic_bungalow),
            heading = "Household",
            modifier = Modifier.weight(1f)
        )
        WasteTypeCard(
            icon = vectorResource(Res.drawable.ic_grass),
            heading = "Garden",
            modifier = Modifier.weight(1f)
        )
        WasteTypeCard(
            icon = vectorResource(Res.drawable.ic_weekend),
            heading = "Furniture",
            modifier = Modifier.weight(1f)
        )
        WasteTypeCard(
            icon = vectorResource(Res.drawable.ic_construction),
            heading = "Building",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WasteTypeCard(
    icon: ImageVector,
    heading: String,
    modifier: Modifier = Modifier.Companion
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = heading,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BookingItem(
    booking: Booking,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier.Companion
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${booking.wasteType} · ${booking.estimatedVolume}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = booking.address,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = booking.jobStatus,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${booking.date} · ${booking.time}",
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = booking.price,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun EmptyUpcomingBooking(modifier: Modifier = Modifier.Companion) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_calendar_month),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.no_upcoming_booking),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Book a pickup and you can track it here.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyCompletedBooking(modifier: Modifier = Modifier.Companion) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.no_completed_booking),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Finished pickups, invoices and receipts will appear here.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BookingsButtonsRow(
    selectedTab: BookingTab,
    onTabSelected: (BookingTab) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { onTabSelected(BookingTab.Upcoming) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == BookingTab.Upcoming) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                contentColor = if (selectedTab == BookingTab.Upcoming) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            ),
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
        ) {
            Text(
                text = "Upcoming",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = { onTabSelected(BookingTab.Completed) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == BookingTab.Completed) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                contentColor = if (selectedTab == BookingTab.Completed) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            ),
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
        ) {
            Text(
                text = "Completed",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}