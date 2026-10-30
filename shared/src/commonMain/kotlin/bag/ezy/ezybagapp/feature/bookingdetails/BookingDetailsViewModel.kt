package bag.ezy.ezybagapp.feature.bookingdetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.repository.BookingRepository
import bag.ezy.ezybagapp.ui.format.formatDisplayDate
import bag.ezy.ezybagapp.ui.format.formatDisplayTime
import bag.ezy.ezybagapp.ui.format.formatPrice
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
    val price: String = ""
)

class BookingDetailsViewModel(
    private val bookingId: String,
    private val repo: BookingRepository
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
                    state = if (b == null) state.copy(isLoading = false, error = "Booking not found")
                        else b.toUiState()
                }
                .onFailure { state = state.copy(isLoading = false, error = it.message ?: "Couldn't load booking") }
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
        bag = if (wasteDetails.bagRequired) "${wasteDetails.bagSize ?: ""} bag".trim() else "No bag",
        address = listOf(collectionAddress.street, collectionAddress.postcode).filter { it.isNotBlank() }.joinToString(", "),
        price = if (pricing.estimatedQuote > 0) formatPrice(pricing.estimatedQuote) else "-"
    )
}