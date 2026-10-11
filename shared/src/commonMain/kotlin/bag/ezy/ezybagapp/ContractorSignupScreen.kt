
package bag.ezy.ezybagapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
private val Green = Color(0xFF16764A)
private val LightGreen = Color(0xFFEAF6EF)
private val Background = Color(0xFFF5F7F5)
private val DarkText = Color(0xFF202923)
private val MutedText = Color(0xFF68736B)

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

    MaterialTheme(
        colorScheme = lightColorScheme(primary = Green, background = Background)
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Ezybag", color = Green, fontSize = 32.sp,
                    fontWeight = FontWeight.Bold)
                Text("Contractor Registration", fontSize = 23.sp,
                    fontWeight = FontWeight.Bold, color = DarkText)
                Text("Register your business to join the Ezybag network.",
                    color = MutedText, fontSize = 14.sp,
                    textAlign = TextAlign.Center)
            }

            Section("1", "Business details", "How can we contact you?") {
                Field("Company name", company, "Enter company name") { company = it }
                Field("Contact person's name", contact, "Enter full name") { contact = it }
                Field("Email address", email, "name@company.com") { email = it }
                Field("Phone number", phone, "Enter phone number") { phone = it }
            }

            Section("2", "Service areas", "Where does your business operate?") {
                Field("Towns or postcodes covered", areas, "e.g. Bristol, BS1, BS2",
                    singleLine = false) { areas = it }
            }

            Section("3", "Waste carrier licence", "Provide your licence details.") {
                Field("Licence / registration number", licence,
                    "Enter registration number") { licence = it }
                Document("Waste carrier licence document", licenceDoc) {
                    licenceDoc = it
                }
            }

            Section("4", "Insurance", "Provide your insurance information.") {
                Field("Insurance provider", insurer, "Enter provider name") {
                    insurer = it
                }
                Field("Policy number", policy, "Enter policy number") {
                    policy = it
                }
                Document("Proof of insurance", insuranceDoc) {
                    insuranceDoc = it
                }
            }

            Section("5", "Vehicle details", "Tell us which vehicle you use.") {
                Field("Vehicle type", vehicle, "e.g. Van, truck") { vehicle = it }
                Field("Vehicle registration number", registration,
                    "Enter registration number") { registration = it }
            }

            Section("6", "Identification", "Provide proof of identity.") {
                Document("Proof of identity", identityDoc) { identityDoc = it }
            }

            Section("7", "Declaration", "Confirm before submitting.") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(declaration, { declaration = it })
                    Text("I confirm that the information provided is accurate.",
                        modifier = Modifier.weight(1f), color = DarkText,
                        fontSize = 14.sp)
                }
            }

            if (message.isNotBlank()) {
                Surface(
                    color = if (success) LightGreen else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(message, Modifier.padding(14.dp), color = DarkText)
                }
            }

            Button(
                onClick = {
                    val missing = buildList {
                        if (company.isBlank()) add("company name")
                        if (contact.isBlank()) add("contact name")
                        if (!email.contains("@") || email.isBlank()) add("valid email")
                        if (phone.isBlank()) add("phone number")
                        if (areas.isBlank()) add("service areas")
                        if (licence.isBlank()) add("licence number")
                        if (!licenceDoc) add("licence document")
                        if (insurer.isBlank()) add("insurance provider")
                        if (policy.isBlank()) add("policy number")
                        if (!insuranceDoc) add("proof of insurance")
                        if (vehicle.isBlank()) add("vehicle type")
                        if (registration.isBlank()) add("vehicle registration")
                        if (!identityDoc) add("proof of identity")
                        if (!declaration) add("declaration")
                    }

                    success = missing.isEmpty()
                    message = if (success) {
                        "Demo submission complete! No information has been uploaded or saved."
                    } else {
                        "Please complete: ${missing.joinToString(", ")}."
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green)
            ) {
                Text("Submit application", fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold)
            }

            Text(
                "Applications may require review before a contractor account is activated.",
                Modifier.fillMaxWidth().padding(bottom = 20.dp),
                color = MutedText, fontSize = 12.sp, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun Section(
    number: String,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE3E9E4))
    ) {
        Column(Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(10.dp), color = LightGreen) {
                    Text(number, Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Green, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                        color = DarkText)
                    Text(subtitle, fontSize = 12.sp, color = MutedText)
                }
            }
            HorizontalDivider(color = Color(0xFFE8EDE9))
            content()
        }
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    hint: String,
    singleLine: Boolean = true,
    onChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            color = DarkText)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(hint, fontSize = 14.sp, color = MutedText) },
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 2,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun Document(
    title: String,
    selected: Boolean,
    onChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onChange(!selected) },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) LightGreen else Color(0xFFFAFBFA),
        border = BorderStroke(1.dp, if (selected) Green else Color(0xFFD5DED7))
    ) {
        Row(Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(if (selected) "✓" else "+", color = Green, fontSize = 23.sp)
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = DarkText)
                Text(
                    if (selected) "Marked as selected (demo)"
                    else "Tap to mark the document as selected",
                    fontSize = 12.sp, color = MutedText
                )
            }
        }
    }
}
@Preview
@Composable
fun ContractorSignupScreenPreview() {
    ContractorSignupScreen()
}