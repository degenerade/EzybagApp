
package bag.ezy.ezybagapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

private val RatingAppBackground = Color(0xFF0D1718)
private val RatingCardBackground = Color(0xFF192A2D)
private val RatingFieldBackground = Color(0xFF101A1C)
private val RatingAccentGreen = Color(0xFF39B596)
private val RatingMainText = Color(0xFFE8F0EF)
private val RatingMutedText = Color(0xFF94A8AC)
private val RatingBorderColor = Color(0xFF2B4145)
private val RatingIssueColor = Color(0xFFE58A79)

@Composable
fun RatingsScreen() {

    var selectedRating by remember { mutableStateOf(0) }

    var reviewComment by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = RatingAppBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "EZYBAG",
                color = RatingAccentGreen,
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
                        BorderStroke(2.dp, RatingMainText),
                        RoundedCornerShape(28.dp)
                    )
                    .background(
                        RatingCardBackground,
                        RoundedCornerShape(28.dp)
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Rate your contractor",
                    color = RatingMainText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "How was your waste removal service?",
                    color = RatingMutedText,
                    fontSize = 14.sp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            RatingFieldBackground,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Completed job",
                        color = RatingAccentGreen,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "Your contractor",
                        color = RatingMainText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Share your experience with this contractor.",
                        color = RatingMutedText,
                        fontSize = 12.sp
                    )
                }

                // Star rating section.
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Your rating",
                        color = RatingMutedText,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Creates five clickable stars.
                        repeat(5) { index ->

                            val starNumber = index + 1

                            Text(
                                text = if (
                                    starNumber <= selectedRating
                                ) {
                                    "★"
                                } else {
                                    "☆"
                                },
                                color = if (
                                    starNumber <= selectedRating
                                ) {
                                    RatingAccentGreen
                                } else {
                                    RatingMutedText
                                },
                                fontSize = 38.sp,
                                modifier = Modifier
                                    .clickable {
                                        selectedRating = starNumber
                                        message = ""
                                    }
                                    .padding(horizontal = 5.dp)
                            )
                        }
                    }

                    Text(
                        text = when (selectedRating) {
                            1 -> "1 star - Poor"
                            2 -> "2 stars - Fair"
                            3 -> "3 stars - Good"
                            4 -> "4 stars - Very good"
                            5 -> "5 stars - Excellent"
                            else -> "Tap a star to rate the service"
                        },
                        color = RatingMutedText,
                        fontSize = 13.sp
                    )
                }

                // Review comment section.
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Write a review",
                        color = RatingMutedText,
                        fontSize = 13.sp
                    )

                    BasicTextField(
                        value = reviewComment,
                        onValueChange = {
                            reviewComment = it
                            message = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                RatingFieldBackground,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        singleLine = false,
                        maxLines = 5,
                        textStyle = TextStyle(
                            color = RatingMainText,
                            fontSize = 14.sp
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (reviewComment.isEmpty()) {
                                    Text(
                                        text = "Describe your experience...",
                                        color = RatingMutedText,
                                        fontSize = 13.sp
                                    )
                                }

                                innerTextField()
                            }
                        }
                    )
                }

                // Feedback message.
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = RatingAccentGreen,
                        fontSize = 13.sp
                    )
                }

                // Submit review button.
                Button(
                    onClick = {
                        message = when {
                            selectedRating == 0 ->
                                "Please select a star rating."

                            reviewComment.isBlank() ->
                                "Please write a review comment."

                            else ->
                                "Review ready to submit!"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RatingAccentGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Submit review",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        message =
                            "Issue reporting selected. Connect this to the report form."
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        RatingIssueColor
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = RatingIssueColor
                    )
                ) {
                    Text(
                        text = "Report an issue",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}