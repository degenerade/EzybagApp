

package bag.ezy.ezybagapp.feature.contractorjobs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.repository.BookingRepository
import kotlinx.coroutines.launch

data class ContractorJobDetailsUiState(
    val isLoading: Boolean = true,
    val booking: Booking? = null,
    val error: String? = null,
    val isAccepting: Boolean = false,
    val acceptError: String? = null,
    val acceptSuccess: Boolean = false,
    val isCompleting: Boolean = false,
    val completeError: String? = null,
    val completeSuccess: Boolean = false
)

class ContractorJobDetailsViewModel(
    private val bookingRepository: BookingRepository =
        AppContainer.bookingRepository
) : ViewModel() {

    var state by mutableStateOf(ContractorJobDetailsUiState())
        private set

    fun loadBooking(bookingId: String) {
        if (bookingId.isBlank()) {
            state = state.copy(
                isLoading = false,
                error = "Booking ID is missing."
            )
            return
        }

        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                booking = null,
                error = null,
                isAccepting = false,
                acceptError = null,
                acceptSuccess = false,
                isCompleting = false,
                completeError = null,
                completeSuccess = false
            )

            bookingRepository.getBooking(bookingId)
                .onSuccess { booking ->
                    state = state.copy(
                        isLoading = false,
                        booking = booking,
                        error = if (booking == null) {
                            "Booking details could not be found."
                        } else {
                            null
                        }
                    )
                }
                .onFailure { exception ->
                    state = state.copy(
                        isLoading = false,
                        error = exception.message
                            ?: "Unable to load booking details."
                    )
                }
        }
    }

    fun acceptJob(bookingId: String) {
        if (state.isAccepting || state.acceptSuccess) return

        viewModelScope.launch {
            state = state.copy(
                isAccepting = true,
                acceptError = null
            )

            bookingRepository.acceptAvailableBooking(bookingId)
                .onSuccess {
                    state = state.copy(
                        isAccepting = false,
                        acceptSuccess = true,
                        acceptError = null,
                        booking = state.booking?.copy(
                            status = "Active",
                            assigned = true
                        )
                    )
                }
                .onFailure { exception ->
                    state = state.copy(
                        isAccepting = false,
                        acceptError = exception.message
                            ?: "Unable to accept this job."
                    )
                }
        }
    }

    fun completeJob(bookingId: String) {
        if (state.isCompleting || state.completeSuccess) return

        viewModelScope.launch {
            state = state.copy(
                isCompleting = true,
                completeError = null
            )

            bookingRepository.completeContractorJob(bookingId)
                .onSuccess {
                    state = state.copy(
                        isCompleting = false,
                        completeSuccess = true,
                        completeError = null,
                        booking = state.booking?.copy(
                            status = "Completed"
                        )
                    )
                }
                .onFailure { exception ->
                    state = state.copy(
                        isCompleting = false,
                        completeError = exception.message
                            ?: "Unable to complete this job."
                    )
                }
        }
    }
}
