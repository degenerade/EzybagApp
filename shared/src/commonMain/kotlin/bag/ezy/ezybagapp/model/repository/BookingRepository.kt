
package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.data.Contractor
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock

interface BookingRepository {
    suspend fun getBookings(
        customerId: String
    ): Result<List<Booking>>

    suspend fun getBooking(
        bookingId: String
    ): Result<Booking?>

    suspend fun getEstimate(
        wasteType: String,
        volume: String
    ): Double

    suspend fun createBooking(
        booking: Booking
    ): Result<String>

    suspend fun requestFinalQuote(
        booking: Booking
    ): Result<Unit>

    suspend fun getCurrentContractorBookings(): Result<List<Booking>>

    suspend fun getAvailableBookings(): Result<List<Booking>>

    suspend fun selectContractor(
        bookingId: String,
        contractorId: String
    ): Result<Unit>
}

class FirebaseBookingRepository : BookingRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val bookingsCollection =
        firestore.collection("bookings")

    private val contractorsCollection =
        firestore.collection("contractors")

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
            runCatching {
                document.data<Booking>()
            }.getOrNull()
        }
    }


    override suspend fun getBooking(
        bookingId: String
    ): Result<Booking?> = runCatching {

        val uid = requireCurrentUserId()

        require(bookingId.isNotBlank()) {
            "Booking ID is required."
        }

        val document =
            bookingsCollection.document(bookingId).get()

        if (!document.exists) {
            null
        } else {

            val booking = document.data<Booking>()

            // The customer who created the booking can view it.
            val isCustomer = booking.customerId == uid

            // Check whether the signed-in user is an approved contractor.
            val contractorDocument =
                contractorsCollection.document(uid).get()

            val isApprovedContractor =
                contractorDocument.exists &&
                        contractorDocument.data<Contractor>().let {
                            it.uid == uid && it.isApproved
                        }

            // Contractors can view jobs assigned to them.
            val isAssignedContractor =
                isApprovedContractor &&
                        booking.contractorId == uid

            // Contractors can also open unassigned available jobs.
            val isAvailableJob =
                isApprovedContractor &&
                        !booking.assigned &&
                        booking.contractorId == null &&
                        booking.status == "Upcoming"

            require(
                isCustomer ||
                        isAssignedContractor ||
                        isAvailableJob
            ) {
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
            assigned = false,
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

        val document =
            bookingsCollection.document(booking.bookingId).get()

        require(document.exists) {
            "Booking not found."
        }

        val existingBooking = document.data<Booking>()

        require(existingBooking.customerId == uid) {
            "You are not authorized to change this booking."
        }

        require(existingBooking.status == "Upcoming") {
            "A final quote can only be requested for an upcoming booking."
        }

        bookingsCollection.document(booking.bookingId).update(
            "pricing.finalQuoteRequested" to true
        )
    }

    override suspend fun selectContractor(
        bookingId: String,
        contractorId: String
    ): Result<Unit> = runCatching {
        val customerUid = requireCurrentUserId()

        require(bookingId.isNotBlank()) {
            "Booking ID is required."
        }

        require(contractorId.isNotBlank()) {
            "Please select a contractor."
        }

        require(contractorId != customerUid) {
            "You cannot assign yourself as the contractor."
        }

        // Check that the contractor exists and is approved.
        // Firestore Rules independently repeat this check
        // when the booking update is attempted.
        val contractorDocument =
            contractorsCollection.document(contractorId).get()

        require(contractorDocument.exists) {
            "Contractor not found."
        }

        val contractor =
            contractorDocument.data<Contractor>()

        require(
            contractor.uid == contractorId &&
                    contractor.isApproved
        ) {
            "This contractor is not approved to receive jobs."
        }

        // Verify that the customer owns an unassigned booking.
        val bookingDocument =
            bookingsCollection.document(bookingId).get()

        require(bookingDocument.exists) {
            "Booking not found."
        }

        val booking = bookingDocument.data<Booking>()

        require(booking.customerId == customerUid) {
            "You can only select a contractor for your own booking."
        }

        require(!booking.assigned && booking.contractorId == null) {
            "A contractor has already been assigned to this booking."
        }

        require(booking.status == "Upcoming") {
            "This booking is not available for contractor selection."
        }

        // The Firestore rules must validate ownership,
        // assignment, status, and contractor approval again.
        bookingsCollection.document(bookingId).update(
            "contractorId" to contractorId,
            "status" to "Active",
            "assigned" to true
        )
    }

    override suspend fun getCurrentContractorBookings():
            Result<List<Booking>> = runCatching {

        val uid = requireCurrentUserId()

        // This query returns only bookings assigned to this UID.
        // Firestore Rules also require the contractor to be approved.
        val snapshot = bookingsCollection
            .where { "contractorId" equalTo uid }
            .get()

        snapshot.documents.mapNotNull { document ->
            runCatching {
                document.data<Booking>()
            }.getOrNull()
        }
    }
    override suspend fun getAvailableBookings():
            Result<List<Booking>> = runCatching {

        requireCurrentUserId()

        val snapshot = bookingsCollection
            .where {
                "status" equalTo "Upcoming"
            }
            .where {
                "assigned" equalTo false
            }
            .get()

        snapshot.documents.mapNotNull { document ->
            runCatching {
                document.data<Booking>()
            }.getOrNull()
        }
    }
}
