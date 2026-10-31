package bag.ezy.ezybagapp.feature.admin.contractors

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Contractor
import bag.ezy.ezybagapp.model.queries.AdminRepository
import bag.ezy.ezybagapp.model.repository.ContractorRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class ContractorsTab {
    All,
    Active,
    Pending
}

data class ContractorsAdminUiModel(
    val reference: String,
    val companyName: String,
    val email: String,
    val isApproved: Boolean,
    val tab: ContractorsTab?
)

data class ContractorsAdminUiState(
    val isLoading: Boolean = true,
    val contractors: List<ContractorsAdminUiModel> = emptyList(),
    val selectedTab: ContractorsTab = ContractorsTab.All,
    val error: String? = null
) {
    val visibleContractors: List<ContractorsAdminUiModel>
        get() = if (selectedTab == ContractorsTab.All) {
            contractors.filter { it.tab != null }
        } else {
            contractors.filter { it.tab == selectedTab }
        }

    val cancelledCount: Int
        get() = contractors.count { it.tab == null }
}

class ContractorsAdminViewModel(
    private val repo: ContractorRepository = AppContainer.contractorRepository,
    private val auth: AdminRepository = AppContainer.adminRepository
) : ViewModel() {
    var state by mutableStateOf(ContractorsAdminUiState())
        private set

    fun load() {
        val isAdmin = runBlocking {
            auth.isCurrentUserAdmin()
        }.getOrNull() ?: false
        if (!isAdmin) {
            state = state.copy(
                isLoading = false,
                error = "This account does not have access to this page."
            )
            return
        }

        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            try {
                repo.getAllContractors().fold(
                    onSuccess = { contractors ->
                        state = state.copy(
                            isLoading = false,
                            contractors = contractors.map { it.toUiModel() },
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        state = state.copy(
                            isLoading = false,
                            error = exception.message
                                ?: "Couldn't load contractors"
                        )

                    }
                )
            } catch (exception: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = exception.message
                        ?: "Couldn't load contractors."
                )
            }
        }
    }

    fun selectTab(tab: ContractorsTab) {
        state = state.copy(selectedTab = tab)
    }

    fun toggleApprovalStatus(contractorId: String, currentApproved: Boolean) {
        if (contractorId.isBlank()) return
        val newStatus = !currentApproved

        // Update UI state immediately for instant visual feedback
        state = state.copy(
            contractors = state.contractors.map {
                if (it.reference == contractorId) {
                    it.copy(
                        isApproved = newStatus,
                        tab = if (newStatus) ContractorsTab.Active else ContractorsTab.Pending
                    )
                } else {
                    it
                }
            }
        )

        viewModelScope.launch {
            repo.changeContractorStatus(contractorId, newStatus).fold(
                onSuccess = {
                    load()
                },
                onFailure = { exception ->
                    state = state.copy(
                        error = exception.message ?: "Failed to update contractor status."
                    )
                    load()
                }
            )
        }
    }

    fun deleteContractor(contractorId: String) {
        if (contractorId.isBlank()) return
        state = state.copy(
            contractors = state.contractors.filter { it.reference != contractorId }
        )
        viewModelScope.launch {
            repo.deleteContractor(contractorId).fold(
                onSuccess = {
                    load()
                },
                onFailure = { exception ->
                    state = state.copy(
                        error = exception.message ?: "Failed to delete contractor."
                    )
                    load()
                }
            )
        }
    }

    private fun Contractor.toUiModel() = ContractorsAdminUiModel(
        reference = uid,
        companyName = companyName,
        email = email,
        isApproved = isApproved,
        tab = when (isApproved) {
            true -> ContractorsTab.Active
            false -> ContractorsTab.Pending
        }
    )
}