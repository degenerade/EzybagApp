
package bag.ezy.ezybagapp.feature.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.repository.AuthRepository
import bag.ezy.ezybagapp.model.repository.BookingRepository
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val userName: String = "",
    val totalBookings: Int? = null,
    val isLoading: Boolean = true,
    val bookingError: String? = null
)

class AdminDashboardViewModel(
    private val auth: AuthRepository = AppContainer.authRepository,
    private val bookings: BookingRepository = AppContainer.bookingRepository
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

            val result = bookings.countAllBookings()

            state = if (result.isSuccess) {
                state.copy(
                    totalBookings = result.getOrNull(),
                    bookingError = null
                )
            } else {
                state.copy(
                    totalBookings = null,
                    bookingError = result.exceptionOrNull()?.message
                        ?: "Could not load bookings"
                )
            }
        }
    }
}
