
package bag.ezy.ezybagapp.feature.bookings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.repository.AuthRepository
import bag.ezy.ezybagapp.model.repository.BookingRepository
import bag.ezy.ezybagapp.ui.format.formatDisplayDate
import kotlinx.coroutines.launch

enum class BookingTab {
    All,
    Upcoming,
    Active,
    Completed
}

data class BookingUiModel(
    val reference: String,
    val wasteType: String,
    val status: String,
    val date: String,
    val tab: BookingTab?
)

data class BookingsUiState(
    val isLoading: Boolean = true,
    val bookings: List<BookingUiModel> = emptyList(),
    val selectedTab: BookingTab = BookingTab.All,
    val error: String? = null
) {
    val visibleBookings: List<BookingUiModel>
        get() = if (selectedTab == BookingTab.All) {
            bookings.filter { it.tab != null }
        } else {
            bookings.filter { it.tab == selectedTab }
        }

    val cancelledCount: Int
        get() = bookings.count { it.tab == null }
}

class BookingsViewModel(
    private val auth: AuthRepository,
    private val repo: BookingRepository
) : ViewModel() {

    var state by mutableStateOf(BookingsUiState())
        private set

    fun load() {
        val customerId = auth.currentUserId()
        if (customerId.isNullOrBlank()) {
            state = state.copy(
                isLoading = false,
                error = "Please sign in to view your bookings."
            )
            return
        }

        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            try {
                repo.getBookings(customerId).fold(
                    onSuccess = { bookings ->
                        state = state.copy(
                            isLoading = false,
                            bookings = bookings.map { it.toUiModel() },
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        state = state.copy(
                            isLoading = false,
                            error = exception.message
                                ?: "Couldn't load bookings."
                        )
                    }
                )
            } catch (exception: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = exception.message
                        ?: "Couldn't load bookings."
                )
            }
        }
    }

    fun selectTab(tab: BookingTab) {
        state = state.copy(selectedTab = tab)
    }

    private fun Booking.toUiModel() = BookingUiModel(
        reference = bookingId,
        wasteType = wasteDetails.type,
        status = status,
        date = formatDisplayDate(schedule.preferredDate),
        tab = when (status) {
            "En route", "On site" -> BookingTab.Active
            "Completed" -> BookingTab.Completed
            "Cancelled" -> null
            else -> BookingTab.Upcoming
        }
    )
}
