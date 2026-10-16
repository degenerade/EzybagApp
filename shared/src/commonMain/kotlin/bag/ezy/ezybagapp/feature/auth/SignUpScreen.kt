package bag.ezy.ezybagapp.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreen(
    vm: AuthViewModel,
    onSignedUp: () -> Unit,
    onBackToSignIn: () -> Unit,
) {
    val s = vm.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TextButton(
            onClick = onBackToSignIn,
            contentPadding = PaddingValues(0.dp)
        ) { Text("← Back") }
        Text("Create your account", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "Book and track pickups in one place",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            s.name,
            vm::onNameChange,
            label = { Text("Full name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            s.email, vm::onEmailChange, label = { Text("Email") }, singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ), modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            s.password, vm::onPasswordChange, label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ), modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            s.confirmPassword,
            vm::onConfirmChange,
            label = { Text("Confirm password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        s.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { vm.signUp(onSignedUp) },
            enabled = !s.isLoading,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(top = 4.dp),
        ) {
            if (s.isLoading) CircularProgressIndicator(
                Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            else Text("Create account")
        }
        TextButton(
            onClick = onBackToSignIn,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already have an account? Sign in")
        }
    }
}