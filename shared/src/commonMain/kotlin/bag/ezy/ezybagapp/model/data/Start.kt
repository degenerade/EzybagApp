package bag.ezy.ezybagapp.model.data

enum class UserRole {
    CUSTOMER,
    CONTRACTOR
}

data class StartOption(
    val role: UserRole,
    val title: String,
)