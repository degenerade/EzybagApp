package bag.ezy.ezybagapp.feature.admin.contractors.contractordetailsadmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.data.Contractor
import bag.ezy.ezybagapp.model.queries.AdminRepository
import bag.ezy.ezybagapp.model.repository.BookingRepository
import bag.ezy.ezybagapp.model.repository.ContractorRepository
import kotlinx.coroutines.launch

data class ContractorDetailsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val actionError: String? = null,
    val isUpdating: Boolean = false,
    val contractor: Contractor? = null,
    val totalJobs: Int = 0,
    val activeJobs: Int = 0,
    val completedJobs: Int = 0
) {
    val completionRate: Int
        get() = if (totalJobs == 0) 0 else completedJobs * 100 / totalJobs
}

private val activeStatuses = setOf("Accepted", "En route", "On site")

class ContractorDetailsAdminViewModel(
    private val contractorId: String,
    private val contractors: ContractorRepository,
    private val bookings: BookingRepository,
    private val admin: AdminRepository
) : ViewModel() {
    var state by mutableStateOf(ContractorDetailsUiState())
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            val isAdmin = admin.isCurrentUserAdmin().getOrDefault(false)
            if (!isAdmin) {
                state = state.copy(isLoading = false,
                    error = "This account does not have access to this page")
                return@launch
            }

            contractors.getContractor(contractorId)
                .onSuccess { contractor ->
                    if (contractor == null) {
                        state = state.copy(isLoading = false, error = "Contractor not found")
                        return@onSuccess
                    }
                    val jobs = bookings
                        .getBookingsForContractor(contractorId)
                        .getOrDefault(emptyList())
                    state = state.copy(
                        isLoading = false,
                        contractor = contractor,
                        totalJobs = jobs.size,
                        activeJobs = jobs.count { it.status in activeStatuses },
                        completedJobs = jobs.count { it.status.equals(
                            "Completed", ignoreCase = true
                        ) }
                    )
                }
                .onFailure {
                    state = state.copy(
                        isLoading = false,
                        error = it.message
                            ?: "Couldn't load contractor"
                    ) }
        }
    }

    fun toggleApproval() {
        val contractor = state.contractor ?: return
        val newStatus = !contractor.isApproved

        viewModelScope.launch {
            state = state.copy(isUpdating = true, actionError = null)
            contractors.changeContractorStatus(contractor.uid, newStatus)
                .onSuccess {
                    state = state.copy(
                        isUpdating = false,
                        contractor = contractor.copy(isApproved = newStatus))
                }
                .onFailure {
                    state = state.copy(
                        isUpdating = false,
                        actionError = it.message ?: "Couldn't update status")
                }
        }
    }
}