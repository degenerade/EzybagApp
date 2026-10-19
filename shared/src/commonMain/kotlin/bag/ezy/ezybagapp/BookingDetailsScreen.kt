package bag.ezy.ezybagapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ezybagapp.shared.generated.resources.Res
import ezybagapp.shared.generated.resources.call
import ezybagapp.shared.generated.resources.cancel
import ezybagapp.shared.generated.resources.edit_booking
import ezybagapp.shared.generated.resources.get_help
import ezybagapp.shared.generated.resources.message
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { BackButton(onBackClick = onBackClick) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Title(
                wasteType = "Construction waste",
                volume = "half van"
            )
            JobStatusAndDateRow(
                jobStatus = "En route",
                date = "Thu 8 Oct",
                time = "9am–12pm"
            )
            Map()
            Job(
                description = "Old kitchen units and rubble",
                volume = "Half van",
                bag = "Medium bag"
            )
            Collection(
                address = "12 Elm Road, M1 4BT",
                access = "Side gate, code 1234",
                instructions = "Call on arrival"
            )
            Contractor(
                contractorName = "Jake R.",
                contractorInitial = "J",
                contractorRating = "4.8",
                contractorCompanyName = "Drop Waste Ltd"
            )
            Payment(
                totalPrice = "£145",
                paymentStatus = "Paid",
                paymentMethod = "Visa ending 4417"
            )
            ButtonsRow()
        }
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.clickable(onClick = onBackClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "Back",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun Title(
    wasteType: String,
    volume: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$wasteType · $volume",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun JobStatusAndDateRow(
    jobStatus: String,
    date: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = jobStatus,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "$date · $time",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun Map(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Map Preview",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun Job(
    description: String,
    volume: String,
    bag: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Job",
            fontWeight = FontWeight.ExtraBold,
            modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        CardRows(
            parameterHeaderOne = "Description",
            parameterNameOne = description,
            parameterHeaderTwo = "Volume",
            parameterNameTwo = volume,
            parameterHeaderThree = "Bag",
            parameterNameThree = bag
        )
    }
}

@Composable
fun Collection(
    address: String,
    access: String,
    instructions: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Collection",
            fontWeight = FontWeight.ExtraBold,
            modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        CardRows(
            parameterHeaderOne = "Address",
            parameterNameOne = address,
            parameterHeaderTwo = "Access",
            parameterNameTwo = access,
            parameterHeaderThree = "Instructions",
            parameterNameThree = instructions
        )
    }
}

@Composable
fun Contractor(
    contractorName: String,
    contractorInitial: String,
    contractorRating: String,
    contractorCompanyName: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Contractor",
            fontWeight = FontWeight.ExtraBold,
            modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = contractorInitial,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
                Column {
                    Text(
                        text = "$contractorName · $contractorCompanyName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = contractorRating,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.message),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.call),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun Payment(
    totalPrice: String,
    paymentStatus: String,
    paymentMethod: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Payment",
            fontWeight = FontWeight.ExtraBold,
            modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        CardRows(
            parameterHeaderOne = "Total",
            parameterNameOne = totalPrice,
            parameterHeaderTwo = "Status",
            parameterNameTwo = paymentStatus,
            parameterHeaderThree = "Method",
            parameterNameThree = paymentMethod
        )
    }
}

@Composable
fun CardRows(
    parameterHeaderOne: String,
    parameterNameOne: String,
    parameterHeaderTwo: String,
    parameterNameTwo: String,
    parameterHeaderThree: String,
    parameterNameThree: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = parameterHeaderOne, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = parameterNameOne, fontWeight = FontWeight.SemiBold)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = parameterHeaderTwo, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = parameterNameTwo, fontWeight = FontWeight.SemiBold)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = parameterHeaderThree, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = parameterNameThree, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ButtonsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {},
            modifier = Modifier.weight(1f).height(40.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text(
                text = stringResource(Res.string.edit_booking),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        OutlinedButton(
            onClick = {},
            modifier = Modifier.weight(1f).height(40.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text(
                text = stringResource(Res.string.cancel),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Button(
            onClick = {},
            modifier = Modifier.weight(1f).height(40.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text(
                text = stringResource(Res.string.get_help),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
