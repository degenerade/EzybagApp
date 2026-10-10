package bag.ezy.ezybagapp

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class BookingUiModel(
    val reference: String,
    val wasteType: String,
    val status: String,
    val date: String,
    val isEmptyState: Boolean = false
)

@Composable
fun BookingsScreen() {

    val bookings = listOf(
        BookingUiModel(
            reference = "CC-10482",
            wasteType = "Mixed waste",
            status = "En route",
            date = "2 Oct"
        ),
        BookingUiModel(
            reference = "CC-10511",
            wasteType = "Garden waste",
            status = "Upcoming",
            date = "13 Oct"
        ),
        BookingUiModel(
            reference = "CC-10274",
            wasteType = "Furniture",
            status = "Completed",
            date = "18 Sep"
        ),
        BookingUiModel(
            reference = "Cancelled",
            wasteType = "No bookings",
            status = "",
            date = "",
            isEmptyState = true
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {

        Text(
            text = "Bookings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Upcoming · Active · Completed",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "3 bookings",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookings) { booking ->
                BookingCard(booking)
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF087A3D)
            )
        ) {
            Text(
                text = "Book another"
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
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F6E9)
        )
    ) {

        if (booking.isEmptyState) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Cancelled",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = "No bookings",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

        } else {

            Column(
                modifier = Modifier.padding(14.dp)
            ) {

                Text(
                    text = booking.reference,
                    fontSize = 12.sp,
                    color = Color.Gray
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
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = booking.date,
                        fontSize = 13.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = booking.status,
                    fontSize = 13.sp,
                    color = Color(0xFF087A3D),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
    @androidx.compose.ui.tooling.preview.Preview
    @Composable
    fun BookingsScreenPreview() {
        BookingsScreen()

}
