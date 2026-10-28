package bag.ezy.ezybagapp.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Contractor(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val companyName: String = "",
    val serviceAreas: String = "",
    val policyNumber: String = "",
    val vehicleType: String = "",
    val vehicleRegistrationNumber: String = "",
    val serviceRadius: Double = 20.0,
    val wasteCarrierLicenceDetails: String = "",
    val insuranceProvider: String = "",
    val isApproved: Boolean = false,
    val proofOfInsuranceUrl: String? = null,
    val proofOfIdentityUrl: String? = null,
    val currentJobId: String? = null,
    val status: String = "Offline",
    val location: GeoLocation? = null
)