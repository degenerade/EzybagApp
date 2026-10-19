
package bag.ezy.ezybagapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.dev.DEV_MENU_ENABLED
import bag.ezy.ezybagapp.dev.DevMenu
import bag.ezy.ezybagapp.dev.DevMenuButton
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.feature.admin.AdminDashboardScreen
import bag.ezy.ezybagapp.feature.auth.AuthViewModel
import bag.ezy.ezybagapp.feature.auth.SignInScreen
import bag.ezy.ezybagapp.feature.auth.SignUpScreen
import bag.ezy.ezybagapp.feature.bookings.BookingsScreen
import bag.ezy.ezybagapp.feature.newbooking.NewBookingViewModel
import bag.ezy.ezybagapp.feature.newbooking.NewBookingDetailsScreen
import bag.ezy.ezybagapp.feature.newbooking.ScheduleBagQuoteScreen
import bag.ezy.ezybagapp.feature.ratings.RatingsScreen
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme

enum class Screen {
    DevMenu,
    PreviewMenu,
    SignIn,
    SignUp,
    ContractorSignup,
    Home,
    Bookings,
    NewBookingDetails,
    NewBookingSchedule,
    Ratings,
    AdminDashboard
}

@Composable
fun App() {
    ClearawayTheme {
        var screen by rememberSaveable {
            mutableStateOf(
                if (DEV_MENU_ENABLED) Screen.DevMenu else Screen.SignUp
            )
        }

        val authVm = viewModel { AuthViewModel() }
        val newBookingVm = viewModel {
            NewBookingViewModel(AppContainer.bookingRepository)
        }

        Box(Modifier.fillMaxSize()) {
            when (screen) {
                Screen.DevMenu -> DevMenu(
                    onOpen = { screen = it }
                )

                Screen.PreviewMenu -> ScreenPreviewMenu(
                    onSelectScreen = { screen = it }
                )

                Screen.SignIn -> SignInScreen(
                    vm = authVm,
                    onSignedIn = { screen = Screen.Home },
                    onCreateAccount = {
                        authVm.clearMessage()
                        screen = Screen.SignUp
                    }
                )

                Screen.SignUp -> SignUpScreen(
                    vm = authVm,
                    onSignedUp = { screen = Screen.Home },
                    onBackToSignIn = {
                        authVm.clearMessage()
                        screen = Screen.SignIn
                    }
                )

                Screen.ContractorSignup -> ContractorSignupScreen()

                Screen.Home -> HomePlaceholder(
                    onSignOut = { screen = Screen.SignIn }
                )

                Screen.Bookings -> BookingsScreen(
                    onBookAnother = {
                        newBookingVm.reset()
                        screen = Screen.NewBookingDetails
                    }
                )

                Screen.NewBookingDetails -> NewBookingDetailsScreen(
                    vm = newBookingVm,
                    onContinue = {
                        screen = Screen.NewBookingSchedule
                    }
                )

                Screen.NewBookingSchedule -> ScheduleBagQuoteScreen(
                    vm = newBookingVm,
                    onBack = {
                        screen = Screen.NewBookingDetails
                    },
                    onBooked = {
                        newBookingVm.reset()
                        screen = Screen.Bookings
                    }
                )

                Screen.Ratings -> RatingsScreen(
                    bookingId = "demo",
                    onReportIssue = { /* TODO */ }
                )

                Screen.AdminDashboard -> AdminDashboardScreen()
            }

            if (DEV_MENU_ENABLED && screen != Screen.DevMenu) {
                DevMenuButton(
                    onClick = { screen = Screen.DevMenu },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .safeContentPadding()
                        .padding(8.dp)
                )
            }

            if (screen != Screen.DevMenu &&
                screen != Screen.PreviewMenu
            ) {
                Button(
                    onClick = { screen = Screen.PreviewMenu },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .safeContentPadding()
                        .padding(12.dp)
                ) {
                    Text("Screen Menu")
                }
            }
        }
    }
}

@Composable
private fun ScreenPreviewMenu(
    onSelectScreen: (Screen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "EzybagApp",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Screen Preview Menu",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(24.dp))

        PreviewButton("Sign In") {
            onSelectScreen(Screen.SignIn)
        }

        PreviewButton("Sign Up") {
            onSelectScreen(Screen.SignUp)
        }

        PreviewButton("Contractor Signup") {
            onSelectScreen(Screen.ContractorSignup)
        }

        PreviewButton("Bookings") {
            onSelectScreen(Screen.Bookings)
        }

        PreviewButton("New Booking") {
            onSelectScreen(Screen.NewBookingDetails)
        }

        PreviewButton("Ratings") {
            onSelectScreen(Screen.Ratings)
        }

        PreviewButton("Admin Dashboard") {
            onSelectScreen(Screen.AdminDashboard)
        }

        PreviewButton("Home") {
            onSelectScreen(Screen.Home)
        }
    }
}

@Composable
private fun PreviewButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Text(label)
    }
}

@Composable
private fun HomePlaceholder(
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Signed in! Booking screen goes here.")

        Spacer(Modifier.height(16.dp))

        Button(onClick = onSignOut) {
            Text("Sign out")
        }
    }
}
