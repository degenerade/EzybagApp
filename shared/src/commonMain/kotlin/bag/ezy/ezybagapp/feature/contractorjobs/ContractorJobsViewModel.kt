
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

data class ContractorJobUiModel(
    val reference: String,
    val time: String,
    val wasteType: String,
    val address: String,
    val price: String
)

data class ContractorJobsUiState(
    val jobs: List<ContractorJobUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ContractorJobsViewModel(
    private val bookingRepository: BookingRepository =
        AppContainer.bookingRepository
) : ViewModel() {

    var state by mutableStateOf(ContractorJobsUiState())
        private set

    init {
        loadAvailableJobs()
    }

    fun loadAvailableJobs() {
        viewModelScope.launch {

            state = state.copy(
                isLoading = true,
                error = null
            )

            bookingRepository.getAvailableBookings()
                .onSuccess { bookings ->

                    state = state.copy(
                        jobs = bookings.map {
                            it.toJobUiModel()
                        },
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { exception ->

                    state = state.copy(
                        isLoading = false,
                        error = exception.message
                            ?: "Unable to load available jobs."
                    )
                }
        }
    }
}

private fun Booking.toJobUiModel(): ContractorJobUiModel {

    val dateTime = listOf(
        schedule.preferredDate,
        schedule.preferredTime
    )
        .filter { it.isNotBlank() }
        .joinToString(" · ")
        .ifBlank { "Date not specified" }

    val address = listOf(
        collectionAddress.street,
        collectionAddress.city,
        collectionAddress.postcode
    )
        .filter { it.isNotBlank() }
        .joinToString(", ")
        .ifBlank { "Address not available" }

    val quote = pricing.estimatedQuote

    val price = if (quote % 1.0 == 0.0) {
        "£${quote.toInt()}"
    } else {
        "£$quote"
    }

    return ContractorJobUiModel(
        reference = bookingId,
        time = dateTime,
        wasteType = wasteDetails.type.ifBlank {
            "Waste removal"
        },
        address = address,
        price = price
    )
}
