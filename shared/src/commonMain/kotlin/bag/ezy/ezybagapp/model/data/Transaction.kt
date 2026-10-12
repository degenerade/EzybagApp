package bag.ezy.ezybagapp.model.data

data class Transaction(
    val transactionId: String = "",
    val bookingId: String = "",
    val customerId: String = "",
    val contractorId: String = "",
    val amount: Double = 0.0,
    val currency: String = "GBP",
    val status: String = "Pending",
    val createdAt: String = ""
)
