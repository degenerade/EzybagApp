package bag.ezy.ezybagapp.feature.newbooking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.data.Pricing
import bag.ezy.ezybagapp.model.data.Schedule
import bag.ezy.ezybagapp.model.data.WasteDetails
import bag.ezy.ezybagapp.model.repository.BookingRepository
import kotlinx.coroutines.launch
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
data class NewBookingState(
    //details
    val address: String = "",
    val postcode: String = "",
    val phone: String = "",
    val wasteType: String = "Construction",
    val volume: String = "Half van",
    val description: String = "",
    val mediaFileNames: List<String> = emptyList(),
    //schedule, bag, quote
    val dateMillis: Long? = null,
    val hour: Int = 9,
    val minute: Int = 0,
    val accessInfo: String = "Side gate, code 1234",
    val specialInstructions: String = "Call on arrival",
    val needsBag: Boolean = true,
    val bagSize: String = "Medium",
    val estimate: Double? = null,
    //status
    val isSubmitting: Boolean = false,
    val quoteRequested: Boolean = false,
    val error: String? = null
)

class NewBookingViewModel (
    private val repo: BookingRepository
) : ViewModel() {
    var state by mutableStateOf(NewBookingState())
        private set

    fun onAddressChange(v: String) { state = state.copy(address = v, error = null) }
    fun onPostcodeChange(v: String) { state = state.copy(postcode = v, error = null) }
    fun onPhoneChange(v: String) { state = state.copy(phone = v, error = null) }
    fun onDescriptionChange(v: String) { state = state.copy(description = v) }
    fun onWasteTypeSelected(v: String) { state = state.copy(wasteType = v) }
    fun onVolumeSelected(v: String) { state = state.copy(volume = v) }

    fun onFilesPicked(names: List<String>) {
        state = state.copy(mediaFileNames = (state.mediaFileNames + names).distinct(), error = null)
    }
    fun onFileRemoved(name: String) {
        state = state.copy(mediaFileNames = state.mediaFileNames - name)
    }

    fun continueFromDetails(onValid: () -> Unit) {
        val error = when {
            state.address.isBlank() -> "Please enter the collection address."
            state.postcode.isBlank() -> "Please enter the postcode."
            state.phone.isBlank() -> "Please enter your phone number."
            else -> null
        }
        if (error != null) {
            state = state.copy(error = error)
            return
        }
        viewModelScope.launch {
            state = state.copy(error = null, estimate = repo.getEstimate(state.wasteType, state.volume))
            onValid()
        }
    }

    fun onDateSelected(millis: Long?) { state = state.copy(dateMillis = millis, error = null) }
    fun onTimeSelected(hour: Int, minute: Int) { state = state.copy(hour = hour, minute = minute) }
    fun onAccessInfoChange(v: String) { state = state.copy(accessInfo = v) }
    fun onInstructionChange(v: String) { state = state.copy(specialInstructions = v) }
    fun onNeedsBagChange(v: Boolean) { state = state.copy(needsBag = v) }
    fun onBagSizeChange(v: String) { state = state.copy(bagSize = v) }

    fun requestQuote() {
        viewModelScope.launch {
            repo.requestFinalQuote(toBooking())
                .onSuccess { state = state.copy(quoteRequested = true) }
                .onFailure { state = state.copy(error = it.message) }
        }
    }

    fun pay(onBooked: (bookingId: String) -> Unit ) {
        if (state.dateMillis == null) {
            state = state.copy(error = "Please choose a collection date.")
            return
        }
        viewModelScope.launch {
            state = state.copy(isSubmitting = true, error = null)
            repo.createBooking(toBooking())
                .onSuccess { id -> state = state.copy(isSubmitting = false); onBooked(id) }
                .onFailure { state = state.copy(isSubmitting = false, error = it.message ?: "Booking failed") }
        }
    }

    //fresh booking
    fun reset() { state = NewBookingState() }

    private fun toBooking() = Booking(
        wasteDetails = WasteDetails(
            type = state.wasteType,
            description = state.description,
            estimatedVolume = state.volume,
            bagRequired = state.needsBag,
            bagSize = if (state.needsBag) state.bagSize else null
        ),
        schedule = Schedule(
            preferredDate = (state.dateMillis?.let { isoDate(it) } ?: ""),
            preferredTime = "${state.hour.toString().padStart(2, '0')}:${state.minute.toString().padStart(2, '0')}",
        ),
        pricing = Pricing(estimatedQuote = state.estimate ?: 0.0),
    )

    private fun isoDate(epochMillis: Long): String =
        Instant.fromEpochMilliseconds(epochMillis)
            .toLocalDateTime(TimeZone.UTC)
            .date
            .toString()
}