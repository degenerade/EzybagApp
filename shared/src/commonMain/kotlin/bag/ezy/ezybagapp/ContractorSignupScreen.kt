
package bag.ezy.ezybagapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ContractorBackground = Color(0xFF0D1718)
private val ContractorCard = Color(0xFF192A2D)
private val ContractorField = Color(0xFF101A1C)
private val ContractorGreen = Color(0xFF39B596)
private val ContractorText = Color(0xFFE8F0EF)
private val ContractorMuted = Color(0xFF94A8AC)
private val ContractorBorder = Color(0xFF2B4145)
private val ContractorError = Color(0xFFE58A79)

@Composable
fun ContractorSignupScreen() {
    var company by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var areas by remember { mutableStateOf("") }
    var licence by remember { mutableStateOf("") }
    var insurer by remember { mutableStateOf("") }
    var policy by remember { mutableStateOf("") }
    var vehicle by remember { mutableStateOf("") }
    var registration by remember { mutableStateOf("") }

    var licenceDoc by remember { mutableStateOf(false) }
    var insuranceDoc by remember { mutableStateOf(false) }
    var identityDoc by remember { mutableStateOf(false) }
    var declaration by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ContractorBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EZYBAG",
                color = ContractorGreen,
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
                        BorderStroke(1.dp, ContractorBorder),
                        RoundedCornerShape(28.dp)
                    )
                    .background(
                        ContractorCard,
                        RoundedCornerShape(28.dp)
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Contractor registration",
                    color = ContractorText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Register your business to join the Ezybag network!",
                    color = ContractorMuted,
                    fontSize = 14.sp
                )

                ContractorSection(
                    "1", "Business details",
                    "How can we contact you?"
                ) {
                    ContractorField("Company name", company, "Enter company name") {
                        company = it
                    }
                    ContractorField(
                        "Contact person's name", contact, "Enter full name"
                    ) { contact = it }
                    ContractorField(
                        "Email address", email, "name@company.com"
                    ) { email = it }
                    ContractorField(
                        "Phone number", phone, "Enter phone number"
                    ) { phone = it }
                }

                ContractorSection(
                    "2", "Service areas",
                    "Where does your business operate?"
                ) {
                    ContractorField(
                        "Towns or postcodes covered",
                        areas,
                        "e.g. Newquay, BS1, BS2",
                        multiline = true
                    ) { areas = it }
                }

                ContractorSection(
                    "3", "Waste carrier licence",
                    "Provide your licence details."
                ) {
                    ContractorField(
                        "Licence / registration number",
                        licence,
                        "Enter registration number"
                    ) { licence = it }

                    ContractorDocument(
                        "Waste carrier licence document", licenceDoc
                    ) { licenceDoc = it }
                }

                ContractorSection(
                    "4", "Insurance",
                    "Provide your insurance information."
                ) {
                    ContractorField(
                        "Insurance provider", insurer, "Enter provider name"
                    ) { insurer = it }
                    ContractorField(
                        "Policy number", policy, "Enter policy number"
                    ) { policy = it }

                    ContractorDocument(
                        "Proof of insurance", insuranceDoc
                    ) { insuranceDoc = it }
                }

                ContractorSection(
                    "5", "Vehicle details",
                    "Tell us which vehicle you use."
                ) {
                    ContractorField(
                        "Vehicle type", vehicle, "e.g. Van, truck"
                    ) { vehicle = it }
                    ContractorField(
                        "Vehicle registration number",
                        registration,
                        "Enter registration number"
                    ) { registration = it }
                }

                ContractorSection(
                    "6", "Identification",
                    "Provide proof of identity."
                ) {
                    ContractorDocument(
                        "Proof of identity", identityDoc
                    ) { identityDoc = it }
                }

                ContractorSection(
                    "7", "Declaration",
                    "Confirm before submitting."
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = declaration,
                            onCheckedChange = { declaration = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = ContractorGreen,
                                uncheckedColor = ContractorMuted,
                                checkmarkColor = ContractorBackground
                            )
                        )

                        Text(
                            text = "I confirm that the information provided is accurate.",
                            modifier = Modifier.weight(1f),
                            color = ContractorText,
                            fontSize = 14.sp
                        )
                    }
                }

                if (message.isNotBlank()) {
                    Text(
                        text = message,
                        color = if (success) ContractorGreen
                        else ContractorError,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        val missing = buildList {
                            if (company.isBlank()) add("company name")
                            if (contact.isBlank()) add("contact name")
                            if (email.isBlank() || !email.contains("@")) {
                                add("valid email")
                            }
                            if (phone.isBlank()) add("phone number")
                            if (areas.isBlank()) add("service areas")
                            if (licence.isBlank()) add("licence number")
                            if (!licenceDoc) add("licence document")
                            if (insurer.isBlank()) add("insurance provider")
                            if (policy.isBlank()) add("policy number")
                            if (!insuranceDoc) add("proof of insurance")
                            if (vehicle.isBlank()) add("vehicle type")
                            if (registration.isBlank()) {
                                add("vehicle registration")
                            }
                            if (!identityDoc) add("proof of identity")
                            if (!declaration) add("declaration")
                        }

                        success = missing.isEmpty()
                        message = if (success) {
                            "Submission complete!"
                        } else {
                            "Please complete: ${missing.joinToString(", ")}."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ContractorGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Submit application",
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
                ContractorField,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                ContractorBorder,
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
                color = ContractorGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        ContractorCard,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = ContractorText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = ContractorMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ContractorBorder)
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
    onChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = ContractorMuted,
            fontSize = 13.sp
        )

        BasicTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ContractorBackground,
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    ContractorBorder,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            singleLine = !multiline,
            minLines = if (multiline) 2 else 1,
            maxLines = if (multiline) 4 else 1,
            textStyle = TextStyle(
                color = ContractorText,
                fontSize = 14.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            color = ContractorMuted,
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
                if (selected) ContractorCard else ContractorBackground,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (selected) ContractorGreen else ContractorBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onChange(!selected) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (selected) "✓" else "+",
            color = ContractorGreen,
            fontSize = 22.sp
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = ContractorText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (selected) "Marked as selected"
                else "Tap to mark the document as selected",
                color = ContractorMuted,
                fontSize = 12.sp
            )
        }
    }
}
