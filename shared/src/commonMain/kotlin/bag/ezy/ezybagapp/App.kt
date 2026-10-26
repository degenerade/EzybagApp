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
import bag.ezy.ezybagapp.feature.auth.ContractorSignupScreen
import bag.ezy.ezybagapp.feature.auth.SignInScreen
import bag.ezy.ezybagapp.feature.auth.SignUpScreen
import bag.ezy.ezybagapp.feature.bookings.BookingsScreen
import bag.ezy.ezybagapp.feature.newbooking.NewBookingDetailsScreen
import bag.ezy.ezybagapp.feature.newbooking.NewBookingViewModel
import bag.ezy.ezybagapp.feature.newbooking.ScheduleBagQuoteScreen
import bag.ezy.ezybagapp.feature.ratings.RatingsScreen
import bag.ezy.ezybagapp.feature.startPage.StartScreen
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme
import bag.ezy.ezybagapp.feature.bookings.BookingConfirmationScreen


import bag.ezy.ezybagapp.feature.contractormockpage.MockContractorHomeScreen

enum class Screen {
    DevMenu,
    Start,
    SignIn,
    SignUp,
    ContractorSignup,
    ContractorHome,
    Home,
    Bookings,
    NewBookingDetails,
    NewBookingSchedule,
    BookingConfirmation,
    Ratings,
    AdminDashboard
}

@Composable
fun App() {
    ClearawayTheme {
        var screen by rememberSaveable {
            mutableStateOf(
                if (DEV_MENU_ENABLED) {
                    Screen.DevMenu
                } else {
                    Screen.SignIn
                }
            )
        }

        // Keep the booking ID available when navigating to confirmation.
        var confirmedBookingId by rememberSaveable {
            mutableStateOf("")
        }

        val authVm = viewModel {
            AuthViewModel()
        }

        val newBookingVm = viewModel {
            NewBookingViewModel(AppContainer.bookingRepository)
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (screen) {

                Screen.DevMenu -> {
                    DevMenu(
                        onOpen = { destination ->
                            screen = destination
                        }
                    )
                }

                Screen.Start -> {
                    StartScreen(
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
                }

                Screen.SignIn -> {
                    SignInScreen(
                        vm = authVm,
                        onSignedIn = {
                            role -> screen = when (role.lowercase()) {
                                "admin" -> Screen.AdminDashboard
                                "contractor" -> Screen.ContractorHome
                                else -> Screen.Home
                            }
                        },
                        onCreateAccount = {
                            authVm.clearMessage()
                            screen = Screen.SignUp
                        }
                    )
                }

                Screen.SignUp -> {
                    SignUpScreen(
                        vm = authVm,
                        onSignedUp = {
                            screen = Screen.Home
                        },
                        onBackToSignIn = {
                            authVm.clearMessage()
                            screen = Screen.SignIn
                        }
                    )
                }

                Screen.ContractorSignup -> {
                    ContractorSignupScreen()
                }

                Screen.ContractorHome -> {
                    MockContractorHomeScreen()
                }

                Screen.Home -> {
                    MainContainer(
                        onCreatePickup = {
                            newBookingVm.reset()
                            confirmedBookingId = ""
                            screen = Screen.NewBookingDetails
                        },
                        onOpenBookings = {
                            screen = Screen.Bookings
                        }
                    )
                }

                Screen.Bookings -> {
                    BookingsScreen(
                        onBookAnother = {
                            newBookingVm.reset()
                            confirmedBookingId = ""
                            screen = Screen.NewBookingDetails
                        }
                    )
                }

                Screen.NewBookingDetails -> {
                    NewBookingDetailsScreen(
                        vm = newBookingVm,
                        onContinue = {
                            screen = Screen.NewBookingSchedule
                        }
                    )
                }

                Screen.NewBookingSchedule -> {
                    ScheduleBagQuoteScreen(
                        vm = newBookingVm,
                        onBack = {
                            screen = Screen.NewBookingDetails
                        },
                        onBooked = { bookingId ->
                            // The booking has been saved successfully.
                            // Preserve its ID for the confirmation screen.
                            confirmedBookingId = bookingId
                            screen = Screen.BookingConfirmation
                        }
                    )
                }

                Screen.BookingConfirmation -> {
                    BookingConfirmationScreen(
                        bookingId = confirmedBookingId,
                        onViewBookings = {
                            screen = Screen.Bookings
                        },
                        onReturnHome = {
                            newBookingVm.reset()
                            confirmedBookingId = ""
                            screen = Screen.Home
                        },
                        onBookAnother = {
                            newBookingVm.reset()
                            confirmedBookingId = ""
                            screen = Screen.NewBookingDetails
                        }
                    )
                }

                Screen.Ratings -> {
                    RatingsScreen(
                        bookingId = "demo",
                        onReportIssue = {
                            // TODO: Connect issue reporting.
                        }
                    )
                }

                Screen.AdminDashboard -> {
                    AdminDashboardScreen()
                }
            }

            // Development navigation button.
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