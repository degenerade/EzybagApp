package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Booking
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock

interface BookingRepository {
    suspend fun getBookings(customerId: String): Result<List<Booking>>
    suspend fun getBooking(bookingId: String): Result<Booking?>
    suspend fun getEstimate(wasteType: String, volume: String): Double
    suspend fun createBooking(booking: Booking): Result<String>
    suspend fun requestFinalQuote(booking: Booking): Result<Unit>
    suspend fun getAllBookings(): Result<List<Booking>>
    suspend fun countAllBookings(): Result<Int>
    suspend fun deleteBooking(bookingId: String = ""): Result<Unit>
}

class FirebaseBookingRepository : BookingRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val bookingsCollection = firestore.collection("bookings")

    private fun requireCurrentUserId(): String {
        return auth.currentUser?.uid
            ?.takeIf { it.isNotBlank() }
            ?: error("Please sign in to access bookings.")
    }

    override suspend fun getBookings(
        customerId: String
    ): Result<List<Booking>> = runCatching {
        val uid = requireCurrentUserId()

        require(customerId == uid) {
            "You can only load your own bookings."
        }

        val snapshot = bookingsCollection
            .where { "customerId" equalTo uid }
            .get()

        snapshot.documents.mapNotNull { document ->
            runCatching { document.data<Booking>() }.getOrNull()
        }
    }

    override suspend fun getBooking(
        bookingId: String
    ): Result<Booking?> = runCatching {
        val uid = requireCurrentUserId()

        require(bookingId.isNotBlank()) {
            "Booking ID is required."
        }

        val document = bookingsCollection.document(bookingId).get()

        if (!document.exists) {
            null
        } else {
            val booking = document.data<Booking>()

            require(booking.customerId == uid) {
                "You are not authorized to access this booking."
            }

            booking
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
        val uid = requireCurrentUserId()

        require(booking.customerId == uid) {
            "The booking customer does not match the signed-in user."
        }

        require(booking.collectionAddress.street.isNotBlank()) {
            "Collection address is required."
        }

        require(booking.collectionAddress.postcode.isNotBlank()) {
            "Collection postcode is required."
        }

        require(booking.customerPhone.isNotBlank()) {
            "Customer phone number is required."
        }

        require(booking.schedule.preferredDate.isNotBlank()) {
            "Collection date is required."
        }

        require(booking.schedule.preferredTime.isNotBlank()) {
            "Collection time is required."
        }

        require(booking.pricing.estimatedQuote > 0.0) {
            "A valid estimate is required before booking."
        }

        val document = bookingsCollection.document
        val now = Clock.System.now().toString()

        val bookingToSave = booking.copy(
            bookingId = document.id,
            customerId = uid,
            contractorId = null,
            status = "Upcoming",
            createdAt = now
        )

        document.set(bookingToSave)
        document.id
    }

    override suspend fun requestFinalQuote(
        booking: Booking
    ): Result<Unit> = runCatching {
        val uid = requireCurrentUserId()

        require(booking.bookingId.isNotBlank()) {
            "Booking ID is required."
        }

        val document = bookingsCollection.document(booking.bookingId).get()

        require(document.exists) {
            "Booking not found."
        }

        val existingBooking = document.data<Booking>()

        require(existingBooking.customerId == uid) {
            "You are not authorized to change this booking."
        }

        bookingsCollection.document(booking.bookingId).update(
            "pricing.finalQuoteRequested" to true
        )
    }

    override suspend fun getAllBookings(): Result<List<Booking>> = runCatching {
        // TODO: Query all bookings from Firestore
        emptyList()
    }

    override suspend fun countAllBookings(): Result<Int> = runCatching {
        // TODO: Count total number of bookings in Firestore
        0
    }

    override suspend fun deleteBooking(bookingId: String): Result<Unit> = runCatching {
        // TODO: Delete booking document from Firestore
    }
}
