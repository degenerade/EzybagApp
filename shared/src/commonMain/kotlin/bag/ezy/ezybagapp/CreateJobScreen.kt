
package bag.ezy.ezybagapp

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType

private val AppBackground = Color(0xFF0D1718)
private val CardBackground = Color(0xFF192A2D)
private val FieldBackground = Color(0xFF101A1C)
private val AccentGreen = Color(0xFF39B596)
private val MainText = Color(0xFFE8F0EF)
private val MutedText = Color(0xFF94A8AC)
private val BorderColor = Color(0xFF2B4145)

@Composable
fun CreateJobScreen() {
    var address by remember { mutableStateOf("") }
    var postcode by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedWaste by remember {
        mutableStateOf("Construction")
    }

    var selectedVolume by remember {
        mutableStateOf("Half van")
    }

    var selectedFiles by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var message by remember { mutableStateOf("") }

    val wasteTypes = listOf(
        "Household",
        "Garden",
        "Construction",
        "Furniture",
        "Electrical",
        "Mixed",
        "Other"
    )

    val volumes = listOf("Small", "Half van", "Full van")

    // Opens the device's file picker for multiple photos/videos.
    val mediaPicker = rememberFilePickerLauncher(
        type = PickerType.File(
            extensions = listOf(
                "jpg", "jpeg", "png", "webp", "heic",
                "mp4", "mov", "m4v", "webm", "avi"
            )
        ),
        mode = PickerMode.Multiple(),
        title = "Choose photos or videos"
    ) { files ->
        if (files != null) {
            selectedFiles =
                (selectedFiles + files.map { it.name }).distinct()
            message = ""
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EZY BAG",
                color = AccentGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        BorderStroke(2.dp, MainText),
                        RoundedCornerShape(28.dp)
                    )
                    .background(
                        CardBackground,
                        RoundedCornerShape(28.dp)
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "New booking",
                    color = MainText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                BookingField(
                    label = "Collection address",
                    placeholder = "Enter collection address",
                    value = address,
                    onValueChange = { address = it }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BookingField(
                            label = "Postcode",
                            placeholder = "Enter postcode",
                            value = postcode,
                            onValueChange = { postcode = it }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        BookingField(
                            label = "Phone",
                            placeholder = "Phone number",
                            value = phone,
                            onValueChange = { phone = it }
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Waste type",
                        color = MutedText,
                        fontSize = 13.sp
                    )

                    wasteTypes.chunked(3).forEach { rowTypes ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowTypes.forEach { type ->
                                SelectionChip(
                                    text = type,
                                    selected = selectedWaste == type,
                                    onClick = { selectedWaste = type }
                                )
                            }
                        }
                    }
                }

                BookingField(
                    label = "Description",
                    placeholder = "Describe the waste",
                    value = description,
                    onValueChange = { description = it },
                    singleLine = false
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Estimated volume",
                        color = MutedText,
                        fontSize = 13.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        volumes.forEach { volume ->
                            SelectionChip(
                                text = volume,
                                selected = selectedVolume == volume,
                                onClick = { selectedVolume = volume }
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { mediaPicker.launch() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MainText
                        )
                    ) {
                        Text("+ Add photos / videos")
                    }

                    selectedFiles.forEach { fileName ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fileName,
                                modifier = Modifier.weight(1f),
                                color = MutedText,
                                fontSize = 12.sp
                            )

                            TextButton(
                                onClick = {
                                    selectedFiles =
                                        selectedFiles - fileName
                                }
                            ) {
                                Text(
                                    text = "Remove",
                                    color = AccentGreen
                                )
                            }
                        }
                    }
                }

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = AccentGreen,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        message = when {
                            address.isBlank() ->
                                "Please enter the collection address."

                            postcode.isBlank() ->
                                "Please enter the postcode."

                            phone.isBlank() ->
                                "Please enter your phone number."

                            else ->
                                "Your job details are ready for the next step."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Continue",
                        fontWeight = FontWeight.Bold
                    )
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
            color = MutedText,
            fontSize = 12.sp
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    FieldBackground,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 4,
            textStyle = TextStyle(
                color = MainText,
                fontSize = 14.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MutedText,
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
            if (selected) AccentGreen else BorderColor
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) {
                AccentGreen.copy(alpha = 0.12f)
            } else {
                Color.Transparent
            },
            contentColor = if (selected) AccentGreen else MainText
        )
    ) {
        Text(
            text = text,
            fontSize = 12.sp
        )
    }
}
