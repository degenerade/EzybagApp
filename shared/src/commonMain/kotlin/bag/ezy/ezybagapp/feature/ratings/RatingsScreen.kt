package bag.ezy.ezybagapp.feature.ratings

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bag.ezy.ezybagapp.di.AppContainer

private val RatingAppBackground = Color(0xFF0D1718)
private val RatingCardBackground = Color(0xFF192A2D)
private val RatingFieldBackground = Color(0xFF101A1C)
private val RatingAccentGreen = Color(0xFF39B596)
private val RatingMainText = Color(0xFFE8F0EF)
private val RatingMutedText = Color(0xFF94A8AC)
private val RatingBorderColor = Color(0xFF2B4145)
private val RatingIssueColor = Color(0xFFE58A79)

@Composable
fun RatingsScreen(
    bookingId: String,
    onReportIssue: () -> Unit,
    vm: RatingsViewModel = viewModel(key = "ratings-$bookingId") {
        RatingsViewModel(bookingId, AppContainer.reviewRepository)
    }
) {
    RatingsContent(
        state = vm.state,
        onRatingSelected = vm::onRatingSelected,
        onCommentChange = vm::onCommentChange,
        onSubmit = vm::submit,
        onReportIssue = onReportIssue,
    )
}

@Composable
fun RatingsContent(
    state: RatingsUiState,
    onRatingSelected: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onReportIssue: () -> Unit,
) {
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
                        androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
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
                            val filled = starNumber <= state.rating

                            Text(
                                text = if (filled) "★" else "☆",
                                color = if (filled) RatingAccentGreen else RatingMutedText,
                                fontSize = 38.sp,
                                modifier = Modifier
                                    .clickable(enabled = !state.submitted) {
                                        onRatingSelected(starNumber)
                                    }
                                    .padding(horizontal = 5.dp)
                            )
                        }
                    }

                    Text(
                        text = when (state.rating) {
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
                        value = state.comment,
                        onValueChange = onCommentChange,
                        enabled = !state.submitted,
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
                                if (state.comment.isEmpty()) {
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
                state.message?.let { msg ->
                    Text(
                        text = msg,
                        color = if (state.isError) RatingIssueColor else RatingAccentGreen,
                        fontSize = 13.sp
                    )
                }

                // Submit review button.
                Button(
                    onClick = onSubmit,
                    enabled = !state.isSubmitting && !state.submitted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RatingAccentGreen,
                        contentColor = Color.White
                    )
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = if (state.submitted) "Review submitted" else "Submit review",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedButton(
                    onClick = onReportIssue,
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

@Preview
@Composable
private fun RatingsEmptyPreview() {
    RatingsContent(
        RatingsUiState(),
        {},
        {},
        {},
        {}
    )
}

@Preview
@Composable
private fun RatingsErrorPreview() {
    RatingsContent(
        RatingsUiState(rating = 4, message = "Please write a review comment.", isError = true),
        {},
        {},
        {},
        {}
    )
}

@Preview
@Composable
private fun RatingsSubmittedPreview() {
    RatingsContent(
        RatingsUiState(
            rating = 5,
            comment = "Quick, tidy and on time.",
            submitted = true,
            message = "Thanks for your review!"
        ),
        {},
        {},
        {},
        {}
    )
}