package bag.ezy.ezybagapp.model.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

interface RevenueRepository {
    suspend fun countRevenue(): Result<Double>
}

class FirebaseRevenueRepository : RevenueRepository {

    private val firestore = Firebase.firestore

    override suspend fun countRevenue(): Result<Double> = runCatching {
        // TODO: Calculate total revenue from Firestore transactions/bookings
        0.0
    }
}