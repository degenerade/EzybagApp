package bag.ezy.ezybagapp.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val reviewId: String = "",
    val bookingId: String = "",
    val customerId: String = "",
    val contractorId: String = "",
    val stars: Int = 0, // max is 5
    val comment: String = "",
    val createdAt: String = ""
)
@Serializable
data class Issue(
    val issueId: String = "",
    val bookingId: String = "",
    val customerId: String = "",
    val contractorId: String = "",
//    val stars: Int = 0, // max is 5
    val comment: String = "",
    val createdAt: String = ""
)
