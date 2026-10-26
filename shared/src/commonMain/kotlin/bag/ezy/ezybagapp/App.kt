
package bag.ezy.ezybagapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import bag.ezy.ezybagapp.feature.bookingdetails.BookingDetailsScreen
import bag.ezy.ezybagapp.feature.contractor.ContractorSignupScreen
import bag.ezy.ezybagapp.feature.contractorjobs.ContractorJobsScreen
import bag.ezy.ezybagapp.feature.dbdevpage.DbScreen
import bag.ezy.ezybagapp.feature.main.MainContainer
import bag.ezy.ezybagapp.feature.main.MainTab
import bag.ezy.ezybagapp.feature.newbooking.NewBookingDetailsScreen
import bag.ezy.ezybagapp.feature.newbooking.NewBookingViewModel
import bag.ezy.ezybagapp.feature.newbooking.ScheduleBagQuoteScreen
import bag.ezy.ezybagapp.feature.ratings.RatingsScreen
import bag.ezy.ezybagapp.feature.startPage.StartScreen
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme

enum class Screen {
    DevMenu,
    Start,
    SignIn,
    SignUp,
    ContractorSignup,
    ContractorJobs,
    Home,
    NewBookingDetails,
    NewBookingSchedule,
    BookingDetails,
    Ratings,
    AdminDashboard,
    DbTest,
}

@Composable
fun App() {
    ClearawayTheme {
        var screen by rememberSaveable {
            mutableStateOf(if (DEV_MENU_ENABLED) Screen.DevMenu else Screen.SignIn)
        }
        var customerTab by rememberSaveable { mutableStateOf(MainTab.Home) }
        var selectedBookingId by rememberSaveable { mutableStateOf<String?>(null) }

        val authVm = viewModel { AuthViewModel() }

        val newBookingVm = viewModel {
            NewBookingViewModel(AppContainer.bookingRepository)
        }

        fun startBooking(wasteType: String?) {
            newBookingVm.reset()
            wasteType?.let { newBookingVm.onWasteTypeSelected(it) }
            screen = Screen.NewBookingDetails
        }

        Box(Modifier.fillMaxSize()) {
            when (screen) {
                Screen.DevMenu -> DevMenu(onOpen =  { target ->
                    if (target == Screen.BookingDetails && selectedBookingId == null) {
                        selectedBookingId = "CC-10482"
                    }
                    screen = target
                })

                Screen.Start -> StartScreen(
                    onCustomerSignUp = {
                        screen = Screen.SignUp
                    },
                    onContractorSignUp = {
                        screen = Screen.ContractorSignup
                    },
                    onSignIn = {
                        screen = Screen.SignIn
                    }
                )

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

                Screen.DbTest -> DbScreen()

                Screen.ContractorSignup -> ContractorSignupScreen()

                Screen.ContractorJobs -> ContractorJobsScreen(
                    onOpenJob = { }
                )

                Screen.Home -> MainContainer(
                    selectedTab = customerTab,
                    onTabSelected = { customerTab = it },
                    onStartBooking = { startBooking(it) },
                    onOpenBooking = { id ->
                        selectedBookingId = id
                        screen = Screen.BookingDetails
                                    },
                    onSignOut = { screen = Screen.SignIn }
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
                        customerTab = MainTab.Bookings
                        screen = Screen.Home
                    }
                )

                Screen.BookingDetails -> BookingDetailsScreen(
                    bookingId = selectedBookingId ?: "",
                    onBack = { screen = Screen.Home }
                )

                Screen.Ratings -> RatingsScreen(
                    bookingId = "demo",
                    onReportIssue = { /* TODO */ }
                )

                Screen.AdminDashboard -> AdminDashboardScreen()
            }

            if (DEV_MENU_ENABLED && screen != Screen.DevMenu) {
                DevMenuButton(
                    onClick = {
                        screen = Screen.DevMenu
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .safeContentPadding()
                        .padding(8.dp)
                )
            }
        }
    }
}
