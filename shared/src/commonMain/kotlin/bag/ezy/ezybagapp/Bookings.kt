package bag.ezy.ezybagapp

data class Booking(
    val id: String = "",
    val jobStatus: String = "On site",
    val wasteType: String = "Construction waste",
    val estimatedVolume: String = "Full van",
    val address: String = "12 Elm Road, M1 4BT",
    val date: String = "Thu 8 Oct",
    val time: String = "9am-12pm",
    val price: String = "£145"
)
