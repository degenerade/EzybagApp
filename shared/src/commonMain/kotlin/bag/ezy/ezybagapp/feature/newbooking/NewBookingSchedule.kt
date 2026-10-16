package bag.ezy.ezybagapp.feature.newbooking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBagQuoteScreen(
    vm: NewBookingViewModel,
    onBack: () -> Unit,
    onBooked: (bookingId: String) -> Unit
) {
    NewBookingScheduleContent(
        state = vm.state,
        onBack = onBack,
        onDateSelected = vm::onDateSelected,
        onTimeSelected = vm::onTimeSelected,
        onAccessInfoChange = vm::onAccessInfoChange,
        onInstructionsChange = vm::onInstructionChange,
        onNeedsBagChange = vm::onNeedsBagChange,
        onBagSizeChange = vm::onBagSizeChange,
        onRequestQuote = vm::requestQuote,
        onPay = { vm.pay(onBooked) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBookingScheduleContent(
    state: NewBookingState,
    onBack: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onAccessInfoChange: (String) -> Unit,
    onInstructionsChange: (String) -> Unit,
    onNeedsBagChange: (Boolean) -> Unit,
    onBagSizeChange: (String) -> Unit,
    onRequestQuote: () -> Unit,
    onPay: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.dateMillis)
    val timePickerState = rememberTimePickerState(
        initialHour = state.hour,
        initialMinute = state.minute,
        is24Hour = false
    )

    val selectedDateText = state.dateMillis?.let { formatBookingDate(it) } ?: "Choose date"
    val selectedTimeText = formatBookingTime(state.hour, state.minute)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                        RoundedCornerShape(28.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.surface,
                        androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
                    )
                    .padding(18.dp)
            ) {
                TextButton(
                    onClick = onBack
                ) {
                    Text(
                        text = "← Back",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "New booking",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable booking details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date and time pickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            PickerField(
                                label = "Date",
                                value = selectedDateText,
                                onClick = { showDatePicker = true }
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            PickerField(
                                label = "Time",
                                value = selectedTimeText,
                                onClick = { showTimePicker = true }
                            )
                        }
                    }

                    // Access information
                    ScheduleTextField(
                        label = "Access information",
                        value = state.accessInfo,
                        onValueChange = onAccessInfoChange,
                        placeholder = "Gate, door code, or access details"
                    )

                    // Special instructions
                    ScheduleTextField(
                        label = "Special instructions",
                        value = state.specialInstructions,
                        onValueChange = onInstructionsChange,
                        placeholder = "Instructions for the contractor",
                        singleLine = false
                    )

                    // Bag selection
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                androidx.compose.foundation.shape.RoundedCornerShape(22.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Need a bag?",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BagOption(
                                    text = "Yes",
                                    selected = state.needsBag,
                                    onClick = { onNeedsBagChange(true) }
                                )

                                BagOption(
                                    text = "No",
                                    selected = !state.needsBag,
                                    onClick = { onNeedsBagChange(false) }
                                )
                            }
                        }

                        if (state.needsBag) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Small", "Medium", "Large").forEach { size ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        BagOption(
                                            text = size,
                                            selected = state.bagSize == size,
                                            onClick = { onBagSizeChange(size) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Instant estimate
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.5.dp,
                                MaterialTheme.colorScheme.primary,
                                androidx.compose.foundation.shape.RoundedCornerShape(22.dp)
                            )
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                androidx.compose.foundation.shape.RoundedCornerShape(22.dp)
                            )
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Instant estimate",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )

                        Text(
                            text = state.estimate?.let { "£${it.toInt()}" } ?: "-",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "${state.volume} · ${state.wasteType.lowercase()} waste",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )

                        if (state.needsBag) {
                            Text(
                                text = "${state.bagSize} bag selected",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Request final quote
                    OutlinedButton(
                        onClick = onRequestQuote,
                        enabled = !state.quoteRequested,
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = if (state.quoteRequested) "Final quote requested" else "Request a final quote",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                state.error?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pay button stays at the bottom
                Button(
                    onClick = onPay,
                    enabled = !state.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Pay securely",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Calendar dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Clock dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select collection time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false }
                ) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun PickerField(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ScheduleTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Box {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = singleLine,
                maxLines = if (singleLine) 1 else 4,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
private fun BagOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(42.dp),
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 4.dp
        ),
        shape = CircleShape,
        border = BorderStroke(
            1.dp,
            if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )
    }
}

private fun formatBookingDate(epochMillis: Long): String {
    val date = Instant
        .fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.UTC)
        .date

    val days = listOf(
        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    )

    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    return "${days[date.dayOfWeek.ordinal]} " +
            "${date.day} ${months[date.month.ordinal]}"
}

private fun formatBookingTime(hour: Int, minute: Int): String {
    val displayHour = when (hour % 12) {
        0 -> 12
        else -> hour % 12
    }

    val displayMinute = minute.toString().padStart(2, '0')
    val period = if (hour < 12) "AM" else "PM"

    return "$displayHour:$displayMinute $period"
}

@Preview
@Composable
private fun NewBookingScheduleEmptyPreview() {
    ClearawayTheme {
        NewBookingScheduleContent(
            NewBookingState(estimate = 145.0),
            {},
            {},
            { _, _ -> },
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}

@Preview
@Composable
private fun NewBookingScheduleFilledPreview() {
    ClearawayTheme {
        NewBookingScheduleContent(
            NewBookingState(
                wasteType = "Furniture", volume = "Small", estimate = 70.0,
                dateMillis = 1792627200000, hour = 14, minute = 30,
                needsBag = false, quoteRequested = true,
                error = "Please choose a collection date."
            ),
            {},
            {},
            { _, _ -> },
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}