
package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Booking
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock

interface BookingRepository {

    suspend fun getBookings(customerId: String): Result<List<Booking>>

    suspend fun getEstimate(
        wasteType: String,
        volume: String
    ): Double

    suspend fun createBooking(booking: Booking): Result<String>

    suspend fun requestFinalQuote(booking: Booking): Result<Unit>
}

class FirebaseBookingRepository : BookingRepository {

    private val firestore = Firebase.firestore
    private val bookingsCollection = firestore.collection("bookings")

    override suspend fun getBookings(
        customerId: String
    ): Result<List<Booking>> = runCatching {
        require(customerId.isNotBlank()) {
            "Customer ID is required to load bookings."
        }

        val snapshot = bookingsCollection
            .where { "customerId" equalTo customerId }
            .get()

        snapshot.documents.mapNotNull { document ->
            runCatching {
                document.data<Booking>()
            }.getOrNull()
        }
    }

    override suspend fun getEstimate(
        wasteType: String,
        volume: String
    ): Double {
        return when (volume) {
            "Small" -> 70.0
            "Half van" -> 145.0
            "Full van" -> 260.0
            else -> 0.0
        }
    }

    override suspend fun createBooking(
        booking: Booking
    ): Result<String> = runCatching {

        require(booking.customerId.isNotBlank()) {
            "Customer ID is required to create a booking."
        }

        val document = bookingsCollection.document

        val bookingToSave = booking.copy(
            bookingId = document.id,

            customerPhone = booking.customerPhone.ifBlank {
                booking.contactPhone
            },

            contactPhone = booking.contactPhone.ifBlank {
                booking.customerPhone
            },

            accessDescriptionInformation =
                booking.accessDescriptionInformation.ifBlank {
                    booking.accessInfo
                },

            accessInfo = booking.accessInfo.ifBlank {
                booking.accessDescriptionInformation
            },

            status = "Upcoming",

            createdAt = booking.createdAt.ifBlank {
                Clock.System.now().toString()
            }
        )

        document.set(bookingToSave)

        document.id
    }


    override suspend fun requestFinalQuote(
        booking: Booking
    ): Result<Unit> = runCatching {
        require(booking.bookingId.isNotBlank()) {
            "Booking ID is required to request a final quote."
        }

        bookingsCollection
            .document(booking.bookingId)
            .update(
                "pricing.finalQuoteRequested" to true
            )
    }
}
