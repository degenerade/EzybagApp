
package bag.ezy.ezybagapp.feature.dbdevpage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DbScreen() {
    val viewModel = remember { DbTestViewModel() }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Firebase Development Tests",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Status / Output",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = viewModel.statusMessage,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        if (viewModel.isLoading) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.signOut() },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign out")
        }

        Text(
            text = "Review Tests",
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            onClick = { viewModel.leaveReview() },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Review Submission")
        }

        Text(
            text = "Access Control Tests",
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            onClick = { viewModel.testUnauthorizedAccess() },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Unauthenticated Access")
        }

        Button(
            onClick = { viewModel.testCustomerAccess() },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Customer Access")
        }

        Button(
            onClick = { viewModel.testAdminAccess() },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Admin Access")
        }
    }
}
