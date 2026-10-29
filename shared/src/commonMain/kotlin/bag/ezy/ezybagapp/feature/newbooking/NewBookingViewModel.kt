
package bag.ezy.ezybagapp.feature.newbooking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.data.Address
import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.data.Media
import bag.ezy.ezybagapp.model.data.Pricing
import bag.ezy.ezybagapp.model.data.Schedule
import bag.ezy.ezybagapp.model.data.WasteDetails
import bag.ezy.ezybagapp.model.repository.AuthRepository
import bag.ezy.ezybagapp.model.repository.BookingRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

data class NewBookingState(
    // Collection details
    val address: String = "",
    val postcode: String = "",
    val phone: String = "",
    val wasteType: String = "Construction",
    val volume: String = "Half van",
    val description: String = "",
    val mediaFileNames: List<String> = emptyList(),

    // Schedule and collection preferences
    val dateMillis: Long? = null,
    val hour: Int = 9,
    val minute: Int = 0,
    val accessInfo: String = "",
    val specialInstructions: String = "",
    val needsBag: Boolean = true,
    val bagSize: String = "Medium",
    val estimate: Double? = null,

    // UI state
    val isSubmitting: Boolean = false,
    val quoteRequested: Boolean = false,
    val error: String? = null
)

class NewBookingViewModel(
    private val repo: BookingRepository,
    private val auth: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(NewBookingState())
        private set

    fun onAddressChange(value: String) {
        state = state.copy(address = value, error = null)
    }

    fun onPostcodeChange(value: String) {
        state = state.copy(postcode = value, error = null)
    }

    fun onPhoneChange(value: String) {
        state = state.copy(phone = value, error = null)
    }

    fun onDescriptionChange(value: String) {
        state = state.copy(description = value, error = null)
    }

    fun onWasteTypeSelected(value: String) {
        state = state.copy(wasteType = value, error = null)
    }

    fun onVolumeSelected(value: String) {
        state = state.copy(volume = value, error = null)
    }

    fun onFilesPicked(names: List<String>) {
        state = state.copy(
            mediaFileNames = (state.mediaFileNames + names).distinct(),
            error = null
        )
    }

    fun onFileRemoved(name: String) {
        state = state.copy(
            mediaFileNames = state.mediaFileNames - name,
            error = null
        )
    }

    // Classify video filenames by their file extension.
    // Files with other extensions are treated as photos.
    private fun isVideoFile(name: String): Boolean {
        return name.substringAfterLast('.', "")
            .lowercase() in setOf(
            "mp4",
            "mov",
            "m4v",
            "webm",
            "avi"
        )
    }

    fun continueFromDetails(onValid: () -> Unit) {
        val current = state

        val validationError = when {
            current.address.isBlank() ->
                "Please enter the collection address."

            current.postcode.isBlank() ->
                "Please enter the postcode."

            current.phone.isBlank() ->
                "Please enter your phone number."

            else -> null
        }

        if (validationError != null) {
            state = state.copy(error = validationError)
            return
        }

        viewModelScope.launch {
            state = state.copy(error = null)

            try {
                val currentState = state

                val estimate = repo.getEstimate(
                    currentState.wasteType,
                    currentState.volume
                )

                state = state.copy(estimate = estimate)
                onValid()
            } catch (exception: Exception) {
                state = state.copy(
                    error = exception.message
                        ?: "Could not calculate the estimate."
                )
            }
        }
    }

    // Schedule and collection preferences
    fun onDateSelected(millis: Long?) {
        state = state.copy(dateMillis = millis, error = null)
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        state = state.copy(
            hour = hour.coerceIn(0, 23),
            minute = minute.coerceIn(0, 59),
            error = null
        )
    }

    fun onAccessInfoChange(value: String) {
        state = state.copy(accessInfo = value, error = null)
    }

    fun onInstructionChange(value: String) {
        state = state.copy(
            specialInstructions = value,
            error = null
        )
    }

    fun onNeedsBagChange(value: Boolean) {
        state = state.copy(needsBag = value, error = null)
    }

    fun onBagSizeChange(value: String) {
        state = state.copy(bagSize = value, error = null)
    }

    // Test flow: record the quote request locally.
    // No Firestore update is attempted until a booking exists.
    fun requestQuote() {
        state = state.copy(
            quoteRequested = true,
            error = null
        )
    }

    // TEST ONLY: simulate payment by saving a booking.
    // No real payment is processed.
    fun pay(onBooked: (bookingId: String) -> Unit) {
        if (state.isSubmitting) return

        val current = state

        val validationError = when {
            current.address.isBlank() ||
                    current.postcode.isBlank() ||
                    current.phone.isBlank() ->
                "Please complete the address, postcode and phone number."

            current.dateMillis == null ->
                "Please choose a collection date."

            current.estimate == null ->
                "Please calculate the booking estimate first."

            else -> null
        }

        if (validationError != null) {
            state = state.copy(error = validationError)
            return
        }

        viewModelScope.launch {
            state = state.copy(
                isSubmitting = true,
                error = null
            )

            try {
                val customerId = auth.currentUserId()

                if (customerId.isNullOrBlank()) {
                    state = state.copy(
                        isSubmitting = false,
                        error = "Please sign in before creating a booking."
                    )
                    return@launch
                }

                val result = repo.createBooking(
                    toBooking(customerId = customerId)
                )

                result.fold(
                    onSuccess = { bookingId ->
                        state = state.copy(
                            isSubmitting = false,
                            error = null
                        )

                        // Continue only after the booking is saved.
                        onBooked(bookingId)
                    },
                    onFailure = { exception ->
                        state = state.copy(
                            isSubmitting = false,
                            error = exception.message
                                ?: "Could not save the booking."
                        )
                    }
                )
            } catch (exception: Exception) {
                state = state.copy(
                    isSubmitting = false,
                    error = exception.message
                        ?: "Could not save the booking."
                )
            }
        }
    }

    fun reset() {
        state = NewBookingState()
    }

    private fun toBooking(
        customerId: String,
        quoteRequested: Boolean = state.quoteRequested
    ): Booking {
        val current = state
        val phone = current.phone.trim()
        val access = current.accessInfo.trim()

        return Booking(
            customerId = customerId,

            collectionAddress = Address(
                street = current.address.trim(),
                postcode = current.postcode.trim()
            ),

            customerPhone = phone,

            accessInfo = access,

            specialInstructions = current.specialInstructions.trim(),

            wasteDetails = WasteDetails(
                type = current.wasteType,
                description = current.description.trim(),
                estimatedVolume = current.volume,
                bagRequired = current.needsBag,
                bagSize = if (current.needsBag) {
                    current.bagSize
                } else {
                    null
                }
            ),

            // Store filenames separately; no file upload occurs.
            media = Media(
                photos = current.mediaFileNames.filterNot {
                    isVideoFile(it)
                },
                videos = current.mediaFileNames.filter {
                    isVideoFile(it)
                }
            ),

            schedule = Schedule(
                preferredDate = current.dateMillis?.let {
                    isoDate(it)
                } ?: "",
                preferredTime = formatTime(
                    current.hour,
                    current.minute
                )
            ),

            pricing = Pricing(
                estimatedQuote = current.estimate ?: 0.0,
                currency = "GBP",
                finalQuoteRequested = quoteRequested
            )
        )
    }

    private fun isoDate(epochMillis: Long): String =
        Instant.fromEpochMilliseconds(epochMillis)
            .toLocalDateTime(TimeZone.UTC)
            .date
            .toString()

    private fun formatTime(hour: Int, minute: Int): String =
        "${hour.toString().padStart(2, '0')}:" +
                minute.toString().padStart(2, '0')
}
