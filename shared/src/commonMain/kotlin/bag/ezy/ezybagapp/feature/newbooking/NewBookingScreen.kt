package bag.ezy.ezybagapp.feature.newbooking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType

private val wasteTypes = listOf("Household", "garden", "Construction", "Furniture", "Electrical", "Mixed", "Other")
private val volumes = listOf("Small", "Half van", "Full van")

@Composable
fun NewBookingDetailsScreen(
    vm: NewBookingViewModel,
    onContinue: () -> Unit
) {
    val mediaPicker = rememberFilePickerLauncher(
        type = PickerType.File(
            extensions = listOf("jpg", "jpeg", "png", "webp", "heic", "mp4", "mov", "m4v", "webm", "avi")
        ),
        mode = PickerMode.Multiple(),
        title = "Choose photos or videos"
    ) { files ->
        if (files != null) vm.onFilesPicked(files.map { it.name })
        // todo: keep actual files (not just names) when uploading to firebase
    }
    NewBookingDetailsContent(
        state = vm.state,
        onAddressChange = vm::onAddressChange,
        onPostcodeChange = vm::onPostcodeChange,
        onPhoneChange = vm::onPhoneChange,
        onWasteTypeSelected = vm::onWasteTypeSelected,
        onDescriptionChange = vm::onDescriptionChange,
        onVolumeSelected = vm::onVolumeSelected,
        onAddMediaClick = { mediaPicker.launch() },
        onFileRemoved = vm::onFileRemoved,
        onContinue = { vm.continueFromDetails(onValid = onContinue) },
    )
}

@Composable
fun NewBookingDetailsContent(
    state: NewBookingState,
    onAddressChange: (String) -> Unit,
    onPostcodeChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onWasteTypeSelected: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onVolumeSelected: (String) -> Unit,
    onAddMediaClick: () -> Unit,
    onFileRemoved: (String) -> Unit,
    onContinue: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EZY BAG",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.outline),
                        RoundedCornerShape(28.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.surface,
                        androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "New booking",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                BookingField(
                    label = "Collection address",
                    placeholder = "Enter collection address",
                    value = state.address,
                    onValueChange = onAddressChange
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BookingField(
                            label = "Postcode",
                            placeholder = "Enter postcode",
                            value = state.postcode,
                            onValueChange = onPostcodeChange
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        BookingField(
                            label = "Phone",
                            placeholder = "Phone number",
                            value = state.phone,
                            onValueChange = onPhoneChange
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Waste type",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    wasteTypes.chunked(3).forEach { rowTypes ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowTypes.forEach { type ->
                                SelectionChip(
                                    text = type,
                                    selected = state.wasteType == type,
                                    onClick = { onWasteTypeSelected(type) }
                                )
                            }
                        }
                    }
                }

                BookingField(
                    label = "Description",
                    placeholder = "Describe the waste",
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    singleLine = false
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Estimated volume",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        volumes.forEach { volume ->
                            SelectionChip(
                                text = volume,
                                selected = state.volume == volume,
                                onClick = { onVolumeSelected(volume) }
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onAddMediaClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("+ Add photos / videos")
                    }

                    state.mediaFileNames.forEach { fileName ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fileName,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )

                            TextButton(onClick = { onFileRemoved(fileName) }) {
                                Text(
                                    text = "Remove",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                state.error?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Continue", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BookingField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 4,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun SelectionChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
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
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                Color.Transparent
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
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
private fun NewBookingDetailsEmptyPreview() {
    ClearawayTheme {
        NewBookingDetailsContent(
            NewBookingState(),
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {})
    }
}

@Preview
@Composable
private fun NewBookingDetailsFilledPreview() {
    ClearawayTheme {
        NewBookingDetailsContent(
            NewBookingState(
                address = "12 Elm Road", postcode = "M1 4BT", wasteType = "Furniture",
                description = "Old sofa and two chairs", volume = "Small",
                mediaFileNames = listOf("sofa.jpg", "chairs.jpg"),
                error = "Please enter your phone number."
            ),
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}