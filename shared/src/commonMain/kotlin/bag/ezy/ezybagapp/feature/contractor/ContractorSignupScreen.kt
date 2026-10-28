package bag.ezy.ezybagapp.feature.contractor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ContractorSignupScreen(
    onSubmitted: () -> Unit = {},
    vm: ContractorSignupViewModel = viewModel { ContractorSignupViewModel() }
) {
    val state = vm.state

    LaunchedEffect(vm.submitted) {
        if (vm.submitted) onSubmitted()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EZYBAG",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        ),
                        RoundedCornerShape(28.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(28.dp)
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Contractor registration",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Register your business to join the Ezybag network!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )

                ContractorSection(
                    "1", "Business details",
                    "How can we contact you?"
                ) {
                    ContractorField(
                        "Company name", state.company, "Enter company name"
                    ) { vm.onCompanyChange(it) }

                    ContractorField(
                        "First name", state.firstName, "Enter first name"
                    ) { vm.onFirstNameChange(it) }

                    ContractorField(
                        "Last name", state.lastName, "Enter last name"
                    ) { vm.onLastNameChange(it) }

                    ContractorField(
                        "Email address", state.email, "name@company.com"
                    ) { vm.onEmailChange(it) }

                    ContractorField(
                        "Phone number", state.phone, "Enter phone number"
                    ) { vm.onPhoneChange(it) }

                    ContractorField(
                        "Password",
                        state.password,
                        "Minimum 8 characters",
                        isPassword = true
                    ) { vm.onPasswordChange(it) }

                    ContractorField(
                        "Confirm password",
                        state.confirmPassword,
                        "Enter password again",
                        isPassword = true
                    ) { vm.onConfirmPasswordChange(it) }
                }

                ContractorSection(
                    "2", "Service areas",
                    "Where does your business operate?"
                ) {
                    ContractorField(
                        "Towns or postcodes covered",
                        state.areas,
                        "e.g. Newquay, BS1, BS2",
                        multiline = true
                    ) { vm.onAreasChange(it) }
                }

                ContractorSection(
                    "3", "Waste carrier licence",
                    "Provide your licence details."
                ) {
                    ContractorField(
                        "Licence / registration number",
                        state.licence,
                        "Enter registration number"
                    ) { vm.onLicenceChange(it) }

                    ContractorDocument(
                        "Waste carrier licence document",
                        state.licenceDoc
                    ) { vm.onLicenceDocChange(it) }
                }

                ContractorSection(
                    "4", "Insurance",
                    "Provide your insurance information."
                ) {
                    ContractorField(
                        "Insurance provider", state.insurer, "Enter provider name"
                    ) { vm.onInsurerChange(it) }

                    ContractorField(
                        "Policy number", state.policy, "Enter policy number"
                    ) { vm.onPolicyChange(it) }

                    ContractorDocument(
                        "Proof of insurance",
                        state.insuranceDoc
                    ) { vm.onInsuranceDocChange(it) }
                }

                ContractorSection(
                    "5", "Vehicle details",
                    "Tell us which vehicle you use."
                ) {
                    ContractorField(
                        "Vehicle type", state.vehicle, "e.g. Van, truck"
                    ) { vm.onVehicleChange(it) }

                    ContractorField(
                        "Vehicle registration number",
                        state.registration,
                        "Enter registration number"
                    ) { vm.onRegistrationChange(it) }
                }

                ContractorSection(
                    "6", "Identification",
                    "Provide proof of identity."
                ) {
                    ContractorDocument(
                        "Proof of identity",
                        state.identityDoc
                    ) { vm.onIdentityDocChange(it) }
                }

                ContractorSection(
                    "7", "Declaration",
                    "Confirm before submitting."
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.declaration,
                            onCheckedChange = { vm.onDeclarationChange(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.outline,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )

                        Text(
                            text = "I confirm that the information provided is accurate.",
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    }
                }

                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { vm.submit() },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (state.isLoading) "Submitting..." else "Submit application",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ContractorSection(
    number: String,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = number.padStart(2, '0'),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )

        content()
    }
}

@Composable
private fun ContractorField(
    label: String,
    value: String,
    hint: String,
    multiline: Boolean = false,
    isPassword: Boolean = false,
    onChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )

        BasicTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            singleLine = !multiline,
            minLines = if (multiline) 2 else 1,
            maxLines = if (multiline) 4 else 1,
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }

                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun ContractorDocument(
    title: String,
    selected: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                RoundedCornerShape(12.dp)
            )
            .clickable { onChange(!selected) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (selected) "✓" else "+",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 22.sp
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = if (selected) "Marked as selected"
                else "Tap to mark the document as selected",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}