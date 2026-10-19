package bag.ezy.ezybagapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bag.ezy.ezybagapp.viewmodel.AuthViewModel

@Composable
fun AuthScreen() {
    val viewModel = remember { AuthViewModel() }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Firebase Auth, Reviews & RBAC Demo",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "Status / Output:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = viewModel.statusMessage,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )

        if (viewModel.isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Sign up with mock data
        Button(onClick = { viewModel.signUpWithMockData() }, modifier = Modifier.fillMaxWidth()) {
            Text("1. Sign Up (Mock Data)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Log in by mail/password
        Button(onClick = { viewModel.loginWithEmail("mock_user_4015@example.com", "password123") }, modifier = Modifier.fillMaxWidth()) {
            Text("2. Log In (Mail / Password)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Log in by Google
        Button(onClick = { viewModel.loginWithGoogle() }, modifier = Modifier.fillMaxWidth()) {
            Text("3. Google Authorization")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sign Out button
        Button(onClick = { viewModel.signOut() }, modifier = Modifier.fillMaxWidth()) {
            Text("🔒 Sign Out")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "--- Review Action ---", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // 4. Leave Review (Single button checking auth state)
        Button(onClick = { viewModel.leaveReview() }, modifier = Modifier.fillMaxWidth()) {
            Text("Leave Review")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "--- RBAC (Admin / Customer / Unauthorized) ---", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // 5. Unauthorized Access Test
        Button(onClick = { viewModel.testUnauthorizedAccess() }, modifier = Modifier.fillMaxWidth()) {
            Text("5. RBAC: Unauthorized Access")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6. Customer Access Test
        Button(onClick = { viewModel.testCustomerAccess() }, modifier = Modifier.fillMaxWidth()) {
            Text("6. RBAC: Customer Access")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 7. Admin Access Test
        Button(onClick = { viewModel.testAdminAccess() }, modifier = Modifier.fillMaxWidth()) {
            Text("7. RBAC: Admin Access")
        }
    }
}
