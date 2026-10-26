package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Address
import bag.ezy.ezybagapp.model.data.Booking
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock
import bag.ezy.ezybagapp.model.data.Pricing
import bag.ezy.ezybagapp.model.data.Schedule
import bag.ezy.ezybagapp.model.data.WasteDetails
import kotlinx.coroutines.delay

interface BookingRepository {
    suspend fun getBookings(customerId: String): Result<List<Booking>>
    suspend fun getBooking(bookingId: String): Result<Booking?>
    suspend fun getEstimate(wasteType: String, volume: String): Double
    suspend fun createBooking(booking: Booking): Result<String>
    suspend fun requestFinalQuote(booking: Booking): Result<Unit>
}

class FakeBookingRepository : BookingRepository {
    private val bookings = mutableListOf(
        Booking(
            bookingId = "CC-10482",
            collectionAddress = Address(
                street = "12 Elm Road",
                city = "Manchester",
                postcode = "M1 4BT"
            ),
            wasteDetails = WasteDetails(type = "Construction", description = "Old kitchen units and rubble",
                estimatedVolume = "Half van", bagRequired = true, bagSize = "Medium"),
            schedule = Schedule(preferredDate = "2026-10-08", preferredTime = "09:00"),
            pricing = Pricing(estimatedQuote = 145.0),
            status = "En route",
        ),
        Booking(
            bookingId = "CC-10511",
            collectionAddress = Address(street = "4 Park Lane", city = "Manchester", postcode = "M14 6QL"),
            wasteDetails = WasteDetails(type = "Garden", estimatedVolume = "Small"),
            schedule = Schedule(preferredDate = "2026-10-17", preferredTime = "13:00"),
            pricing = Pricing(estimatedQuote = 60.0),
            status = "Upcoming",
        ),
        Booking(
            bookingId = "CC-10274",
            collectionAddress = Address(street = "9 Oak Street", city = "Stockport", postcode = "SK4 2DA"),
            wasteDetails = WasteDetails(type = "Furniture", estimatedVolume = "Small"),
            schedule = Schedule(preferredDate = "2026-09-18", preferredTime = "10:00"),
            pricing = Pricing(estimatedQuote = 70.0),
            status = "Completed",
        ),
        Booking(
            bookingId = "CC-10199",
            wasteDetails = WasteDetails(type = "Household", estimatedVolume = "Full van"),
            schedule = Schedule(preferredDate = "2026-09-04", preferredTime = "08:00"),
            status = "Cancelled",
        ),
    )

    override suspend fun getBookings(customerId: String): Result<List<Booking>> {
        delay(500)
        return Result.success(bookings.toList())
    }

    override suspend fun getBooking(bookingId: String): Result<Booking?> {
        delay(300)
        return Result.success(bookings.find { it.bookingId == bookingId })
    }

    override suspend fun getEstimate(wasteType: String, volume: String): Double = when (volume) {
        "Small" -> 70.0
        "Half van" -> 145.0
        "Full van" -> 260.0
        else -> 0.0
    }

    override suspend fun createBooking(booking: Booking): Result<String> {
        delay(800)
        val id = "CC-" + (10600..19999).random()
        bookings.add(0, booking.copy(bookingId = id, status = "Upcoming"))
        return Result.success(id)
    }

    override suspend fun requestFinalQuote(booking: Booking): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
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

    override suspend fun getBooking(
        bookingId: String
    ): Result<Booking?> = runCatching {
        require(bookingId.isNotBlank()) {
            "Booking ID is required to load a booking."
        }

        val document = bookingsCollection
            .document(bookingId)
            .get()

        if (document.exists) document.data<Booking>() else null
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