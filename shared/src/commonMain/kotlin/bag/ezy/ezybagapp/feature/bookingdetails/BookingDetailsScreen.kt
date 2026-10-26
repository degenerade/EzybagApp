package bag.ezy.ezybagapp.feature.bookingdetails

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.di.AppContainer
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme
import ezybagapp.shared.generated.resources.Res
import ezybagapp.shared.generated.resources.call
import ezybagapp.shared.generated.resources.cancel
import ezybagapp.shared.generated.resources.edit_booking
import ezybagapp.shared.generated.resources.get_help
import ezybagapp.shared.generated.resources.message
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookingDetailsScreen(
    bookingId: String,
    onBack: () -> Unit,
    vm: BookingDetailsViewModel = viewModel(key = "booking-$bookingId") {
        BookingDetailsViewModel(bookingId, AppContainer.bookingRepository)
    }
) {
    BookingDetailsContent(
        state = vm.state,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsContent(
    state: BookingDetailsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { BackButton(onBackClick = onBack) }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { Text(state.error, color = MaterialTheme.colorScheme.error) }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Title(
                    wasteType = state.title,
                    volume = state.volume
                )
                JobStatusAndDateRow(
                    jobStatus = state.status,
                    date = state.date,
                    time = state.time
                )
                Map()
                Job(
                    description = state.description,
                    volume = state.volume,
                    bag = state.bag
                )
                Collection(
                    address = state.address,
                    access = "Side gate, code 1234", // todo
                    instructions = "Call on arrival" // todo
                )
                Contractor(
                    contractorName = "Jake R.", // todo
                    contractorInitial = "J", // todo
                    contractorRating = "4.8", // todo
                    contractorCompanyName = "Drop Waste Ltd" // todo
                )
                Payment(
                    totalPrice = state.price,
                    paymentStatus = "Paid", // todo
                    paymentMethod = "Visa ending 4417" // todo
                )
                ButtonsRow()
            }
        }
    }
}

@Composable
private fun BackButton(
    modifier: Modifier = Modifier.Companion,
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
private fun Title(
    wasteType: String,
    volume: String,
    modifier: Modifier = Modifier.Companion
) {
    Text(
        text = "$wasteType · $volume",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun JobStatusAndDateRow(
    jobStatus: String,
    date: String,
    time: String,
    modifier: Modifier = Modifier.Companion
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
private fun Map(modifier: Modifier = Modifier.Companion) {
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
private fun Job(
    description: String,
    volume: String,
    bag: String,
    modifier: Modifier = Modifier.Companion
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
private fun Collection(
    address: String,
    access: String,
    instructions: String,
    modifier: Modifier = Modifier.Companion
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
private fun Contractor(
    contractorName: String,
    contractorInitial: String,
    contractorRating: String,
    contractorCompanyName: String,
    modifier: Modifier = Modifier.Companion
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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
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
private fun Payment(
    totalPrice: String,
    paymentStatus: String,
    paymentMethod: String,
    modifier: Modifier = Modifier.Companion
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
private fun CardRows(
    parameterHeaderOne: String,
    parameterNameOne: String,
    parameterHeaderTwo: String,
    parameterNameTwo: String,
    parameterHeaderThree: String,
    parameterNameThree: String,
    modifier: Modifier = Modifier.Companion
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
private fun ButtonsRow(modifier: Modifier = Modifier.Companion) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {},
            modifier = Modifier.weight(1f).height(40.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
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
            shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
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
            shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
        ) {
            Text(
                text = stringResource(Res.string.get_help),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview
@Composable
private fun BookingDetailsPreview() {
    ClearawayTheme {
        BookingDetailsContent(
            state = BookingDetailsUiState(
                isLoading = false,
                title = "Construction waste", volume = "Half van", status = "En route",
                date = "Thu 8 Oct", time = "9:00 AM", description = "Old kitchen units and rubble",
                bag = "Medium bag", address = "12 Elm Road, M1 4BT", price = "£145",
            ),
            onBack = {},
        )
    }
}

@Preview
@Composable
private fun BookingDetailsNotFoundPreview() {
    ClearawayTheme {
        BookingDetailsContent(BookingDetailsUiState(isLoading = false, error = "Booking not found"), onBack = {})
    }
}