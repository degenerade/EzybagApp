package bag.ezy.ezybagapp

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    firebaseStatus: String = "Firebase: Not tested",
    operationResult: String = "Tap a button below to test CRUD",
    onCreateCustomer: () -> Unit = {},
    onReadCustomer: () -> Unit = {},
    onUpdateCustomer: () -> Unit = {},
    onDeleteCustomer: () -> Unit = {}
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = firebaseStatus,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Firestore CRUD Test Output:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = operationResult,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onCreateCustomer) {
                Text("1. Create Customer")
            }

            Button(onClick = onReadCustomer) {
                Text("2. Read Customer")
            }

            Button(onClick = onUpdateCustomer) {
                Text("3. Update Customer")
            }

            Button(onClick = onDeleteCustomer) {
                Text("4. Delete Customer")
            }
        }
    }
}
