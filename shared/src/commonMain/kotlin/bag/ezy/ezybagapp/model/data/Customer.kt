package bag.ezy.ezybagapp.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val customerId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val createdAt: String = ""
)
