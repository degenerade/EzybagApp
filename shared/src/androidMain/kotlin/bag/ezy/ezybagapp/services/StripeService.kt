package bag.ezy.ezybagapp.services

import com.stripe.StripeClient
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams

class StripeService(
    private val apiKey: String = stripeSecretKey
) {

    fun createPaymentIntent(
        amount: Long,
        currency: String = "gbp",
        paymentMethodId: String? = null
    ): String? {
        val keyToUse = apiKey.ifBlank { stripeSecretKey }
        val stripeClient = StripeClient(keyToUse)

        val paramsBuilder = PaymentIntentCreateParams.builder()
            .setAmount(amount)
            .setCurrency(currency)
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            )

        if (!paymentMethodId.isNullOrEmpty()) {
            paramsBuilder.setPaymentMethod(paymentMethodId)
        }

        val params = paramsBuilder.build()

        return try {
            val paymentIntent: PaymentIntent = stripeClient.v1().paymentIntents().create(params)
            paymentIntent.clientSecret
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        var stripeSecretKey: String = ""
    }
}
