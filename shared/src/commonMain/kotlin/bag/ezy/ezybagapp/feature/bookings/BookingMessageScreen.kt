package bag.ezy.ezybagapp.feature.bookings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BookingConfirmationScreen(
    bookingId: String,
    onViewBookings: () -> Unit,
    onReturnHome: () -> Unit,
    onBookAnother: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "✓",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Booking confirmed!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your pickup booking has been saved successfully.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Booking reference",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = bookingId.ifBlank { "Reference unavailable" },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Payment is simulated during testing. No real payment has been taken.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onViewBookings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View my bookings")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onReturnHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Return home")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBookAnother,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book another pickup")
            }
        }
    }
}

@Composable
fun BookingFailedScreen(
    onReturnHome: () -> Unit,
    onBookAnother: () -> Unit,
    message: String = "Something went wrong and your pickup booking could not be saved."
) {
    BookingResultScreen(
        symbol = "✗",
        symbolColor = MaterialTheme.colorScheme.error,
        title = "Booking failed",
        message = message,
        note = "No payment has been taken. Please try again.",
        onReturnHome = onReturnHome,
        onBookAnother = onBookAnother
    )
}

@Composable
fun BookingCanceledScreen(
    onReturnHome: () -> Unit,
    onBookAnother: () -> Unit,
    bookingId: String? = null
) {
    BookingResultScreen(
        symbol = "⊘",
        symbolColor = Color(0xFF9A6B1F),
        title = "Booking canceled",
        message = "Your pickup booking has been canceled.",
        reference = bookingId,
        note = "Payment is simulated during testing. No real payment has been taken.",
        onReturnHome = onReturnHome,
        onBookAnother = onBookAnother
    )
}

@Composable
private fun BookingResultScreen(
    symbol: String,
    symbolColor: Color,
    title: String,
    message: String,
    onReturnHome: () -> Unit,
    onBookAnother: () -> Unit,
    reference: String? = null,
    note: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.displayLarge,
                color = symbolColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            if (!reference.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Booking reference",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = reference,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            if (note != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBookAnother,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book another pickup")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onReturnHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Return home")
            }
        }
    }
}