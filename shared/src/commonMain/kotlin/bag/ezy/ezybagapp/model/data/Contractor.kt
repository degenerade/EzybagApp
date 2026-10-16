package bag.ezy.ezybagapp.model.data

data class Contractor(
    val contractorId: String = "",
    val companyName: String = "",
    val contactDetails: String = "",
    val serviceAreas: List<String> = emptyList(),
    val wasteCarrierLicenceDetails: String = "",
    val isApproved: Boolean = false,
    val currentJobId: String? = null,
    val status: String = "Offline",
    val location: GeoLocation? = null
)