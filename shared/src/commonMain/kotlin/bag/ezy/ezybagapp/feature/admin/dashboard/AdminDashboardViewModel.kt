package bag.ezy.ezybagapp.feature.admin.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.repository.AuthRepository
import bag.ezy.ezybagapp.model.repository.BookingRepository
import bag.ezy.ezybagapp.model.repository.ContractorRepository
import bag.ezy.ezybagapp.model.repository.IssueRepository
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val userName: String = "",
    val totalBookings: Int? = null,
    val isLoading: Boolean = true,
    val bookingError: String? = null,
    val totalContractors: Int? = null,
    val contractorError: String? = null,
    val totalUnresolvedIssues: Int? = null,
    val issueError: String? = null,
)

class AdminDashboardViewModel(
    private val auth: AuthRepository = AppContainer.authRepository,
    private val bookings: BookingRepository = AppContainer.bookingRepository,
    private val contractors: ContractorRepository = AppContainer.contractorRepository,
    private val issues: IssueRepository = AppContainer.issueRepository,
) : ViewModel() {

    var state by mutableStateOf(AdminDashboardUiState())
        private set

    fun load() {
        viewModelScope.launch {
            val name = auth.getCurrentUserName()

            state = state.copy(
                userName = name,
                isLoading = false
            )

            val bookingCountResult = bookings.countAllBookings()
            val contractorCountResult = contractors.countAllContractors()
            val issueCountResult = issues.countUnresolvedIssues()

            state = if (bookingCountResult.isSuccess && contractorCountResult.isSuccess) {
                state.copy(
                    totalBookings = bookingCountResult.getOrNull(),
                    totalContractors = contractorCountResult.getOrNull(),
                )
            } else if (bookingCountResult.isFailure) {
                state.copy(
                    bookingError = bookingCountResult.exceptionOrNull()?.message
                        ?: "Could not load bookings"
                )
            } else {
                state.copy(
                    contractorError = contractorCountResult.exceptionOrNull()?.message
                        ?: "Could not load contractors"
                )
            }
            state = if (issueCountResult.isSuccess) {
                state.copy(
                    totalUnresolvedIssues = issueCountResult.getOrNull(),
                    issueError = null
                )
            } else {
                state.copy(
                    issueError = issueCountResult.exceptionOrNull()?.message
                        ?: "Could not load issues"
                )
            }
        }
    }
}