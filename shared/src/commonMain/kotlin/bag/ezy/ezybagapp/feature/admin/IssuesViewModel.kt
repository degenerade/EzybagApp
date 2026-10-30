package bag.ezy.ezybagapp.feature.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.repository.AdminIssue
import bag.ezy.ezybagapp.model.repository.IssueRepository
import kotlinx.coroutines.launch

data class IssuesUiState(
    val issues: List<AdminIssue> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class IssuesViewModel(
    private val repository: IssueRepository = AppContainer.issueRepository
) : ViewModel() {

    var state by mutableStateOf(IssuesUiState())
        private set

    fun loadIssues() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            val result = repository.getAllIssues()

            state = if (result.isSuccess) {
                state.copy(
                    issues = result.getOrNull() ?: emptyList(),
                    isLoading = false
                )
            } else {
                state.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                        ?: "Could not load issues."
                )
            }
        }
    }

    fun changeIssueStatus(issueId: String, status: String) {
        viewModelScope.launch {
            val result = repository.changeIssueStatus(issueId, status)

            if (result.isSuccess) {
                loadIssues()
            } else {
                state = state.copy(
                    error = result.exceptionOrNull()?.message
                        ?: "Could not update issue."
                )
            }
        }
    }
}