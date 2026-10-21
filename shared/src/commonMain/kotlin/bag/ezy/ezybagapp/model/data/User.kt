package bag.ezy.ezybagapp.model.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val role: String = "Customer",
    val createdAt: String = ""
)
