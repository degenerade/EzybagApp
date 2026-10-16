package bag.ezy.ezybagapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.feature.auth.AuthViewModel
import bag.ezy.ezybagapp.feature.auth.SignInScreen
import bag.ezy.ezybagapp.feature.auth.SignUpScreen
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme

enum class Screen { SignIn, SignUp, Home }

@Composable
fun App() {
    ClearawayTheme {
        var screen by rememberSaveable { mutableStateOf(Screen.SignIn) }
        val authVm = viewModel { AuthViewModel() }

        when (screen) {
            Screen.SignIn -> SignInScreen(
                vm = authVm,
                onSignedIn = { screen = Screen.Home },
                onCreateAccount = { authVm.clearMessage(); screen = Screen.SignUp },
            )
            Screen.SignUp -> SignUpScreen(
                vm = authVm,
                onSignedUp = { screen = Screen.Home },
                onBackToSignIn = { authVm.clearMessage(); screen = Screen.SignIn },
            )
            Screen.Home -> HomePlaceholder(onSignOut = { screen = Screen.SignIn })
        }
    }
}

@Composable
private fun HomePlaceholder(onSignOut: () -> Unit) {
    Column(
        Modifier.fillMaxSize().safeContentPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Signed in! Booking screen goes here.")
        Button(onClick = onSignOut) { Text("Sign out") }
    }
}