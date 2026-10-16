package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.data.Schedule
import bag.ezy.ezybagapp.model.data.WasteDetails
import kotlinx.coroutines.delay

interface BookingRepository {
    suspend fun getBookings(customerId: String): Result<List<Booking>>
    suspend fun getEstimate(wasteType: String, volume: String): Double
    suspend fun createBooking(booking: Booking): Result<String>
    suspend fun requestFinalQuote(booking: Booking): Result<Unit>
}

class FakeBookingRepository : BookingRepository {
    private val bookings = mutableListOf(
        Booking(bookingId = "CC-10482", wasteDetails = WasteDetails(type = "Mixed waste"),
            schedule = Schedule(preferredDate = "Fri 2 Oct"), status = "En route"),
        Booking(bookingId = "CC-10511", wasteDetails = WasteDetails(type = "Garden waste"),
            schedule = Schedule(preferredDate = "13 Oct"), status = "Upcoming"),
        Booking(bookingId = "CC-10274", wasteDetails = WasteDetails(type = "Furniture"),
            schedule = Schedule(preferredDate = "18 Sep"), status = "Completed"),
        Booking(bookingId = "CC-10199", wasteDetails = WasteDetails(type = "Household"),
            schedule = Schedule(preferredDate = "4 Sep"), status = "Cancelled"),
    )

    override suspend fun getBookings(customerId: String): Result<List<Booking>> {
        delay(500)
        return Result.success(bookings.toList())
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