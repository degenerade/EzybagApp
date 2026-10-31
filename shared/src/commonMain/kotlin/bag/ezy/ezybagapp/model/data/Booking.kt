package bag.ezy.ezybagapp.model.data

import kotlinx.serialization.Serializable

@Serializable
data class WasteDetails(
    val type: String = "", // Household, Garden, Construction, Furniture, Electrical, Mixed, Other
    val description: String = "",
    val estimatedVolume: String = "", // Small, Half van, Full van
    val bagRequired: Boolean = false,
    val bagSize: String? = null // Small, Medium, Large
)

@Serializable
data class Schedule(
    val preferredDate: String = "",
    val preferredTime: String = ""
)

@Serializable
data class Pricing(
    val estimatedQuote: Double = 0.0,
    val currency: String = "GBP",
    val finalQuoteRequested: Boolean = false
)

@Serializable
data class DisposalDetails(
    val facilityName: String = "",
    val weightKg: Double = 0.0,
    val referenceNumber: String = "",
    val completionPhotos: List<String> = emptyList()
)

@Serializable
data class Booking(
    val bookingId: String = "",
    val customerId: String = "",
    val contractorId: String? = null,
    val customerPhone: String = "", // Added from New Booking UI
    val collectionAddress: Address = Address(),
    val accessInfo: String = "",
    val specialInstructions: String = "",
    val wasteDetails: WasteDetails = WasteDetails(),
    val media: Media = Media(),
    val schedule: Schedule = Schedule(),
    val pricing: Pricing = Pricing(),
    val status: String = "Pending", // Pending, Upcoming, En Route, On Site, Completed, Cancelled
    val contractorCurrentLocation: GeoLocation? = null,
    val disposalDetails: DisposalDetails? = null,
    val assigned: Boolean = false,
    val createdAt: String = "" //having such info is useful :D
)
