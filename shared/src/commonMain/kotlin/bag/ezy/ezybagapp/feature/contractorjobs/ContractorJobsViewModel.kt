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
    val price: String,
    val status: String
)

data class ContractorJobsUiState(
    val jobs: List<ContractorJobUiModel> = emptyList(),
    val activeJobs: List<ContractorJobUiModel> = emptyList(),
    val completedJobs: List<ContractorJobUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val completingBookingId: String? = null,
    val updatingBookingId: String? = null,
    val completionError: String? = null
)

class ContractorJobsViewModel(
    private val bookingRepository: BookingRepository = AppContainer.bookingRepository
) : ViewModel() {

    var state by mutableStateOf(ContractorJobsUiState())
        private set

    init {
        loadJobs()
    }

    fun loadAvailableJobs() = loadJobs()

    fun loadJobs() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null,
                completionError = null
            )

            val availableResult = bookingRepository.getAvailableBookings()
            val contractorResult = bookingRepository.getCurrentContractorBookings()

            if (availableResult.isFailure) {
                state = state.copy(
                    isLoading = false,
                    error = availableResult.exceptionOrNull()?.message
                        ?: "Unable to load available jobs."
                )
                return@launch
            }

            if (contractorResult.isFailure) {
                state = state.copy(
                    isLoading = false,
                    error = contractorResult.exceptionOrNull()?.message
                        ?: "Unable to load your jobs."
                )
                return@launch
            }

            val contractorBookings = contractorResult.getOrThrow()

            state = state.copy(
                jobs = availableResult.getOrThrow().map { it.toJobUiModel() },
                activeJobs = contractorBookings
                    .filter { it.status != "Completed" }
                    .map { it.toJobUiModel() },
                completedJobs = contractorBookings
                    .filter { it.status == "Completed" }
                    .map { it.toJobUiModel() },
                isLoading = false,
                error = null
            )
        }
    }

    fun updateJobStatus(bookingId: String, newStatus: String) {
        if (state.updatingBookingId != null || state.completingBookingId != null) return

        viewModelScope.launch {
            state = state.copy(
                updatingBookingId = bookingId,
                completionError = null
            )

            bookingRepository.updateContractorJobStatus(bookingId, newStatus)
                .onSuccess {
                    state = state.copy(updatingBookingId = null)
                    loadJobs()
                }
                .onFailure { exception ->
                    state = state.copy(
                        updatingBookingId = null,
                        completionError = exception.message
                            ?: "Unable to update this job."
                    )
                }
        }
    }

    fun completeJob(bookingId: String) {
        if (state.completingBookingId != null || state.updatingBookingId != null) return

        viewModelScope.launch {
            state = state.copy(
                completingBookingId = bookingId,
                completionError = null
            )

            bookingRepository.completeContractorJob(bookingId)
                .onSuccess {
                    state = state.copy(completingBookingId = null)
                    loadJobs()
                }
                .onFailure { exception ->
                    state = state.copy(
                        completingBookingId = null,
                        completionError = exception.message
                            ?: "Unable to complete this job."
                    )
                }
        }
    }
}

private fun Booking.toJobUiModel(): ContractorJobUiModel {
    val dateTime = listOf(schedule.preferredDate, schedule.preferredTime)
        .filter { it.isNotBlank() }
        .joinToString(" · ")
        .ifBlank { "Date not specified" }

    val addressText = listOf(
        collectionAddress.street,
        collectionAddress.city,
        collectionAddress.postcode
    )
        .filter { it.isNotBlank() }
        .joinToString(", ")
        .ifBlank { "Address not available" }

    val quote = pricing.estimatedQuote
    val priceText = if (quote % 1.0 == 0.0) "£${quote.toInt()}" else "£$quote"

    return ContractorJobUiModel(
        reference = bookingId,
        time = dateTime,
        wasteType = wasteDetails.type.ifBlank { "Waste removal" },
        address = addressText,
        price = priceText,
        status = status
    )
}
