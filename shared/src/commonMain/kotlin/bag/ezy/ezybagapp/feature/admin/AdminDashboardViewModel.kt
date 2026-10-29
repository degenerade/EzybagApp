package bag.ezy.ezybagapp.feature.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.repository.AuthRepository
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val userName: String = "",
    val isLoading: Boolean = true
)

class AdminDashboardViewModel(
    private val auth: AuthRepository = AppContainer.authRepository
) : ViewModel() {
    var state by mutableStateOf(AdminDashboardUiState())
        private set

    fun load() {
        viewModelScope.launch {
            val name = auth.getCurrentUserName()
            state = state.copy(userName = name, isLoading = false)
        }
    }
}
