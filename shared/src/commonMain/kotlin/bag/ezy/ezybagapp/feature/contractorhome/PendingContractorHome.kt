package bag.ezy.ezybagapp.feature.contractorhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PendingContractorHome(
    firstName: String,
    companyName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hi ${firstName.ifBlank { "there" }}",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        if (companyName.isNotBlank()) {
            Text(
                text = companyName,
                fontSize = 14.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Application pending",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Your contractor application is currently being reviewed.",
                    fontSize = 14.sp
                )

                Text(
                    text = "Once your account has been approved, you'll be able to view your jobs and access available work.",
                    fontSize = 14.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "You can still use Account to view your submitted details, or Support if you need help.",
            fontSize = 14.sp,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}