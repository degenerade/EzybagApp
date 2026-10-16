package bag.ezy.ezybagapp.feature.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.repository.AuthRepository
import bag.ezy.ezybagapp.model.repository.FakeAuthRepository
import kotlinx.coroutines.launch

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val info: String? = null,
)

class AuthViewModel(
    private val repo: AppContainer.authRepository
) : ViewModel() {
    var state by mutableStateOf(AuthUiState())
        private set

    fun onNameChange(v: String) { state = state.copy(name = v, error = null) }
    fun onEmailChange(v: String) { state = state.copy(email = v, error = null) }
    fun onPasswordChange(v: String) { state = state.copy(password = v, error = null) }
    fun onConfirmChange(v: String) { state = state.copy(confirmPassword = v, error = null) }
    fun clearMessage() { state = state.copy(error = null, info = null) }

    fun signIn(onSuccess: () -> Unit) {
        validate(isSignUp = false)?.let { state = state.copy(error = it); return }
        run(onSuccess) { repo.signIn(state.email.trim(), state.password) }
    }

    fun signUp(onSuccess: () -> Unit) {
        validate(isSignUp = true)?.let { state = state.copy(error = it); return }
        run(onSuccess) { repo.signUp(state.name.trim(), state.email.trim(), state.password) }
    }

    fun forgotPassword() {
        if (!state.email.isValidEmail()) {
            state = state.copy(error = "Enter your email first, then click Forgot password")
            return
        }
        run({ state = state.copy(info = "If that account exists, a reset link has been sent") }) {
            repo.sendPasswordReset(state.email.trim())
        }

    }

    private fun run(onSuccess: () -> Unit, block: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null, info = null)
            block()
                .onSuccess { state = state.copy(isLoading = false, password = "", confirmPassword = ""); onSuccess() }
                .onFailure { state = state.copy(isLoading = false, error = it.message ?: "Something went wrong") }
        }
    }

    private fun validate(isSignUp: Boolean): String? = when {
        isSignUp && state.name.isBlank() -> "Enter your name"
        !state.email.isValidEmail() -> "Enter a valid email"
        state.password.length < 6 -> "Password must be at least 6 characters"
        isSignUp && state.password != state.confirmPassword -> "Passwords don't match"
        else -> null
    }

    private fun String.isValidEmail(): Boolean {
        val t = trim()
        return t.count { it == '@' } == 1 && t.substringAfter('@').contains('.') && !t.startsWith('@')
    }
}