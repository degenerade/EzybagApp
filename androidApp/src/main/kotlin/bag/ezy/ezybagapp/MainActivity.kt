package bag.ezy.ezybagapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import bag.ezy.ezybagapp.model.queries.GoogleAuthHandler
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Register the native Android Google Sign-In handler callback using Credential Manager
        GoogleAuthHandler.onGoogleSignInRequested = {
            withContext(Dispatchers.Main) {
                try {
                    // Read dynamically from google-services.json generated resources without hardcoding
                    val webClientId = getString(R.string.default_web_client_id)

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setServerClientId(webClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    // Trigger native Android Google account selector pop-up on the phone
                    val credentialManager = CredentialManager.create(this@MainActivity)
                    val result = credentialManager.getCredential(this@MainActivity, request)
                    val credential = result.credential

                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken
                        val email = googleIdTokenCredential.id
                        val displayName = googleIdTokenCredential.displayName ?: "Google User"

                        // Authenticate with Firebase Auth
                        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                        val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
                        val firebaseUser = authResult.user

                        if (firebaseUser != null) {
                            Result.success(Pair(firebaseUser.email ?: email, firebaseUser.displayName ?: displayName))
                        } else {
                            Result.failure(Exception("Firebase user was null after Google sign-in"))
                        }
                    } else {
                        Result.failure(Exception("Unexpected credential type received"))
                    }
                } catch (e: Exception) {
                    Log.e("GoogleAuthError", "Google sign-in exception: ${e.javaClass.simpleName} - ${e.message}", e)
                    Result.failure(e)
                }
            }
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
