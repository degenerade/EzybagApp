package bag.ezy.ezybagapp.model.data

data class GeoLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val heading: Double? = null,
    val timestamp: String? = null
)

data class Address(
    val street: String = "",
    val city: String = "",
    val postcode: String = "",
    val coordinates: GeoLocation = GeoLocation()
)

data class Media(
    val photos: List<String> = emptyList(),
    val videos: List<String> = emptyList()
)
