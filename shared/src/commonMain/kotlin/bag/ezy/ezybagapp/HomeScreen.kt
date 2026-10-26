
package bag.ezy.ezybagapp

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.feature.bookings.BookingTab
import bag.ezy.ezybagapp.model.data.Booking as FirebaseBooking
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

private val HomeGreen = Color(0xFF1E6F5C)
private val HomeBackground = Color(0xFFF5F7F6)
private val HomeOrange = Color(0xFFFFB43B)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onBookingClick: (Booking) -> Unit = {}
) {
    var userName by rememberSaveable {
        mutableStateOf("Customer")
    }

    var selectedTab by rememberSaveable {
        mutableStateOf(BookingTab.Upcoming)
    }

    var upcomingBookings by remember {
        mutableStateOf(emptyList<Booking>())
    }

    var completedBookings by remember {
        mutableStateOf(emptyList<Booking>())
    }

    var loadingBookings by remember {
        mutableStateOf(true)
    }

    var bookingError by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        loadingBookings = true
        bookingError = null

        try {
            val currentUser = Firebase.auth.currentUser

            if (currentUser == null) {
                bookingError = "You are not signed in. Please sign in again."
                upcomingBookings = emptyList()
                completedBookings = emptyList()
            } else {
                // Read the name from the profile stored in Firestore.
                userName = runCatching {
                    AppContainer.authRepository.getCurrentUserName()
                }.getOrDefault(
                    currentUser.displayName
                        ?: currentUser.email
                        ?: "Customer"
                )

                val result = AppContainer.bookingRepository
                    .getBookings(currentUser.uid)

                result.fold(
                    onSuccess = { firebaseBookings ->
                        upcomingBookings = firebaseBookings
                            .filterNot { booking ->
                                booking.status.equals(
                                    "Completed",
                                    ignoreCase = true
                                ) || booking.status.equals(
                                    "Cancelled",
                                    ignoreCase = true
                                )
                            }
                            .map { booking ->
                                booking.toHomeBooking()
                            }

                        completedBookings = firebaseBookings
                            .filter { booking ->
                                booking.status.equals(
                                    "Completed",
                                    ignoreCase = true
                                )
                            }
                            .map { booking ->
                                booking.toHomeBooking()
                            }
                    },
                    onFailure = { error ->
                        bookingError = error.message
                            ?: "Unable to load bookings."

                        upcomingBookings = emptyList()
                        completedBookings = emptyList()
                    }
                )
            }
        } catch (error: Exception) {
            bookingError = error.message
                ?: "Unable to load your account or bookings."
        } finally {
            loadingBookings = false
        }
    }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(HomeBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WelcomeMessage(userName = userName)

            Dashboard()

            WasteTypeRow()

            if (loadingBookings) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            bookingError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            BookingsDisplayColumn(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                },
                upcomingBookings = upcomingBookings,
                completedBookings = completedBookings,
                onBookingClick = onBookingClick
            )
        }
    }
}

private fun FirebaseBooking.toHomeBooking(): Booking {
    val formattedAddress = listOf(
        collectionAddress.street,
        collectionAddress.city,
        collectionAddress.postcode
    )
        .filter { it.isNotBlank() }
        .joinToString(", ")

    return Booking(
        id = bookingId,
        jobStatus = status,
        wasteType = wasteDetails.type,
        estimatedVolume = wasteDetails.estimatedVolume,
        address = formattedAddress,
        date = schedule.preferredDate,
        time = schedule.preferredTime,
        price = "£${pricing.estimatedQuote.toInt()}"
    )
}

@Composable
private fun WelcomeMessage(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Good morning",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = "Hi $userName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun Dashboard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = HomeGreen,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Need something taken away?",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Get an instant price in under a minute.",
                color = Color.White
            )
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book a pickup")
            }
        }
    }
}

@Composable
private fun WasteTypeRow() {
    val wasteTypes = listOf(
        "Household",
        "Garden",
        "Furniture",
        "Building"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        wasteTypes.forEach { wasteType ->
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = wasteType,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingsDisplayColumn(
    selectedTab: BookingTab,
    onTabSelected: (BookingTab) -> Unit,
    upcomingBookings: List<Booking>,
    completedBookings: List<Booking>,
    onBookingClick: (Booking) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onTabSelected(BookingTab.Upcoming) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Upcoming")
            }

            Button(
                onClick = { onTabSelected(BookingTab.Completed) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Completed")
            }
        }

        val visibleBookings = when (selectedTab) {
            BookingTab.Completed -> completedBookings
            BookingTab.Active -> upcomingBookings.filter {
                it.jobStatus.equals("En route", true) ||
                        it.jobStatus.equals("On site", true)
            }
            BookingTab.All -> upcomingBookings + completedBookings
            BookingTab.Upcoming -> upcomingBookings.filterNot {
                it.jobStatus.equals("En route", true) ||
                        it.jobStatus.equals("On site", true)
            }
        }

        if (visibleBookings.isEmpty()) {
            Text(
                text = if (selectedTab == BookingTab.Completed) {
                    "You have no completed bookings yet."
                } else {
                    "You have no upcoming bookings yet."
                },
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray
            )
        } else {
            visibleBookings.forEach { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { onBookingClick(booking) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${booking.wasteType} · ${booking.estimatedVolume}",
                            fontWeight = FontWeight.Bold
                        )

                        if (booking.address.isNotBlank()) {
                            Text(
                                text = booking.address,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            text = "${booking.date} · ${booking.time}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = booking.jobStatus,
                            color = HomeGreen
                        )

                        Text(
                            text = booking.price,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
