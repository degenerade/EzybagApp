package bag.ezy.ezybagapp.model.data
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val customerId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val createdAt: String = ""
)