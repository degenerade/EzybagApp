package bag.ezy.ezybagapp.feature.support

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.repository.IssueReport
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock

val issueCategories = listOf(
    "Booking",
    "Payment",
    "App problem",
    "Contractor",
    "Account",
    "Other"
)

data class DisplayIssueReport(
    val id: String,
    val report: IssueReport
)

data class SupportUiState(
    val selectedCategory: String = "Booking",
    val description: String = "",
    val categoryMenuExpanded: Boolean = false,
    val submitting: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
    val reports: List<DisplayIssueReport> = emptyList(),
    val loadingReports: Boolean = false,
    val reportsError: String? = null
)

class SupportViewModel : ViewModel() {

    var state by mutableStateOf(SupportUiState())
        private set

    fun loadReports() {
        val userId = Firebase.auth.currentUser?.uid

        if (userId.isNullOrBlank()) {
            state = state.copy(
                reports = emptyList(),
                reportsError = "Please sign in to view your reports.",
                loadingReports = false
            )
            return
        }

        viewModelScope.launch {
            state = state.copy(loadingReports = true, reportsError = null)

            try {
                val snapshot = Firebase.firestore
                    .collection("issueReports")
                    .where {
                        "customerId" equalTo userId
                    }
                    .get()

                val loadedReports = snapshot.documents.mapNotNull { document ->
                    runCatching {
                        DisplayIssueReport(
                            id = document.id,
                            report = document.data<IssueReport>()
                        )
                    }.getOrNull()
                }.sortedByDescending { it.report.createdAt }

                state = state.copy(
                    reports = loadedReports,
                    loadingReports = false,
                    reportsError = null
                )
            } catch (_: Exception) {
                state = state.copy(
                    loadingReports = false,
                    reportsError = "Could not load your reports. Please try again."
                )
            }
        }
    }

    fun onCategorySelected(category: String) {
        state = state.copy(selectedCategory = category, categoryMenuExpanded = false)
    }

    fun setCategoryMenuExpanded(expanded: Boolean) {
        state = state.copy(categoryMenuExpanded = expanded)
    }

    fun onDescriptionChange(text: String) {
        state = state.copy(description = text, message = null)
    }

    fun submitReport() {
        val userId = Firebase.auth.currentUser?.uid
        val issueDescription = state.description.trim()

        when {
            userId.isNullOrBlank() -> {
                state = state.copy(
                    isError = true,
                    message = "Please sign in before submitting a report."
                )
            }

            issueDescription.length < 10 -> {
                state = state.copy(
                    isError = true,
                    message = "Please describe the issue in at least 10 characters."
                )
            }

            issueDescription.length > 3000 -> {
                state = state.copy(
                    isError = true,
                    message = "The description cannot exceed 3,000 characters."
                )
            }

            else -> {
                viewModelScope.launch {
                    state = state.copy(submitting = true, message = null)

                    try {
                        val reportId = "report-${Random.nextLong()}"

                        Firebase.firestore
                            .collection("issueReports")
                            .document(reportId)
                            .set(
                                mapOf(
                                    "customerId" to userId,
                                    "category" to state.selectedCategory,
                                    "description" to issueDescription,
                                    "status" to "Open",
                                    "createdAt" to Clock.System.now().toEpochMilliseconds()
                                )
                            )

                        state = state.copy(
                            submitting = false,
                            description = "",
                            isError = false,
                            message = "Your report was submitted successfully."
                        )

                        loadReports()
                    } catch (e: Exception) {
                        state = state.copy(
                            submitting = false,
                            isError = true,
                            message = "Could not submit your report: " + (e.message ?: "Please try again.")
                        )
                    }
                }
            }
        }
    }
}
