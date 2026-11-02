package bag.ezy.ezybagapp.feature.contractoraccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.data.Contractor
import bag.ezy.ezybagapp.model.repository.ContractorRepository
import bag.ezy.ezybagapp.model.repository.FirebaseContractorRepository
import kotlinx.coroutines.launch

data class ContractorAccountUiState(
    val contractor: Contractor? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ContractorAccountViewModel(
    private val repository: ContractorRepository =
        FirebaseContractorRepository()
) : ViewModel() {

    var state by mutableStateOf(ContractorAccountUiState())
        private set

    fun load() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            repository.getCurrentContractor()
                .onSuccess { contractor ->
                    if (contractor == null) {
                        state = ContractorAccountUiState(
                            isLoading = false,
                            error = "Contractor profile could not be found."
                        )
                    } else {
                        state = ContractorAccountUiState(
                            contractor = contractor,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    state = ContractorAccountUiState(
                        isLoading = false,
                        error = error.message
                            ?: "Unable to load contractor account."
                    )
                }
        }
    }
}