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

    //revenue
    val totalRevenue: Double? = null,

    //errors when retrieval of numbers is not possible
    val contractorError: String? = null,
    val bookingError: String? = null,
    val issueCustomer: String? = null,
    val issueError: String? = null,
    val revenueError: String? = null,

    // for actions
    val totalUnresolvedIssues: Int? = null,
    val totalUnverifiedContractors: Int? = null,

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

            //it is loading here
            state = state.copy(isLoading = true)

            val name = auth.getCurrentUserName()
            val bookingCountResult = bookings.countAllBookings()
            val contractorCountResult = contractors.countAllContractors()
            val contractorUnverifiedCountResult = contractors.countUnverifiedContractors()
            val issueCountResult = issues.countUnresolvedIssues()
            val customersCountResult = customers.countCustomers()
            val revenueCountResult = revenue.countRevenue()

            state = state.copy(
                userName = name,

                totalBookings = bookingCountResult.getOrNull(),
                bookingError = bookingCountResult.exceptionOrNull()?.message,

                totalContractors = contractorCountResult.getOrNull(),
                contractorError = contractorCountResult.exceptionOrNull()?.message,
                totalUnverifiedContractors = contractorUnverifiedCountResult.getOrNull(),

                totalUnresolvedIssues = issueCountResult.getOrNull(),
                issueError = issueCountResult.exceptionOrNull()?.message,

                totalCustomers = customersCountResult.getOrNull(),
                issueCustomer = customersCountResult.exceptionOrNull()?.message,

                totalRevenue = revenueCountResult.getOrNull(),
                revenueError = revenueCountResult.exceptionOrNull()?.message ,

                //when done, finish loading
                isLoading = false,
            )
        }
    }
}