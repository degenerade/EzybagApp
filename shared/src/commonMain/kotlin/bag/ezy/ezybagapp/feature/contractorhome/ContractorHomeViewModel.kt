package bag.ezy.ezybagapp.feature.contractorhome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.data.Contractor
import bag.ezy.ezybagapp.model.repository.BookingRepository
import bag.ezy.ezybagapp.model.repository.ContractorRepository
import bag.ezy.ezybagapp.model.repository.FirebaseContractorRepository
import kotlinx.coroutines.launch

enum class ContractorHomeJobFilter {
    Upcoming,
    Active,
    Completed
}

data class ContractorHomeJobUiModel(
    val id: String,
    val wasteType: String,
    val address: String,
    val dateTime: String,
    val price: String,
    val status: String
)

data class ContractorHomeUiState(
    val contractor: Contractor? = null,
    val jobs: List<ContractorHomeJobUiModel> = emptyList(),
    val filter: ContractorHomeJobFilter =
        ContractorHomeJobFilter.Upcoming,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showApprovalNotice: Boolean = true
) {
    val visibleJobs: List<ContractorHomeJobUiModel>
        get() = jobs.filter { job ->
            when (filter) {
                ContractorHomeJobFilter.Upcoming ->
                    job.status.equals(
                        "Upcoming",
                        ignoreCase = true
                    )

                ContractorHomeJobFilter.Active ->
                    job.status.equals("Active", ignoreCase = true) ||
                            job.status.equals("En Route", ignoreCase = true) ||
                            job.status.equals("On Site", ignoreCase = true)

                ContractorHomeJobFilter.Completed ->
                    job.status.equals(
                        "Completed",
                        ignoreCase = true
                    )
            }
        }
}

class ContractorHomeViewModel(
    private val contractorRepository: ContractorRepository =
        FirebaseContractorRepository(),
    private val bookingRepository: BookingRepository =
        AppContainer.bookingRepository
) : ViewModel() {

    var state by mutableStateOf(
        ContractorHomeUiState()
    )
        private set

    fun load() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            val contractorResult =
                contractorRepository.getCurrentContractor()

            if (contractorResult.isFailure) {
                state = state.copy(
                    isLoading = false,
                    error = contractorResult
                        .exceptionOrNull()
                        ?.message
                        ?: "Unable to load contractor account."
                )

                return@launch
            }

            val contractor =
                contractorResult.getOrNull()

            if (contractor == null) {
                state = state.copy(
                    isLoading = false,
                    error = "Contractor profile could not be found."
                )

                return@launch
            }

            if (!contractor.isApproved) {
                state = state.copy(
                    contractor = contractor,
                    jobs = emptyList(),
                    isLoading = false,
                    error = null
                )

                return@launch
            }

            bookingRepository
                .getCurrentContractorBookings()
                .onSuccess { bookings ->
                    state = state.copy(
                        contractor = contractor,
                        jobs = bookings.map {
                            it.toContractorHomeUiModel()
                        },
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { error ->
                    state = state.copy(
                        contractor = contractor,
                        isLoading = false,
                        error = error.message
                            ?: "Unable to load your jobs."
                    )
                }
        }
    }

    fun selectFilter(
        filter: ContractorHomeJobFilter
    ) {
        state = state.copy(
            filter = filter
        )
    }

    fun dismissApprovalNotice() {
        state = state.copy(
            showApprovalNotice = false
        )
    }
}

private fun Booking.toContractorHomeUiModel():
        ContractorHomeJobUiModel {

    val address = listOf(
        collectionAddress.street,
        collectionAddress.postcode
    )
        .filter {
            it.isNotBlank()
        }
        .joinToString(", ")

    val dateTime = listOf(
        schedule.preferredDate,
        schedule.preferredTime
    )
        .filter {
            it.isNotBlank()
        }
        .joinToString(" · ")

    val quote = pricing.estimatedQuote

    val formattedPrice =
        if (quote % 1.0 == 0.0) {
            "£${quote.toInt()}"
        } else {
            "£$quote"
        }

    return ContractorHomeJobUiModel(
        id = bookingId,
        wasteType = wasteDetails.type,
        address = address,
        dateTime = dateTime,
        price = formattedPrice,
        status = status
    )
}