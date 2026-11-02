
package bag.ezy.ezybagapp.feature.contractor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bag.ezy.ezybagapp.model.data.Contractor
import bag.ezy.ezybagapp.model.repository.ContractorRepository
import bag.ezy.ezybagapp.model.repository.FirebaseContractorRepository
import kotlinx.coroutines.launch

data class ContractorSignupUiState(
    val company: String = "",
    val contactName: String = "",
    val email: String = "",
    val phone: String = "",
    val areas: String = "",
    val licence: String = "",
    val insurer: String = "",
    val policy: String = "",
    val vehicle: String = "",
    val registration: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val licenceDoc: Boolean = false,
    val insuranceDoc: Boolean = false,
    val identityDoc: Boolean = false,
    val declaration: Boolean = false,
    val termsAccepted: Boolean = false,
    val privacyAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ContractorSignupViewModel(
    private val repository: ContractorRepository = FirebaseContractorRepository()
) : ViewModel() {

    var state by mutableStateOf(ContractorSignupUiState())
        private set

    var submitted by mutableStateOf(false)
        private set

    fun onCompanyChange(value: String) {
        state = state.copy(company = value, error = null)
    }

    fun onContactNameChange(value: String) {
        state = state.copy(contactName = value, error = null)
    }

    fun onEmailChange(value: String) {
        state = state.copy(email = value, error = null)
    }

    fun onPhoneChange(value: String) {
        state = state.copy(phone = value, error = null)
    }

    fun onAreasChange(value: String) {
        state = state.copy(areas = value, error = null)
    }

    fun onLicenceChange(value: String) {
        state = state.copy(licence = value, error = null)
    }

    fun onInsurerChange(value: String) {
        state = state.copy(insurer = value, error = null)
    }

    fun onPolicyChange(value: String) {
        state = state.copy(policy = value, error = null)
    }

    fun onVehicleChange(value: String) {
        state = state.copy(vehicle = value, error = null)
    }

    fun onRegistrationChange(value: String) {
        state = state.copy(registration = value, error = null)
    }

    fun onPasswordChange(value: String) {
        state = state.copy(password = value, error = null)
    }

    fun onConfirmPasswordChange(value: String) {
        state = state.copy(confirmPassword = value, error = null)
    }

    fun onLicenceDocChange(value: Boolean) {
        state = state.copy(licenceDoc = value, error = null)
    }

    fun onInsuranceDocChange(value: Boolean) {
        state = state.copy(insuranceDoc = value, error = null)
    }

    fun onIdentityDocChange(value: Boolean) {
        state = state.copy(identityDoc = value, error = null)
    }

    fun onDeclarationChange(value: Boolean) {
        state = state.copy(declaration = value, error = null)
    }

    fun onTermsAcceptedChange(value: Boolean) {
        state = state.copy(termsAccepted = value, error = null)
    }

    fun onPrivacyAcceptedChange(value: Boolean) {
        state = state.copy(privacyAccepted = value, error = null)
    }

    fun submit() {
        if (state.isLoading) return

        val currentState = state
        val email = currentState.email.trim()
        val phoneDigits = currentState.phone.filter { it.isDigit() }

        val errors = buildList {
            if (currentState.company.isBlank()) {
                add("Company name is required.")
            }

            if (currentState.contactName.isBlank()) {
                add("Contact name is required.")
            }

            val emailRegex =
                Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

            if (!emailRegex.matches(email)) {
                add("Enter a valid email address.")
            }

            if (phoneDigits.length !in 7..15) {
                add("Enter a valid phone number with 7 to 15 digits.")
            }

            if (currentState.areas.isBlank()) {
                add("Service areas are required.")
            }

            if (currentState.licence.isBlank()) {
                add("Waste carrier licence number is required.")
            }

            if (!currentState.licenceDoc) {
                add("Please mark the waste carrier licence document as selected.")
            }

            if (currentState.insurer.isBlank()) {
                add("Insurance provider is required.")
            }

            if (currentState.policy.isBlank()) {
                add("Insurance policy number is required.")
            }

            if (!currentState.insuranceDoc) {
                add("Please mark proof of insurance as selected.")
            }

            if (currentState.vehicle.isBlank()) {
                add("Vehicle type is required.")
            }

            if (currentState.registration.isBlank()) {
                add("Vehicle registration number is required.")
            }

            if (!currentState.identityDoc) {
                add("Please mark proof of identity as selected.")
            }

            if (currentState.password.length < 8) {
                add("Password must contain at least 8 characters.")
            }

            if (currentState.password != currentState.confirmPassword) {
                add("Passwords do not match.")
            }

            if (!currentState.declaration) {
                add("You must confirm that your information is accurate.")
            }

            if (!currentState.termsAccepted) {
                add("You must accept the Contractor Terms and Conditions.")
            }

            if (!currentState.privacyAccepted) {
                add("You must acknowledge the Privacy Notice.")
            }
        }

        if (errors.isNotEmpty()) {
            state = state.copy(error = errors.joinToString("\n"))
            return
        }

        val contractor = Contractor(
            contactName = currentState.contactName.trim(),
            email = email,
            phone = currentState.phone.trim(),
            companyName = currentState.company.trim(),
            serviceAreas = currentState.areas.trim(),
            policyNumber = currentState.policy.trim(),
            vehicleType = currentState.vehicle.trim(),
            vehicleRegistrationNumber = currentState.registration.trim(),
            wasteCarrierLicenceDetails = currentState.licence.trim(),
            insuranceProvider = currentState.insurer.trim(),
            isApproved = false
        )

        val password = currentState.password

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            repository.registerContractor(
                contractor = contractor,
                password = password
            ).onSuccess {
                state = state.copy(isLoading = false)
                submitted = true
            }.onFailure { exception ->
                state = state.copy(
                    isLoading = false,
                    error = exception.message
                        ?: "Unable to submit application. Please try again."
                )
            }
        }
    }
}
