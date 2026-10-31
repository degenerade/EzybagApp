package bag.ezy.ezybagapp.feature.admin.Settings

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
fun SettingsScreen(
    onSignOut: () -> Unit,
    onBack: () -> Unit = {},
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
            TextButton(onClick = onBack) {
                Text("← Back to Dashboard")
            }

            Spacer(Modifier.height(8.dp))

            when {
                loading -> Text("Loading account...")

                profile != null -> {
                    val customer = profile!!
                    ProfileHeader(
                        name = customer.name.ifBlank { "Admin" },
                        email = customer.email.ifBlank {
                            "No email available"
                        },
//                        phone = customer.phone.ifBlank {
//                            "Phone not added"
//                        },
                        initial = customer.name
                            .firstOrNull()
                            ?.uppercase()
                            ?: "A"
                    )
                }

                else -> Text(
                    text = "No signed-in user found.",
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

//            ExpandableRow(
//                title = "Privacy and data",
//                expanded = expandedSection == "privacy",
//                onClick = {
//                    expandedSection =
//                        if (expandedSection == "privacy") null
//                        else "privacy"
//                }
//            )

//            if (expandedSection == "privacy") {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        "Information stored",
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        "EzybagApp may store your name, email to provide and manage the service."
//                    )
//
//                    Text(
//                        "How it is used",
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        "Your information is used to manage your account, arrange bookings and provide support."
//                    )
//                }
//            }

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
                                                    "Your Firebase sign-in account was deleted."

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
//    phone: String,
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
                fontWeight = FontWeight.Bold
            )

//            Text(
//                text = if (phone.isNotBlank()) "$email · $phone" else email,
//                fontSize = 12.sp,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
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
