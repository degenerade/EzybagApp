package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Booking
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

interface RevenueRepository {
    suspend fun countRevenue(): Result<Double>
}

class FirebaseRevenueRepository : RevenueRepository {

    private val firestore = Firebase.firestore
    private val bookingsCollection = firestore.collection("bookings")

    override suspend fun countRevenue(): Result<Double> = runCatching {
        val snapshot = bookingsCollection
            .where { "status" equalTo "Completed" }
            .get()

        snapshot.documents.sumOf { document ->
            runCatching {
                val booking = document.data<Booking>()
                booking.pricing.estimatedQuote
            }.getOrDefault(0.0)
        }
    }
}
