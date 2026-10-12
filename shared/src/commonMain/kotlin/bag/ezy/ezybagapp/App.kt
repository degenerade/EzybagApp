
package bag.ezy.ezybagapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.auth.AuthViewModel
import bag.ezy.ezybagapp.auth.SignInScreen
import bag.ezy.ezybagapp.auth.SignUpScreen
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme

enum class Screen {
    SignIn,
    SignUp,
    Home
}

@Composable
fun App() {
    ClearawayTheme {
        var screen by rememberSaveable {
            mutableStateOf(Screen.SignIn)
        }

        val authVm = viewModel { AuthViewModel() }

        // Keeps the state of each booking screen when navigating.
        val saveableStateHolder = rememberSaveableStateHolder()

        when (screen) {
            Screen.SignIn -> SignInScreen(
                vm = authVm,
                onSignedIn = {
                    screen = Screen.Home
                },
                onCreateAccount = {
                    authVm.clearMessage()
                    screen = Screen.SignUp
                }
            )

            Screen.SignUp -> SignUpScreen(
                vm = authVm,
                onSignedUp = {
                    screen = Screen.Home
                },
                onBackToSignIn = {
                    authVm.clearMessage()
                    screen = Screen.SignIn
                }
            )

            Screen.Home -> BookingNavigation(
                onSignOut = {
                    screen = Screen.SignIn
                },
                saveableStateHolder = saveableStateHolder
            )
        }
    }
}

@Composable
private fun BookingNavigation(
    onSignOut: () -> Unit,
    saveableStateHolder: androidx.compose.runtime.saveable.SaveableStateHolder
) {
    var showScheduleScreen by rememberSaveable {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showScheduleScreen) {
            saveableStateHolder.SaveableStateProvider("schedule-screen") {
                ScheduleBagQuoteScreen(
                    onBack = {
                        showScheduleScreen = false
                    }
                )
            }
        } else {
            saveableStateHolder.SaveableStateProvider("create-job-screen") {
                CreateJobScreen(
                    onContinue = {
                        showScheduleScreen = true
                    }
                )
            }
        }

        // Temporary sign-out button while the team Home screen is a placeholder.
        // Remove this button when the real Home screen is integrated.
        if (!showScheduleScreen) {
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Text("Sign out")
            }
        }
    }
}
