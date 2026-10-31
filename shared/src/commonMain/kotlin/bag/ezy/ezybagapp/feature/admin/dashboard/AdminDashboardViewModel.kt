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
import bag.ezy.ezybagapp.model.repository.CustomerRepository
import bag.ezy.ezybagapp.model.repository.IssueRepository
import bag.ezy.ezybagapp.model.repository.RevenueRepository
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    //username
    val userName: String = "",

    //numbers on dashboard
    val totalBookings: Int? = null,
    val totalContractors: Int? = null,
    val totalCustomers: Int? = null,
    val totalRevenue: Double? = null,

    //errors when retrieval of numbers is not possible
    val contractorError: String? = null,
    val bookingError: String? = null,
    val issueCustomer: String? = null,
    val issueError: String? = null,
    val revenueError: String? = null,

    // for actions
    val totalUnresolvedIssues: Int? = null,

    //?
    val isLoading: Boolean = true,

)

class AdminDashboardViewModel(
    private val auth: AuthRepository = AppContainer.authRepository,
    private val bookings: BookingRepository = AppContainer.bookingRepository,
    private val contractors: ContractorRepository = AppContainer.contractorRepository,
    private val issues: IssueRepository = AppContainer.issueRepository,
    private val customers: CustomerRepository = AppContainer.customerRepository,
    private val revenue: RevenueRepository = AppContainer.revenueRepository
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
            val customersCountResult = customers.countCustomers()
            val revenueCountResult = revenue.countRevenue()

            state = if (bookingCountResult.isSuccess && contractorCountResult.isSuccess) {
                state.copy(
                    totalBookings = bookingCountResult.getOrNull(),
                    totalContractors = contractorCountResult.getOrNull()
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

            //customer count
            state = if (customersCountResult.isSuccess) {
                state.copy(
                    totalCustomers = customersCountResult.getOrNull(),
                    issueCustomer = null
                )
            } else {
                state.copy(
                    issueCustomer = customersCountResult.exceptionOrNull()?.message
                        ?: "Could not load customers"
                )
            }

            //revenue count
            state = if (revenueCountResult.isSuccess) {
                state.copy(
                    totalRevenue = revenueCountResult.getOrNull(),
                    revenueError = null
                )
            } else {
                state.copy(
                    revenueError = revenueCountResult.exceptionOrNull()?.message
                        ?: "Could not load revenue"
                )
            }
        }
    }
}
