package bag.ezy.ezybagapp.feature.ratings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.repository.ReviewRepository
import kotlinx.coroutines.launch

data class RatingsUiState(
    val rating: Int = 0,
    val comment: String = "",
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
)

class RatingsViewModel(
    private val bookingId: String,
    private val repo: ReviewRepository,
    private val contractorId: String = "",
) : ViewModel() {
    var state by mutableStateOf(RatingsUiState())
        private set

    fun onRatingSelected(stars: Int) { state = state.copy(rating = stars, message = null) }
    fun onCommentChange(text: String) { state = state.copy(comment = text, message = null) }

    fun submit() {
        val error = when {
            state.rating == 0 -> "Please select a star rating."
            state.comment.isBlank() -> "Please write a review comment."
            else -> null
        }
        if (error != null) {
            state = state.copy(message = error, isError = true)
            return
        }
        viewModelScope.launch {
            state = state.copy(isSubmitting = true, message = null)
            repo.submitReview(
                bookingId = bookingId,
                rating = state.rating,
                comment = state.comment.trim(),
                contractorId = contractorId
            )
                .onSuccess {
                    state = state.copy(isSubmitting = false, submitted = true,
                        message = "thanks for your review!", isError = false)
                }
                .onFailure {
                    state = state.copy(isSubmitting = false,
                        message = it.message ?: "Couldn't submit review", isError = true)
                }
        }
    }
}