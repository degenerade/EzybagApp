package bag.ezy.ezybagapp.model.data

data class Review(
    val reviewId: String = "",
    val bookingId: String = "",
    val customerId: String = "",
    val contractorId: String = "",
    val stars: Int = 0,
    val comment: String = "",
    val createdAt: String = ""
)