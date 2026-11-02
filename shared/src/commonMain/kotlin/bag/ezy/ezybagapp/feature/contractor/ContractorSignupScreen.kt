
package bag.ezy.ezybagapp.feature.contractor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    vm: ContractorSignupViewModel = viewModel {
        ContractorSignupViewModel()
    },
    onBack: () -> Unit,
) {
    val state = vm.state

    var showTerms by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }

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
            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Back")
            }
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

                // 1. Business details
                ContractorSection(
                    "1",
                    "Business details",
                    "How can we contact you?"
                ) {
                    ContractorField(
                        "Company name",
                        state.company,
                        "Enter company name"
                    ) {
                        vm.onCompanyChange(it)
                    }

                    ContractorField(
                        "Contact name",
                        state.contactName,
                        "Enter your full name"
                    ) {
                        vm.onContactNameChange(it)
                    }

                    ContractorField(
                        "Email address",
                        state.email,
                        "name@company.com"
                    ) {
                        vm.onEmailChange(it)
                    }

                    ContractorField(
                        "Phone number",
                        state.phone,
                        "Enter phone number"
                    ) {
                        vm.onPhoneChange(it)
                    }

                    ContractorField(
                        "Password",
                        state.password,
                        "Minimum 8 characters",
                        isPassword = true
                    ) {
                        vm.onPasswordChange(it)
                    }

                    ContractorField(
                        "Confirm password",
                        state.confirmPassword,
                        "Enter password again",
                        isPassword = true
                    ) {
                        vm.onConfirmPasswordChange(it)
                    }
                }

                // 2. Service areas
                ContractorSection(
                    "2",
                    "Service areas",
                    "Where does your business operate?"
                ) {
                    ContractorField(
                        "Towns or postcodes covered",
                        state.areas,
                        "e.g. Newquay, BS1, BS2",
                        multiline = true
                    ) {
                        vm.onAreasChange(it)
                    }
                }

                // 3. Waste carrier licence
                ContractorSection(
                    "3",
                    "Waste carrier licence",
                    "Provide your licence details."
                ) {
                    ContractorField(
                        "Licence / registration number",
                        state.licence,
                        "Enter registration number"
                    ) {
                        vm.onLicenceChange(it)
                    }

                    ContractorDocument(
                        "Waste carrier licence document",
                        state.licenceDoc
                    ) {
                        vm.onLicenceDocChange(it)
                    }
                }

                // 4. Insurance
                ContractorSection(
                    "4",
                    "Insurance",
                    "Provide your insurance information."
                ) {
                    ContractorField(
                        "Insurance provider",
                        state.insurer,
                        "Enter provider name"
                    ) {
                        vm.onInsurerChange(it)
                    }

                    ContractorField(
                        "Policy number",
                        state.policy,
                        "Enter policy number"
                    ) {
                        vm.onPolicyChange(it)
                    }

                    ContractorDocument(
                        "Proof of insurance",
                        state.insuranceDoc
                    ) {
                        vm.onInsuranceDocChange(it)
                    }
                }

                // 5. Vehicle details
                ContractorSection(
                    "5",
                    "Vehicle details",
                    "Tell us which vehicle you use."
                ) {
                    ContractorField(
                        "Vehicle type",
                        state.vehicle,
                        "e.g. Van, truck"
                    ) {
                        vm.onVehicleChange(it)
                    }

                    ContractorField(
                        "Vehicle registration number",
                        state.registration,
                        "Enter registration number"
                    ) {
                        vm.onRegistrationChange(it)
                    }
                }

                // 6. Identification
                ContractorSection(
                    "6",
                    "Identification",
                    "Provide proof of identity."
                ) {
                    ContractorDocument(
                        "Proof of identity",
                        state.identityDoc
                    ) {
                        vm.onIdentityDocChange(it)
                    }
                }

                // 7. Declaration and acceptance
                ContractorSection(
                    "7",
                    "Declaration",
                    "Confirm before submitting."
                ) {
                    // Accuracy declaration
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.declaration,
                            onCheckedChange = {
                                vm.onDeclarationChange(it)
                            },
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

                    // Contractor Terms and Conditions
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.termsAccepted,
                            onCheckedChange = {
                                vm.onTermsAcceptedChange(it)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.outline,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "I agree to the Contractor Terms and Conditions.",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )

                            Text(
                                text = "Read Contractor Terms and Conditions",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    showTerms = true
                                }
                            )
                        }
                    }

                    // Privacy Notice
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.privacyAccepted,
                            onCheckedChange = {
                                vm.onPrivacyAcceptedChange(it)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.outline,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "I have read and acknowledge the Privacy Notice.",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )

                            Text(
                                text = "Read Privacy Notice",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    showPrivacy = true
                                }
                            )
                        }
                    }
                }

                // Error message
                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                // Submit application
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
                        text = if (state.isLoading) {
                            "Submitting..."
                        } else {
                            "Submit application"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Contractor Terms and Conditions dialog
    if (showTerms) {
        AlertDialog(
            onDismissRequest = { showTerms = false },
            title = {
                Text("Contractor Terms and Conditions")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "1. Registration and information",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "You must provide accurate, complete and up-to-date " +
                                "information when registering as an Ezybag contractor. " +
                                "You are responsible for keeping your account details current."
                    )

                    Text(
                        "2. Licences and insurance",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "You are responsible for holding all licences, permits and " +
                                "insurance required to provide your services legally. " +
                                "You must ensure that these remain valid."
                    )

                    Text(
                        "3. Service responsibilities",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "You agree to provide services professionally and safely, " +
                                "and to comply with applicable waste-management, " +
                                "environmental and safety requirements."
                    )

                    Text(
                        "4. Customer conduct",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "You must treat customers respectfully, take reasonable care " +
                                "of their property and follow the agreed booking instructions."
                    )

                    Text(
                        "5. Bookings and payments",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "You agree to follow Ezybag's booking, communication and " +
                                "payment procedures when using the platform."
                    )

                    Text(
                        "6. Verification and approval",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Submitting an application does not guarantee approval. " +
                                "Ezybag may review the information and documents you provide " +
                                "before approving your contractor account."
                    )

                    Text(
                        "7. Suspension or termination",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Providing false information, failing to maintain required " +
                                "licences or insurance, or breaching these terms may result " +
                                "in restrictions, suspension or termination of your account."
                    )

                    Text(
                        "8. Changes to these terms",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Ezybag may update these terms when necessary. Contractors " +
                                "will be informed of relevant changes."
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showTerms = false }
                ) {
                    Text("Close")
                }
            }
        )
    }

    // Privacy Notice dialog
    if (showPrivacy) {
        AlertDialog(
            onDismissRequest = { showPrivacy = false },
            title = {
                Text("Contractor Privacy Notice")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "1. Information we collect",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "We may collect your contact name, company name, email " +
                                "address, phone number, service areas, licence details, " +
                                "insurance information, vehicle information and account details."
                    )

                    Text(
                        "2. How we use your information",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Your information is used to manage your contractor account, " +
                                "review and verify your application, coordinate bookings " +
                                "and communicate with you about the Ezybag service."
                    )

                    Text(
                        "3. Sharing your information",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Relevant information may be shared with customers, service " +
                                "providers or authorities where necessary to operate the " +
                                "service or meet legal obligations."
                    )

                    Text(
                        "4. Data security",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Ezybag intends to use appropriate measures to protect your " +
                                "personal information against unauthorised access, loss or misuse."
                    )

                    Text(
                        "5. Data retention",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Your information may be retained for as long as necessary " +
                                "for the purposes described in this notice or to meet applicable " +
                                "legal obligations."
                    )

                    Text(
                        "6. Your privacy rights",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Depending on the data protection laws that apply, you may have " +
                                "the right to request access to, correction of, deletion of or " +
                                "restriction of the processing of your personal information."
                    )

                    Text(
                        "7. Contact",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "If you have questions about this Privacy Notice or how your " +
                                "information is handled, contact Ezybag through its official " +
                                "support channel."
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showPrivacy = false }
                ) {
                    Text("Close")
                }
            }
        )
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
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
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
                text = if (selected) {
                    "Marked as selected"
                } else {
                    "Tap to mark the document as selected"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}
