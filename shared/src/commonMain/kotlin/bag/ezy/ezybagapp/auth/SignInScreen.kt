package bag.ezy.ezybagapp.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignInScreen(
    vm: AuthViewModel,
    onSignedIn: () -> Unit,
    onCreateAccount: () -> Unit,
) {
    val s = vm.state
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Clearaway", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "Book a waste pickup in minutes",
            fontSize = 26.sp, lineHeight = 30.sp, fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = s.email, onValueChange = vm::onEmailChange,
            label = { Text("Email") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = s.password, onValueChange = vm::onPasswordChange,
            label = { Text("Password") }, singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) { Text(if (showPassword) "Hide" else "Show") }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        TextButton(onClick = vm::forgotPassword, modifier = Modifier.align(Alignment.End)) {
            Text("Forgot password?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        s.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        s.info?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

        Button(
            onClick = { vm.signIn(onSignedIn) },
            enabled = !s.isLoading,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
            if (s.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            else Text("Sign in")
        }
        OutlinedButton(
            onClick = onCreateAccount,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text("Create account") }

        Text(
            "or continue with", textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { /* TODO: Google via Firebase */ }, shape = MaterialTheme.shapes.small, modifier = Modifier.weight(1f)) { Text("Google") }
            OutlinedButton(onClick = { /* TODO: Apple via Firebase*/ }, shape = MaterialTheme.shapes.small, modifier = Modifier.weight(1f)) { Text("Apple") }
        }
    }
}