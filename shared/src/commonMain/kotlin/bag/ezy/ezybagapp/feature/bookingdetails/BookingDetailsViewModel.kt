package bag.ezy.ezybagapp.feature.bookingdetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.repository.BookingRepository
import bag.ezy.ezybagapp.model.repository.ContractorRepository
import bag.ezy.ezybagapp.ui.format.formatDisplayDate
import bag.ezy.ezybagapp.ui.format.formatDisplayTime
import bag.ezy.ezybagapp.ui.format.formatPrice
import kotlinx.coroutines.launch

data class BookingDetailsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val bookingId: String = "",
    val contractorId: String? = null,
    val title: String = "",
    val volume: String = "",
    val status: String = "",
    val date: String = "",
    val time: String = "",
    val description: String = "",
    val bag: String = "",
    val address: String = "",
    val accessInfo: String = "",
    val specialInstructions: String = "",
    val price: String = "",
    val paymentStatus: String = "",
    val contractorName: String = "",
    val contractorCompanyName: String = "",
    val contractorRating: String = ""
)

class BookingDetailsViewModel(
    private val bookingId: String,
    private val repo: BookingRepository,
    private val contractorRepo: ContractorRepository = AppContainer.contractorRepository
) : ViewModel() {
    var state by mutableStateOf(BookingDetailsUiState())
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            repo.getBooking(bookingId)
                .onSuccess { b ->
                    if (b == null) {
                        state = state.copy(isLoading = false, error = "Booking not found")
                    } else {
                        var cName = ""
                        var cCompany = ""
                        val cId = b.contractorId
                        if (!cId.isNullOrBlank()) {
                            contractorRepo.getContractor(cId).getOrNull()?.let { contractor ->
                                cName = contractor.contactName.ifBlank { "Contractor" }
                                cCompany = contractor.companyName.ifBlank { "Ezybag Partner" }
                            }
                        }
                        state = b.toUiState(cName, cCompany)
                    }
                }
                .onFailure { state = state.copy(isLoading = false, error = it.message ?: "Couldn't load booking") }
        }
    }

    private fun Booking.toUiState(cName: String, cCompany: String) = BookingDetailsUiState(
        isLoading = false,
        bookingId = bookingId,
        contractorId = contractorId,
        title = if (wasteDetails.type.isNotBlank()) "${wasteDetails.type} waste" else "Waste collection",
        volume = wasteDetails.estimatedVolume.ifBlank { "Standard" },
        status = status,
        date = formatDisplayDate(schedule.preferredDate),
        time = formatDisplayTime(schedule.preferredTime),
        description = wasteDetails.description.ifBlank { "No description provided" },
        bag = if (wasteDetails.bagRequired) "${wasteDetails.bagSize ?: ""} bag".trim() else "No bag",
        address = listOf(collectionAddress.street, collectionAddress.postcode).filter { it.isNotBlank() }.joinToString(", "),
        accessInfo = accessInfo.ifBlank { "None provided" },
        specialInstructions = specialInstructions.ifBlank { "None" },
        price = if (pricing.estimatedQuote > 0) formatPrice(pricing.estimatedQuote) else "-",
        paymentStatus = paymentstatus.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }.ifBlank { "Pending" },
        contractorName = cName.ifBlank { if (contractorId != null) "Assigned Contractor" else "Unassigned" },
        contractorCompanyName = cCompany.ifBlank { if (contractorId != null) "Partner" else "" },
        contractorRating = "4.8"
    )
}
