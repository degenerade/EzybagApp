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
    val firstName: String = "",
    val lastName: String = "",
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

    fun onFirstNameChange(value: String) {
        state = state.copy(firstName = value, error = null)
    }

    fun onLastNameChange(value: String) {
        state = state.copy(lastName = value, error = null)
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

    fun submit() {
        val missing = buildList {
            if (state.company.isBlank()) add("company name")
            if (state.firstName.isBlank()) add("first name")
            if (state.lastName.isBlank()) add("last name")

            if (state.email.isBlank() || !state.email.contains("@")) {
                add("valid email")
            }

            if (state.phone.isBlank()) add("phone number")
            if (state.areas.isBlank()) add("service areas")
            if (state.licence.isBlank()) add("licence number")
            if (!state.licenceDoc) add("licence document")
            if (state.insurer.isBlank()) add("insurance provider")
            if (state.policy.isBlank()) add("policy number")
            if (!state.insuranceDoc) add("proof of insurance")
            if (state.vehicle.isBlank()) add("vehicle type")

            if (state.registration.isBlank()) {
                add("vehicle registration")
            }

            if (!state.identityDoc) add("proof of identity")
            if (!state.declaration) add("declaration")
        }

        if (missing.isNotEmpty()) {
            state = state.copy(
                error = "Please complete: ${missing.joinToString(", ")}."
            )
            return
        }

        if (state.password.length < 8) {
            state = state.copy(
                error = "Password must contain at least 8 characters."
            )
            return
        }

        if (state.password != state.confirmPassword) {
            state = state.copy(
                error = "Passwords do not match."
            )
            return
        }

        val contractor = Contractor(
            firstName = state.firstName.trim(),
            lastName = state.lastName.trim(),
            email = state.email.trim(),
            phone = state.phone.trim(),
            companyName = state.company.trim(),
            serviceAreas = state.areas.trim(),
            policyNumber = state.policy.trim(),
            vehicleType = state.vehicle.trim(),
            vehicleRegistrationNumber = state.registration.trim(),
            wasteCarrierLicenceDetails = state.licence.trim(),
            insuranceProvider = state.insurer.trim(),
            isApproved = false
        )

        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            repository.registerContractor(
                contractor = contractor,
                password = state.password
            )
                .onSuccess {
                    state = state.copy(isLoading = false)
                    submitted = true
                }
                .onFailure {
                    state = state.copy(
                        isLoading = false,
                        error = it.message ?: "Unable to submit application."
                    )
                }
        }
    }
}