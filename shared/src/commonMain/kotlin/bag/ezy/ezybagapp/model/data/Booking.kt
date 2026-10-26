
package bag.ezy.ezybagapp.model.data

import kotlinx.serialization.Serializable

@Serializable
data class WasteDetails(
    val type: String = "",
    val description: String = "",
    val estimatedVolume: String = "",
    val bagRequired: Boolean = false,
    val bagSize: String? = null
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
    val customerPhone: String = "",
    val collectionAddress: Address = Address(),
    val contactPhone: String = "",
    val accessInfo: String = "",
    val accessDescriptionInformation: String = "",
    val specialInstructions: String = "",
    val wasteDetails: WasteDetails = WasteDetails(),
    val media: Media = Media(),
    val schedule: Schedule = Schedule(),
    val pricing: Pricing = Pricing(),
    val status: String = "Pending",
    val contractorCurrentLocation: GeoLocation? = null,
    val disposalDetails: DisposalDetails? = null,
    val createdAt: String = ""
)
