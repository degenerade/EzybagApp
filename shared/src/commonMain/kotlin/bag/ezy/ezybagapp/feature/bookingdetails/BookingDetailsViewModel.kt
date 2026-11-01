package bag.ezy.ezybagapp.feature.bookingdetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
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
    val contractorId: String = "",
    val title: String = "",
    val volume: String = "",
    val status: String = "",
    val paymentStatus: String = "Pending",
    val date: String = "",
    val time: String = "",
    val description: String = "",
    val bag: String = "",
    val address: String = "",
    val accessInfo: String = "",
    val specialInstructions: String = "",
    val price: String = "",
    val assigned: Boolean = false,
    val contractorName: String = "",
    val contractorInitial: String = "",
    val contractorRating: String = "4.8",
    val contractorCompanyName: String = ""
)

class BookingDetailsViewModel(
    private val bookingId: String,
    private val bookingRepo: BookingRepository = AppContainer.bookingRepository,
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
            bookingRepo.getBooking(bookingId)
                .onSuccess { booking ->
                    if (booking == null) {
                        state = state.copy(isLoading = false, error = "Booking not found")
                        return@onSuccess
                    }

                    var contractorName = ""
                    var contractorInitial = ""
                    val contractorRating = "4.8"
                    var contractorCompanyName = ""

                    val contractorId = booking.contractorId
                    val isAssigned = booking.assigned && !contractorId.isNullOrBlank()

                    if (isAssigned && contractorId != null) {
                        contractorRepo.getContractor(contractorId)
                            .onSuccess { contractor ->
                                if (contractor != null) {
                                    val name = contractor.contactName.trim()
                                    contractorName = if (name.isNotBlank()) name else "Assigned Contractor"
                                    contractorInitial = contractor.contactName.take(1).uppercase().ifBlank { "C" }
                                    contractorCompanyName = contractor.companyName.ifBlank { "Independent Contractor" }
                                }
                            }
                    }

                    state = BookingDetailsUiState(
                        isLoading = false,
                        bookingId = booking.bookingId,
                        contractorId = contractorId ?: "",
                        title = "${booking.wasteDetails.type} waste",
                        volume = booking.wasteDetails.estimatedVolume,
                        status = booking.status,
                        paymentStatus = booking.paymentstatus.ifBlank { "pending" }.replaceFirstChar { it.uppercase() },
                        date = formatDisplayDate(booking.schedule.preferredDate),
                        time = formatDisplayTime(booking.schedule.preferredTime),
                        description = booking.wasteDetails.description.ifBlank { "None" },
                        bag = if (booking.wasteDetails.bagRequired) "${booking.wasteDetails.bagSize ?: ""} bag".trim() else "No bag",
                        address = listOf(booking.collectionAddress.street, booking.collectionAddress.postcode).filter { it.isNotBlank() }.joinToString(", "),
                        accessInfo = booking.accessInfo.ifBlank { "None" },
                        specialInstructions = booking.specialInstructions.ifBlank { "None" },
                        price = if (booking.pricing.estimatedQuote > 0) formatPrice(booking.pricing.estimatedQuote) else "-",
                        assigned = isAssigned,
                        contractorName = contractorName,
                        contractorInitial = contractorInitial,
                        contractorRating = contractorRating,
                        contractorCompanyName = contractorCompanyName
                    )
                }
                .onFailure {
                    state = state.copy(isLoading = false, error = it.message ?: "Couldn't load booking")
                }
        }
    }
}
