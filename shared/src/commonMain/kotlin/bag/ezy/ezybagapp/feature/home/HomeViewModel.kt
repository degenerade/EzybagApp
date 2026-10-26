package bag.ezy.ezybagapp.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.repository.AuthRepository
import bag.ezy.ezybagapp.model.repository.BookingRepository
import bag.ezy.ezybagapp.ui.format.formatDisplayDate
import bag.ezy.ezybagapp.ui.format.formatDisplayTime
import bag.ezy.ezybagapp.ui.format.formatPrice
import kotlinx.coroutines.launch

enum class HomeBookingFilter { Upcoming, Completed }

data class HomeBookingUiModel(
    val id: String,
    val title: String,
    val address: String,
    val status: String,
    val dateTime: String,
    val price: String,
    val filter: HomeBookingFilter?,
)

data class HomeUiState(
    val userName: String = "",
    val isLoading: Boolean = true,
    val bookings: List<HomeBookingUiModel> = emptyList(),
    val filter: HomeBookingFilter = HomeBookingFilter.Upcoming,
    val error: String?= null,
) {
    val visibleBookings: List<HomeBookingUiModel>
        get() = bookings.filter { it.filter == filter }
}

class HomeViewModel(
    private val auth: AuthRepository,
    private val repo: BookingRepository,
) : ViewModel() {
    var state by mutableStateOf(HomeUiState())
        private set

    fun load() {
        val customerId = auth.currentUserId()
        if (customerId.isNullOrBlank()) {
            state = state.copy(isLoading = false, error = "Please sign in to see your bookings.")
            return
        }
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            val name = auth.getCurrentUserName()
            repo.getBookings(customerId)
                .onSuccess { list ->
                    state = state.copy(isLoading = false, userName = name, bookings = list.map { it.toUi() })
                }
                .onFailure { state = state.copy(isLoading = false, userName = name, error = it.message ?: "Couldn't load bookings") }
        }
    }

    fun selectFilter(filter: HomeBookingFilter) { state = state.copy(filter = filter) }

    private fun Booking.toUi() = HomeBookingUiModel(
        id = bookingId,
        title = listOf(wasteDetails.type, wasteDetails.estimatedVolume).filter { it.isNotBlank() }.joinToString(" · "),
        address = listOf(collectionAddress.street, collectionAddress.postcode).filter { it.isNotBlank() }.joinToString(", "),
        status = status,
        dateTime = listOf(formatDisplayDate(schedule.preferredDate), formatDisplayTime(schedule.preferredTime))
            .filter { it.isNotBlank() }.joinToString(" · "),
        price = if (pricing.estimatedQuote > 0) formatPrice(pricing.estimatedQuote) else "",
        filter = when (status) {
            "Completed" -> HomeBookingFilter.Completed
            "Cancelled" -> null
            else -> HomeBookingFilter.Upcoming
        },
    )
}