package bag.ezy.ezybagapp.feature.admin.Settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.repository.AuthRepository
import bag.ezy.ezybagapp.model.repository.CustomerProfile
import kotlinx.coroutines.launch

data class SettingsUiState(
    val profile: CustomerProfile? = null,
    val loading: Boolean = true,
    val expandedSection: String? = null,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val changingPassword: Boolean = false,
    val passwordMessage: String? = null,
    val passwordChangeSucceeded: Boolean = false,
    val deletingAccount: Boolean = false,
    val deleteAccountPassword: String = "",
    val deleteAccountMessage: String? = null,
    val deleteAccountSucceeded: Boolean = false
)

class SettingsViewModel(
    private val repository: AuthRepository = AppContainer.authRepository
) : ViewModel() {

    var state by mutableStateOf(SettingsUiState())
        private set

    fun load() {
        viewModelScope.launch {
            state = state.copy(loading = true)
            val customerProfile = repository.getCurrentCustomerProfile()
            state = state.copy(
                profile = customerProfile,
                loading = false
            )
        }
    }

    fun toggleSection(section: String) {
        state = state.copy(
            expandedSection = if (state.expandedSection == section) null else section,
            passwordMessage = null,
            passwordChangeSucceeded = false,
            deleteAccountMessage = null,
            deleteAccountSucceeded = false
        )
    }

    fun onCurrentPasswordChange(value: String) {
        state = state.copy(
            currentPassword = value,
            passwordMessage = null,
            passwordChangeSucceeded = false
        )
    }

    fun onNewPasswordChange(value: String) {
        state = state.copy(
            newPassword = value,
            passwordMessage = null,
            passwordChangeSucceeded = false
        )
    }

    fun onConfirmPasswordChange(value: String) {
        state = state.copy(
            confirmPassword = value,
            passwordMessage = null,
            passwordChangeSucceeded = false
        )
    }

    fun onDeletePasswordChange(value: String) {
        state = state.copy(
            deleteAccountPassword = value,
            deleteAccountMessage = null,
            deleteAccountSucceeded = false
        )
    }

    fun changePassword() {
        when {
            state.currentPassword.isBlank() -> {
                state = state.copy(passwordMessage = "Enter your current password.")
            }
            state.newPassword.length < 8 -> {
                state = state.copy(passwordMessage = "Your new password must contain at least 8 characters.")
            }
            state.newPassword != state.confirmPassword -> {
                state = state.copy(passwordMessage = "The new passwords do not match.")
            }
            state.currentPassword == state.newPassword -> {
                state = state.copy(passwordMessage = "Your new password must be different from your current password.")
            }
            else -> {
                viewModelScope.launch {
                    state = state.copy(changingPassword = true, passwordMessage = null)
                    repository.changePassword(
                        currentPassword = state.currentPassword,
                        newPassword = state.newPassword
                    ).onSuccess {
                        state = state.copy(
                            changingPassword = false,
                            passwordChangeSucceeded = true,
                            passwordMessage = "Password changed successfully.",
                            currentPassword = "",
                            newPassword = "",
                            confirmPassword = ""
                        )
                    }.onFailure { error ->
                        state = state.copy(
                            changingPassword = false,
                            passwordChangeSucceeded = false,
                            passwordMessage = error.message ?: "Unable to change password. Check your current password and try again."
                        )
                    }
                }
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        if (state.deleteAccountPassword.isBlank()) {
            state = state.copy(deleteAccountMessage = "Enter your current password to continue.")
            return
        }

        viewModelScope.launch {
            state = state.copy(deletingAccount = true, deleteAccountMessage = null)
            repository.deleteCurrentAccount(state.deleteAccountPassword).fold(
                onSuccess = {
                    state = state.copy(
                        deletingAccount = false,
                        deleteAccountSucceeded = true,
                        deleteAccountMessage = "Your Firebase sign-in account was deleted.",
                        deleteAccountPassword = ""
                    )
                    repository.signOut()
                    onSuccess()
                },
                onFailure = { error ->
                    state = state.copy(
                        deletingAccount = false,
                        deleteAccountSucceeded = false,
                        deleteAccountMessage = error.message ?: "Account deletion failed. Please try again."
                    )
                }
            )
        }
    }
}
