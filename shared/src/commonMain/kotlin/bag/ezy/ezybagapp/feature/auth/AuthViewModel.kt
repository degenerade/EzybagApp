
package bag.ezy.ezybagapp.feature.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.repository.AuthRepository
import kotlinx.coroutines.launch

data class AuthUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val info: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository = AppContainer.authRepository
) : ViewModel() {

    var state by mutableStateOf(AuthUiState())
        private set

    fun onFirstNameChange(v: String) {
        state = state.copy(firstName = v, error = null)
    }

    fun onLastNameChange(v: String) {
        state = state.copy(lastName = v, error = null)
    }

    fun onEmailChange(v: String) {
        state = state.copy(email = v, error = null)
    }

    fun onPasswordChange(v: String) {
        state = state.copy(password = v, error = null)
    }

    fun onConfirmChange(v: String) {
        state = state.copy(confirmPassword = v, error = null)
    }

    fun clearMessage() {
        state = state.copy(error = null, info = null)
    }

    // EMAIL AND PASSWORD SIGN-IN
    fun signIn(onSuccess: () -> Unit) {
        validate(isSignUp = false)?.let {
            state = state.copy(error = it)
            return
        }

        run(onSuccess) {
            repo.signIn(
                state.email.trim(),
                state.password
            )
        }
    }

    // GOOGLE SIGN-IN
    fun signInWithGoogle(onSuccess: () -> Unit) {
        run(onSuccess) {
            repo.signInWithGoogle()
        }
    }

    // EMAIL AND PASSWORD REGISTRATION
    fun signUp(onSuccess: () -> Unit) {
        validate(isSignUp = true)?.let {
            state = state.copy(error = it)
            return
        }

        run(onSuccess) {
            repo.signUp(
                state.firstName.trim(),
                state.lastName.trim(),
                state.email.trim(),
                state.password
            )
        }
    }

    // SIGN-OUT
    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                repo.signOut()
                state = AuthUiState()
                onSignedOut()
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Unable to sign out."
                )
            }
        }
    }

    // PASSWORD RESET
    fun forgotPassword() {
        if (!state.email.isValidEmail()) {
            state = state.copy(
                error = "Enter your email first, then click Forgot password."
            )
            return
        }

        run(
            onSuccess = {
                state = state.copy(
                    info = "If that account exists, a reset link has been sent."
                )
            }
        ) {
            repo.sendPasswordReset(state.email.trim())
        }
    }

    // SHARED ASYNC RESULT HANDLING
    private fun run(
        onSuccess: () -> Unit,
        block: suspend () -> Result<Unit>
    ) {
        if (state.isLoading) return

        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null,
                info = null
            )

            try {
                block()
                    .onSuccess {
                        state = state.copy(
                            isLoading = false,
                            password = "",
                            confirmPassword = ""
                        )
                        onSuccess()
                    }
                    .onFailure { error ->
                        state = state.copy(
                            isLoading = false,
                            error = error.message
                                ?: "Something went wrong."
                        )
                    }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Something went wrong."
                )
            }
        }
    }

    // FORM VALIDATION
    private fun validate(isSignUp: Boolean): String? = when {
        isSignUp && state.firstName.isBlank() ->
            "Enter your first name."

        isSignUp && state.lastName.isBlank() ->
            "Enter your last name."

        !state.email.isValidEmail() ->
            "Enter a valid email address."

        state.password.length < 8 ->
            "Password must be at least 8 characters."

        isSignUp && state.confirmPassword.isBlank() ->
            "Confirm your password."

        isSignUp && state.password != state.confirmPassword ->
            "Passwords do not match."

        else -> null
    }

    private fun String.isValidEmail(): Boolean {
        val value = trim()

        return value.count { it == '@' } == 1 &&
                value.substringAfter('@').contains('.') &&
                !value.startsWith('@') &&
                !value.endsWith('@')
    }
}
