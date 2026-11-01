package bag.ezy.ezybagapp.feature.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bag.ezy.ezybagapp.model.repository.BookingRepository
import bag.ezy.ezybagapp.services.StripeService
import com.stripe.android.paymentsheet.CreateIntentResult
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun CheckoutScreen(
    bookingId: String,
    price: Int,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: ((errorMessage: String) -> Unit)?,
    onPaymentCanceled: (() -> Unit)?,
    onBack: (() -> Unit)?,
    repo: BookingRepository
) {
    val stripeService = remember { StripeService() }
    val scope = rememberCoroutineScope()
    var paymentError by remember { mutableStateOf<String?>(null) }

    val amountInPence = price.toLong() * 100

    val paymentSheet = rememberPaymentSheet(
        createIntentCallback = { paymentMethod, _ ->
            try {
                val clientSecret = stripeService.createPaymentIntent(
                    amount = if (amountInPence > 0) amountInPence else 1000L,
                    currency = "gbp",
                    paymentMethodId = paymentMethod.id
                )
                if (clientSecret != null) {
                    CreateIntentResult.Success(clientSecret)
                } else {
                    CreateIntentResult.Failure(
                        cause = Exception("Failed to create Stripe payment intent"),
                        displayMessage = "Unable to create payment intent"
                    )
                }
            } catch (e: Exception) {
                CreateIntentResult.Failure(
                    cause = e,
                    displayMessage = e.message
                )
            }
        },
        paymentResultCallback = { paymentSheetResult ->
            when (paymentSheetResult) {
                is PaymentSheetResult.Canceled -> {
                    paymentError = "Payment canceled."
                    scope.launch {
                        if (bookingId.isNotBlank()) {
                            repo.updatePaymentStatus(bookingId, "canceled")
                        }
                        if (onPaymentCanceled != null) {
                            onPaymentCanceled()
                        }
                    }
                }

                is PaymentSheetResult.Failed -> {
                    val errorMsg = paymentSheetResult.error.localizedMessage ?: "Payment failed."
                    paymentError = errorMsg
                    scope.launch {
                        if (bookingId.isNotBlank()) {
                            repo.updatePaymentStatus(bookingId, "failed")
                        }
                        if (onPaymentFailed != null) {
                            onPaymentFailed(errorMsg)
                        }
                    }
                }

                is PaymentSheetResult.Completed -> {
                    paymentError = null
                    scope.launch {
                        if (bookingId.isNotBlank()) {
                            repo.updatePaymentStatus(bookingId, "successful")
                        }
                        onPaymentSuccess()
                    }
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Total Amount",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "£$price",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            paymentError?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    paymentError = null
                    val intentConfig = PaymentSheet.IntentConfiguration(
                        mode = PaymentSheet.IntentConfiguration.Mode.Payment(
                            amount = if (amountInPence > 0) amountInPence else 1000L,
                            currency = "gbp",
                        ),
                    )

                    paymentSheet.presentWithIntentConfiguration(
                        intentConfiguration = intentConfig,
                        configuration = PaymentSheet.Configuration.Builder(
                            merchantDisplayName = "Ezybag",
                        ).build()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Pay £$price with Stripe",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
