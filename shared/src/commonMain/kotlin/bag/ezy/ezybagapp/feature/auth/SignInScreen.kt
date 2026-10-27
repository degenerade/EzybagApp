package bag.ezy.ezybagapp.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SignInScreen(
    vm: AuthViewModel,
    onSignedIn: (role:String) -> Unit,
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = s.password, onValueChange = vm::onPasswordChange,
            label = { Text("Password") }, singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                TextButton(onClick = {
                    showPassword = !showPassword
                }) { Text(if (showPassword) "Hide" else "Show") }
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
            if (s.isLoading) CircularProgressIndicator(
                Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
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
            OutlinedButton(
                onClick = { /* TODO: Google via Firebase */ },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f)
            ) { Text("Google") }

            //we don't have apple!
//            OutlinedButton(
//                onClick = { /* TODO: Apple via Firebase*/ },
//                shape = MaterialTheme.shapes.small,
//                modifier = Modifier.weight(1f)
//            ) { Text("Apple") }
        }

//        OutlinedButton(
//            onClick = { /* TODO: Google via Firebase */ },
//            shape = MaterialTheme.shapes.small,
//            modifier = Modifier.weight(1f),
//            contentPadding = PaddingValues(vertical = 14.dp),
//            colors = ButtonDefaults.outlinedButtonColors(
//                contentColor = MaterialTheme.colorScheme.onSurface
//            )
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Canvas(
//                    modifier = Modifier.size(22.dp)
//                ) {
//                    val strokeWidth = size.width * 0.19f
//                    val arcSize = size.width * 0.76f
//                    val topLeft = Offset(
//                        (size.width - arcSize) / 2f,
//                        (size.height - arcSize) / 2f
//                    )
//
//                    drawArc(
//                        color = Color(0xFF4285F4),
//                        startAngle = -45f,
//                        sweepAngle = 90f,
//                        useCenter = false,
//                        topLeft = topLeft,
//                        size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
//                        style = Stroke(strokeWidth, cap = StrokeCap.Butt)
//                    )
//
//                    drawArc(
//                        color = Color(0xFF34A853),
//                        startAngle = 45f,
//                        sweepAngle = 90f,
//                        useCenter = false,
//                        topLeft = topLeft,
//                        size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
//                        style = Stroke(strokeWidth, cap = StrokeCap.Butt)
//                    )
//
//                    drawArc(
//                        color = Color(0xFFFBBC05),
//                        startAngle = 135f,
//                        sweepAngle = 90f,
//                        useCenter = false,
//                        topLeft = topLeft,
//                        size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
//                        style = Stroke(strokeWidth, cap = StrokeCap.Butt)
//                    )
//
//                    drawArc(
//                        color = Color(0xFFEA4335),
//                        startAngle = 225f,
//                        sweepAngle = 90f,
//                        useCenter = false,
//                        topLeft = topLeft,
//                        size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
//                        style = Stroke(strokeWidth, cap = StrokeCap.Butt)
//                    )
//
//                    drawLine(
//                        color = Color(0xFF4285F4),
//                        start = Offset(size.width * 0.52f, size.height * 0.5f),
//                        end = Offset(size.width * 0.96f, size.height * 0.5f),
//                        strokeWidth = strokeWidth,
//                        cap = StrokeCap.Butt
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(10.dp))
//
//                Text("Google")
//            }
  //      }
    }
}