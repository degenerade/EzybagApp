
package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Booking
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

interface RevenueRepository {
    suspend fun countRevenue(): Result<Double>
    suspend fun getSuccessfulBookings(): Result<List<Booking>>
}

class FirebaseRevenueRepository : RevenueRepository {

    private val firestore = Firebase.firestore
    private val bookingsCollection = firestore.collection("bookings")

    override suspend fun countRevenue(): Result<Double> = runCatching {
        val snapshot = bookingsCollection
            .where { "paymentstatus" equalTo "successful" }
            .get()

        snapshot.documents.sumOf { document ->
            runCatching {
                val booking = document.data<Booking>()
                booking.pricing.estimatedQuote
            }.getOrDefault(0.0)
        }
    }

    override suspend fun getSuccessfulBookings(): Result<List<Booking>> =
        runCatching {
            val snapshot = bookingsCollection
                .where { "paymentstatus" equalTo "successful" }
                .get()

            snapshot.documents.mapNotNull { document ->
                runCatching {
                    document.data<Booking>()
                }.getOrNull()
            }
        }
}
