package bag.ezy.ezybagapp.feature.startpage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.model.data.UserRole
import bag.ezy.ezybagapp.ui.theme.EzybagTheme

@Composable
fun StartScreen(
    onCustomerSignUp: () -> Unit,
    onContractorSignUp: () -> Unit,
    onSignIn: () -> Unit,
    vm: StartViewModel = viewModel()
) {
    StartContent(
        state = vm.state,
        onCustomerSignUp = onCustomerSignUp,
        onContractorSignUp = onContractorSignUp,
        onSignIn = onSignIn
    )
}

@Composable
fun StartContent(
    state: StartUiState,
    onCustomerSignUp: () -> Unit,
    onContractorSignUp: () -> Unit,
    onSignIn: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "♻",
                fontSize = 42.sp,
                color = colors.primary
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "EZYBAG",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.primary,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Waste collection, organised.",
                fontSize = 16.sp,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            // Short explanation
            Text(
                text = "Connecting customers who need waste collected with contractors ready to collect it.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            // Customer and contractor buttons
            state.options.forEach { option ->
                when (option.role) {
                    UserRole.CUSTOMER -> {
                        Button(
                            onClick = onCustomerSignUp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary,
                                contentColor = colors.onPrimary
                            )
                        ) {
                            Text(
                                text = option.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    UserRole.CONTRACTOR -> {
                        OutlinedButton(
                            onClick = onContractorSignUp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, colors.primary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colors.primary
                            )
                        ) {
                            Text(
                                text = option.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
            }

            Spacer(Modifier.height(20.dp))

            // Sign-in section
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, colors.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.primary
                )
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StartScreenPreview() {
    EzybagTheme {
        StartContent(
            state = StartUiState(),
            onCustomerSignUp = {},
            onContractorSignUp = {},
            onSignIn = {}
        )
    }
}
