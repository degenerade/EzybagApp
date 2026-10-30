package bag.ezy.ezybagapp.feature.admin.customers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.data.Customer
import bag.ezy.ezybagapp.model.queries.AdminRepository
import bag.ezy.ezybagapp.model.repository.CustomerRepository
import kotlinx.coroutines.launch

data class CustomersAdminUiModel(
    val customerId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val createdAt: String = ""
) {
    val fullName: String get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

data class CustomersAdminUiState(
    val isLoading: Boolean = true,
    val customers: List<CustomersAdminUiModel> = emptyList(),
   // val selectedTab: CustomersTab = CustomersTab.All,
    val error: String? = null
)// {
//    val visibleCustomers: List<CustomersAdminUiModel>
//        get() = if (selectedTab == CustomersTab.All) {
//            customers.filter { it.tab != null }
//        } else {
//            customers.filter { it.tab == selectedTab }
//        }

//    val cancelledCount: Int
//        get() = customers.count { it.tab == null }
//}

class CustomersAdminViewModel(
    private val repo: CustomerRepository = AppContainer.customerRepository,
    private val auth: AdminRepository = AppContainer.adminRepository
) : ViewModel() {
    var state by mutableStateOf(CustomersAdminUiState())
        private set

    fun load() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            try {
                repo.getAllCustomers().fold(
                    onSuccess = { customers ->
                        state = state.copy(
                            isLoading = false,
                            customers = customers.map { it.toUiModel() },
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        state = state.copy(
                            isLoading = false,
                            error = exception.message
                                ?: "Couldn't load customers"
                        )

                    }
                )
            } catch (exception: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = exception.message
                        ?: "Couldn't load customers."
                )
            }
        }
    }

//    fun selectTab(tab: CustomersTab) {
//        state = state.copy(selectedTab = tab)
//    }

    private fun Customer.toUiModel() = CustomersAdminUiModel(
        customerId = customerId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        createdAt = createdAt
    )
}
