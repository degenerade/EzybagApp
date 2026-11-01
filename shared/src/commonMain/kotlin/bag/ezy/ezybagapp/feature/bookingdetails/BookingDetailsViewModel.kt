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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class BookingDetailsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val title: String = "",
    val volume: String = "",
    val status: String = "",
    val date: String = "",
    val time: String = "",
    val description: String = "",
    val bag: String = "",
    val address: String = "",
    val accessInfo: String = "None provided",
    val instructions: String = "None provided",
    val price: String = "",
    val paymentStatus: String = "Pending",
    val paymentMethod: String = "Card",
    val hasContractor: Boolean = false,
    val contractorName: String = "",
    val contractorInitial: String = "C",
    val contractorRating: String = "5.0",
    val contractorCompanyName: String = "",
    val contractorPhone: String = "",
    val contractorEmail: String = "",
    val contractorError: String? = null
)

class BookingDetailsViewModel(
    private val bookingId: String,
    private val bookingRepo: BookingRepository = AppContainer.bookingRepository,
    private val contractorRepo: ContractorRepository = AppContainer.contractorRepository
) : ViewModel() {
    var state by mutableStateOf(BookingDetailsUiState())
        private set

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            var firstLoad = true
            while (isActive) {
                fetchBookingDetails(showLoading = firstLoad)
                firstLoad = false
                delay(5_000L) // Refresh every 5 seconds
            }
        }
    }

    fun load() {
        fetchBookingDetails(showLoading = true)
    }

    fun cancelBooking(onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            bookingRepo.deleteBooking(bookingId)
                .onSuccess {
                    onDeleted()
                }
                .onFailure { exception ->
                    state = state.copy(isLoading = false, error = exception.message ?: "Failed to delete booking")
                }
        }
    }

    private fun fetchBookingDetails(showLoading: Boolean) {
        viewModelScope.launch {
            if (showLoading) {
                state = state.copy(isLoading = true, error = null)
            }
            bookingRepo.getBooking(bookingId)
                .onSuccess { b ->
                    if (b == null) {
                        state = state.copy(isLoading = false, error = "Booking not found")
                    } else {
                        val baseState = b.toUiState()
                        val contractorId = b.contractorId?.trim().orEmpty()
                        if (contractorId.isNotBlank()) {
                            contractorRepo.getContractor(contractorId)
                                .onSuccess { contractor ->
                                    if (contractor != null) {
                                        val displayName = contractor.contactName.ifBlank {
                                            contractor.companyName.ifBlank { contractor.email }
                                        }
                                        val initial = displayName.firstOrNull()?.uppercase() ?: "C"
                                        state = baseState.copy(
                                            hasContractor = true,
                                            contractorName = displayName,
                                            contractorInitial = initial,
                                            contractorRating = "5.0",
                                            contractorCompanyName = contractor.companyName,
                                            contractorPhone = contractor.phone,
                                            contractorEmail = contractor.email,
                                            contractorError = null
                                        )
                                    } else {
                                        state = baseState.copy(
                                            hasContractor = false,
                                            contractorError = "Contractor document '$contractorId' not found."
                                        )
                                    }
                                }
                                .onFailure { exception ->
                                    state = baseState.copy(
                                        hasContractor = false,
                                        contractorError = "Failed to fetch contractor ($contractorId): ${exception.message}"
                                    )
                                }
                        } else {
                            state = baseState.copy(
                                hasContractor = false,
                                contractorError = null
                            )
                        }
                    }
                }
                .onFailure { err ->
                    if (showLoading) {
                        state = state.copy(isLoading = false, error = err.message ?: "Couldn't load booking")
                    }
                }
        }
    }

    private fun Booking.toUiState() = BookingDetailsUiState(
        isLoading = false,
        title = "${wasteDetails.type} waste",
        volume = wasteDetails.estimatedVolume,
        status = status,
        date = formatDisplayDate(schedule.preferredDate),
        time = formatDisplayTime(schedule.preferredTime),
        description = wasteDetails.description,
        bag = if (wasteDetails.bagRequired) "${wasteDetails.bagSize ?: ""} bag".trim() else "No bag required",
        address = listOf(collectionAddress.street, collectionAddress.city, collectionAddress.postcode).filter { it.isNotBlank() }.joinToString(", "),
        accessInfo = accessInfo.ifBlank { "None provided" },
        instructions = specialInstructions.ifBlank { "None provided" },
        price = if (pricing.estimatedQuote > 0) formatPrice(pricing.estimatedQuote) else "—",
        paymentStatus = if (pricing.estimatedQuote > 0) "Paid" else "Pending",
        paymentMethod = "Card"
    )
}
