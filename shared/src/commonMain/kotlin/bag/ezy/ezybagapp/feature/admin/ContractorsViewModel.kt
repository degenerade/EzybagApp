package bag.ezy.ezybagapp.feature.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.repository.ContractorRepository
import kotlinx.coroutines.launch

class ContractorsViewModel(
    private val repo: ContractorRepository = AppContainer.contractorRepository
) : ViewModel() {
    var responseText by mutableStateOf("Contractors will be here.. one day :D")
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun testGetAllContractors() {
        viewModelScope.launch {
            isLoading = true
            repo.getAllContractors()
                .onSuccess { list -> responseText = list.toString() }
                .onFailure { e -> responseText = "Error: ${e.message}" }
            isLoading = false
        }
    }
}
