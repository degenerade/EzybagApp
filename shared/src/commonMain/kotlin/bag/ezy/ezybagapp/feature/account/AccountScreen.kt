
package bag.ezy.ezybagapp.feature.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bag.ezy.ezybagapp.model.repository.CustomerProfile
import bag.ezy.ezybagapp.model.repository.FirebaseAuthRepository
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { FirebaseAuthRepository() }
    val scope = rememberCoroutineScope()

    var profile by remember {
        mutableStateOf<CustomerProfile?>(null)
    }
    var loading by remember { mutableStateOf(true) }

    var expandedSection by remember {
        mutableStateOf<String?>(null)
    }

    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var savingPhone by remember { mutableStateOf(false) }
    var savingAddress by remember { mutableStateOf(false) }

    var phoneMessage by remember { mutableStateOf<String?>(null) }
    var addressMessage by remember { mutableStateOf<String?>(null) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var changingPassword by remember { mutableStateOf(false) }
    var passwordMessage by remember { mutableStateOf<String?>(null) }
    var passwordChangeSucceeded by remember { mutableStateOf(false) }

    // Account deletion state
    var deletingAccount by remember { mutableStateOf(false) }
    var deleteAccountPassword by remember { mutableStateOf("") }
    var deleteAccountMessage by remember { mutableStateOf<String?>(null) }
    var deleteAccountSucceeded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profile = repository.getCurrentCustomerProfile()
        phone = profile?.phone.orEmpty()
        address = profile?.address.orEmpty()
        loading = false
    }

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when {
                loading -> Text("Loading account...")

                profile != null -> {
                    val customer = profile!!
                    ProfileHeader(
                        name = customer.name.ifBlank { "Customer" },
                        email = customer.email.ifBlank {
                            "No email available"
                        },
                        phone = customer.phone.ifBlank {
                            "Phone not added"
                        },
                        initial = customer.name
                            .firstOrNull()
                            ?.uppercase()
                            ?: "C"
                    )
                }

                else -> Text(
                    text = "No signed-in customer found.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Manage Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                ExpandableRow(
                    title = "Personal details",
                    expanded = expandedSection == "personal",
                    onClick = {
                        expandedSection =
                            if (expandedSection == "personal") null
                            else "personal"
                        phoneMessage = null
                    }
                )

                if (expandedSection == "personal") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailLine(
                            label = "Name",
                            value = profile?.name.orEmpty()
                        )

                        DetailLine(
                            label = "Email",
                            value = profile?.email.orEmpty()
                        )

                        Text(
                            text = "Phone number",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                phoneMessage = null
                            },
                            label = { Text("Phone number") },
                            placeholder = { Text("+46...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !savingPhone
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    savingPhone = true
                                    phoneMessage = null

                                    try {
                                        repository
                                            .updateCurrentCustomerPhone(phone)
                                            .onSuccess {
                                                profile =
                                                    repository.getCurrentCustomerProfile()
                                                phone = profile?.phone.orEmpty()
                                                phoneMessage =
                                                    "Phone number saved successfully."
                                            }
                                            .onFailure { error ->
                                                phoneMessage =
                                                    error.message
                                                        ?: "Could not save phone number."
                                            }
                                    } finally {
                                        savingPhone = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !savingPhone && phone.isNotBlank()
                        ) {
                            Text(
                                if (savingPhone) "Saving..."
                                else "Save phone number"
                            )
                        }

                        phoneMessage?.let { message ->
                            Text(
                                text = message,
                                color = if (
                                    message == "Phone number saved successfully."
                                ) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            text = "Your phone number is saved to your customer profile. It has not been verified by SMS.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                ExpandableRow(
                    title = "Saved addresses",
                    expanded = expandedSection == "address",
                    onClick = {
                        expandedSection =
                            if (expandedSection == "address") null
                            else "address"
                        addressMessage = null
                    }
                )

                if (expandedSection == "address") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = address,
                            onValueChange = {
                                address = it
                                addressMessage = null
                            },
                            label = {
                                Text("Street, postcode and city")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            enabled = !savingAddress
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    savingAddress = true
                                    addressMessage = null

                                    try {
                                        repository
                                            .updateCurrentCustomerAddress(address)
                                            .onSuccess {
                                                profile =
                                                    repository.getCurrentCustomerProfile()
                                                address = profile?.address.orEmpty()
                                                addressMessage =
                                                    "Address saved successfully."
                                            }
                                            .onFailure { error ->
                                                addressMessage =
                                                    error.message
                                                        ?: "Could not save address."
                                            }
                                    } finally {
                                        savingAddress = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !savingAddress && address.isNotBlank()
                        ) {
                            Text(
                                if (savingAddress) "Saving..."
                                else "Save address"
                            )
                        }

                        addressMessage?.let { message ->
                            Text(
                                text = message,
                                color = if (
                                    message == "Address saved successfully."
                                ) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                ExpandableRow(
                    title = "Payment methods",
                    expanded = expandedSection == "payments",
                    onClick = {
                        expandedSection =
                            if (expandedSection == "payments") null
                            else "payments"
                    }
                )

                if (expandedSection == "payments") {
                    Text(
                        text = "Payment method management is not available yet. Do not store full card numbers or security codes in Firestore.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                ExpandableRow(
                    title = "Privacy and data",
                    expanded = expandedSection == "privacy",
                    onClick = {
                        expandedSection =
                            if (expandedSection == "privacy") null
                            else "privacy"
                    }
                )

                if (expandedSection == "privacy") {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Information stored",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "EzybagApp may store your name, email, phone number, saved address and booking information to provide and manage the service."
                        )

                        Text(
                            "How it is used",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Your information is used to manage your account, arrange bookings, communicate about services and provide support."
                        )

                        Text(
                            "Your choices",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "You can update your saved phone number and address. You may contact the service operator to request access, correction or deletion of personal data, subject to applicable legal requirements."
                        )

                        Text(
                            "Contact",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "The service operator's verified privacy contact and data-retention policy must be added before this notice is published as final."
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                ExpandableRow(
                    title = "Change password",
                    expanded = expandedSection == "password",
                    onClick = {
                        expandedSection =
                            if (expandedSection == "password") null
                            else "password"

                        currentPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                        passwordMessage = null
                        passwordChangeSucceeded = false
                    }
                )

                if (expandedSection == "password") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Enter your current password and choose a new password.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = {
                                currentPassword = it
                                passwordMessage = null
                                passwordChangeSucceeded = false
                            },
                            label = { Text("Current password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !changingPassword
                        )

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                passwordMessage = null
                                passwordChangeSucceeded = false
                            },
                            label = { Text("New password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !changingPassword
                        )

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                passwordMessage = null
                                passwordChangeSucceeded = false
                            },
                            label = { Text("Confirm new password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !changingPassword
                        )

                        Button(
                            onClick = {
                                passwordMessage = null
                                passwordChangeSucceeded = false

                                when {
                                    currentPassword.isBlank() -> {
                                        passwordMessage =
                                            "Enter your current password."
                                    }

                                    newPassword.length < 8 -> {
                                        passwordMessage =
                                            "Your new password must contain at least 8 characters."
                                    }

                                    newPassword != confirmPassword -> {
                                        passwordMessage =
                                            "The new passwords do not match."
                                    }

                                    currentPassword == newPassword -> {
                                        passwordMessage =
                                            "Your new password must be different from your current password."
                                    }

                                    else -> {
                                        scope.launch {
                                            changingPassword = true

                                            try {
                                                repository.changePassword(
                                                    currentPassword = currentPassword,
                                                    newPassword = newPassword
                                                ).onSuccess {
                                                    passwordChangeSucceeded = true
                                                    passwordMessage =
                                                        "Password changed successfully."

                                                    currentPassword = ""
                                                    newPassword = ""
                                                    confirmPassword = ""
                                                }.onFailure { error ->
                                                    passwordMessage =
                                                        error.message
                                                            ?: "Unable to change password. Check your current password and try again."
                                                }
                                            } finally {
                                                changingPassword = false
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !changingPassword
                        ) {
                            Text(
                                if (changingPassword) {
                                    "Changing password..."
                                } else {
                                    "Change password"
                                }
                            )
                        }

                        passwordMessage?.let { message ->
                            Text(
                                text = message,
                                color = if (passwordChangeSucceeded) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            text = "If you signed in only with Google and have no EzybagApp password, use the password-reset option on the sign-in screen instead.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text(
                    text = "Sign Out",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    expandedSection = "delete"
                    deleteAccountPassword = ""
                    deleteAccountMessage = null
                    deleteAccountSucceeded = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(9.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.error
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Delete Account",
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (expandedSection == "delete") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Delete your account permanently?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "This deletes your Firebase sign-in account. " +
                                "Your Firestore profile and other personal records may remain. " +
                                "Shared bookings and contractor job history are preserved. " +
                                "This is not yet complete deletion of all personal data.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = deleteAccountPassword,
                        onValueChange = {
                            deleteAccountPassword = it
                            deleteAccountMessage = null
                            deleteAccountSucceeded = false
                        },
                        label = { Text("Current password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !deletingAccount
                    )

                    deleteAccountMessage?.let { message ->
                        Text(
                            text = message,
                            color = if (deleteAccountSucceeded) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            if (deleteAccountPassword.isBlank()) {
                                deleteAccountMessage =
                                    "Enter your current password to continue."
                            } else {
                                scope.launch {
                                    deletingAccount = true
                                    deleteAccountMessage = null
                                    deleteAccountSucceeded = false

                                    try {
                                        val result =
                                            repository.deleteCurrentAccount(
                                                currentPassword = deleteAccountPassword
                                            )

                                        result.fold(
                                            onSuccess = {
                                                deleteAccountSucceeded = true
                                                deleteAccountMessage =
                                                    "Your Firebase sign-in account was deleted. " +
                                                            "Some personal data may remain in Firestore."

                                                deleteAccountPassword = ""

                                                repository.signOut()
                                                onSignOut()
                                            },
                                            onFailure = { error ->
                                                deleteAccountMessage =
                                                    error.message
                                                        ?: "Account deletion failed. Please try again."
                                            }
                                        )
                                    } catch (error: Exception) {
                                        deleteAccountMessage =
                                            error.message
                                                ?: "Account deletion failed. Please try again."
                                    } finally {
                                        deletingAccount = false
                                    }
                                }
                            }
                        },
                        enabled = !deletingAccount,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            if (deletingAccount) {
                                "Deleting account..."
                            } else {
                                "Permanently delete account"
                            }
                        )
                    }

                    TextButton(
                        onClick = {
                            expandedSection = null
                            deleteAccountPassword = ""
                            deleteAccountMessage = null
                            deleteAccountSucceeded = false
                        },
                        enabled = !deletingAccount,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    email: String,
    phone: String,
    initial: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initial,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Column {
            Text(
                text = name,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "$email · $phone",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExpandableRow(
    title: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Icon(
            imageVector = if (expanded) {
                Icons.Default.KeyboardArrowUp
            } else {
                Icons.Default.KeyboardArrowDown
            },
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DetailLine(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value.ifBlank { "Not provided" },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
