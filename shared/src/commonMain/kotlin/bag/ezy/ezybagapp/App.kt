package bag.ezy.ezybagapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.feature.auth.AuthViewModel
import bag.ezy.ezybagapp.feature.auth.SignInScreen
import bag.ezy.ezybagapp.feature.auth.SignUpScreen
import bag.ezy.ezybagapp.feature.bookings.BookingsScreen
import bag.ezy.ezybagapp.feature.newbooking.NewBookingDetailsScreen
import bag.ezy.ezybagapp.feature.newbooking.NewBookingViewModel
import bag.ezy.ezybagapp.feature.newbooking.ScheduleBagQuoteScreen
import bag.ezy.ezybagapp.feature.ratings.RatingsScreen
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme

enum class Screen { SignIn, SignUp, Home, Bookings, NewBookingDetails, NewBookingSchedule, Ratings}

@Composable
fun App() {
    ClearawayTheme {
        var screen by rememberSaveable { mutableStateOf(Screen.SignIn) }
        val authVm = viewModel { AuthViewModel() }
        val newBookingVm = viewModel { NewBookingViewModel(AppContainer.bookingRepository) }

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
            Screen.Bookings -> BookingsScreen(
                onBookAnother = { newBookingVm.reset(); screen = Screen.NewBookingDetails }
            )
            Screen.NewBookingDetails -> NewBookingDetailsScreen(
                vm = newBookingVm,
                onContinue = { screen = Screen.NewBookingSchedule }
            )
            Screen.NewBookingSchedule -> ScheduleBagQuoteScreen(
                vm = newBookingVm,
                onBack = { screen = Screen.NewBookingDetails },
                onBooked = { newBookingVm.reset(); screen = Screen.Bookings }
            )
            Screen.Ratings -> RatingsScreen(
                bookingId = "demo",
                onReportIssue = { /* todo */ }
            )
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