
package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Booking

data class BookingReport(
    val totalBookings: Int,
    val completedBookings: Int,
    val activeBookings: Int,
    val cancelledBookings: Int,
    val unassignedBookings: Int,
    val bookingsByWasteType: Map<String, Int>
)

class ReportRepository {

    fun generateBookingReport(bookings: List<Booking>): BookingReport {

        val completed = bookings.count {
            it.status.equals("Completed", ignoreCase = true)
        }

        val cancelled = bookings.count {
            it.status.equals("Cancelled", ignoreCase = true)
        }

        val active = bookings.count {
            it.status.lowercase() in listOf(
                "pending",
                "upcoming",
                "active",
                "en route",
                "on site"
            )
        }

        val unassigned = bookings.count {
            it.contractorId.isNullOrBlank()
        }

        val wasteTypeCounts = bookings
            .groupingBy { it.wasteDetails.type.ifBlank { "Unknown" } }
            .eachCount()
            .toSortedMap()

        return BookingReport(
            totalBookings = bookings.size,
            completedBookings = completed,
            activeBookings = active,
            cancelledBookings = cancelled,
            unassignedBookings = unassigned,
            bookingsByWasteType = wasteTypeCounts
        )
    }
}
