package bag.ezy.ezybagapp.feature.payment

import androidx.compose.runtime.Composable
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.model.repository.BookingRepository

@Composable
expect fun CheckoutScreen(
    bookingId: String,
    price: Int,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: ((errorMessage: String) -> Unit)? = null,
    onPaymentCanceled: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    repo: BookingRepository = AppContainer.bookingRepository
)
