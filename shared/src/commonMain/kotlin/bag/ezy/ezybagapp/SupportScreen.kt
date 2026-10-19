package bag.ezy.ezybagapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Support", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Our team is here Monday to Saturday, 8am to 6pm.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier.padding(bottom = 16.dp)
            )
            CallUs(companyPhoneNumber = "+44 20 0000 0000")
            Spacer(modifier = modifier.height(16.dp))
            EmailUs(companyEmail = "support@clearaway.example")
            Text(
                text = "We usually reply within one working day.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier.padding(16.dp),
            )
        }
    }
}

@Composable
fun CallUs(modifier: Modifier = Modifier, companyPhoneNumber: String) {
    Column(modifier = modifier.fillMaxWidth()) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = modifier.fillMaxWidth()
        ){
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = modifier.width(16.dp))
                Column {
                    Text(text = "Call us", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = companyPhoneNumber, fontSize = 12.sp)
                }
            }
        }
        Spacer(modifier = modifier.height(8.dp))
        Button(
            onClick = {},
            modifier = modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text(text = "Call now", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun EmailUs(modifier: Modifier = Modifier, companyEmail: String) {
    Column(modifier = modifier.fillMaxWidth()) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = modifier.width(16.dp))
                Column {
                    Text(text = "Email us", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = companyEmail, fontSize = 12.sp)
                }
            }
        }
        Spacer(modifier = modifier.height(8.dp))
        Button(
            onClick = {},
            modifier = modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text(text = "Send an email",fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
