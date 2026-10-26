package bag.ezy.ezybagapp.model.data
import kotlinx.serialization.Serializable

@Serializable
data class Contractor(
    val userId: String = "",
    val companyName: String = "",
    val policyNumber: String = "",
    val vehicleType: String = "",
    val vehicleRegistrationNumber: String = "",
    //val serviceAreas: List<String> = emptyList(), // Towns or postcodes covered (e.g., ["BS1", "BS2"])
    val serviceRadius: Double = 20.0, // Added to handle radius-based job searching // in miles?
    val wasteCarrierLicenceDetails: String = "",
    val insuranceProvider: String = "",
    val isApproved: Boolean = false,
    val proofOfInsuranceUrl: String? = null,
    val proofOfIdentityUrl: String? = null,
    val currentJobId: String? = null,
    val status: String = "Offline",
    val location: GeoLocation? = null
)