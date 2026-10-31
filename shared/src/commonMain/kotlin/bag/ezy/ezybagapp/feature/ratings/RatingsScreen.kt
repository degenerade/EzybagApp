package bag.ezy.ezybagapp.feature.ratings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import bag.ezy.ezybagapp.ui.theme.EzybagTheme

private val RatingAccentGreen = Color(0xFF1E6F5C)
private val RatingIssueColor = Color(0xFFC4472F)

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
        onReportIssue = onReportIssue
    )
}

@Composable
fun RatingsContent(
    state: RatingsUiState,
    onRatingSelected: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onReportIssue: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(24.dp)
    val fieldShape = RoundedCornerShape(12.dp)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colors.surface, cardShape)
                    .border(
                        BorderStroke(1.dp, colors.outline),
                        cardShape
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Rate your contractor",
                    color = colors.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "How was the service?",
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp
                )

                // Completed job information.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.background, fieldShape)
                        .border(
                            BorderStroke(1.dp, colors.outline),
                            fieldShape
                        )
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "COMPLETED JOB",
                        color = RatingAccentGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Your contractor",
                        color = colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Share your experience with this contractor.",
                        color = colors.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                // Five selectable rating stars.
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Your rating",
                        color = colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            val starNumber = index + 1
                            val filled = starNumber <= state.rating

                            Text(
                                text = if (filled) "★" else "☆",
                                color = if (filled) {
                                    Color(0xFFE8A33D)
                                } else {
                                    colors.onSurfaceVariant
                                },
                                fontSize = 38.sp,
                                modifier = Modifier
                                    .clickable(
                                        enabled = !state.submitted
                                    ) {
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
                        color = colors.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }

                // Review comment input.
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Write a review",
                        color = colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    BasicTextField(
                        value = state.comment,
                        onValueChange = onCommentChange,
                        enabled = !state.submitted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.background, fieldShape)
                            .border(
                                BorderStroke(1.dp, colors.outline),
                                fieldShape
                            )
                            .padding(14.dp),
                        singleLine = false,
                        maxLines = 5,
                        textStyle = TextStyle(
                            color = colors.onSurface,
                            fontSize = 14.sp
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (state.comment.isEmpty()) {
                                    Text(
                                        text = "Describe your experience...",
                                        color = colors.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                }

                                innerTextField()
                            }
                        }
                    )
                }

                // Success, validation, or error message.
                state.message?.let { message ->
                    Text(
                        text = message,
                        color = if (state.isError) {
                            RatingIssueColor
                        } else {
                            RatingAccentGreen
                        },
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
                            text = if (state.submitted) {
                                "Review submitted"
                            } else {
                                "Submit review"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Report issue button.
                OutlinedButton(
                    onClick = onReportIssue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, RatingIssueColor),
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

// Preview: empty review.
@Preview
@Composable
private fun RatingsEmptyPreview() {
    EzybagTheme {
        RatingsContent(
            state = RatingsUiState(),
            onRatingSelected = {},
            onCommentChange = {},
            onSubmit = {},
            onReportIssue = {}
        )
    }
}

// Preview: validation error.
@Preview
@Composable
private fun RatingsErrorPreview() {
    EzybagTheme {
        RatingsContent(
            state = RatingsUiState(
                rating = 4,
                message = "Please write a review comment.",
                isError = true
            ),
            onRatingSelected = {},
            onCommentChange = {},
            onSubmit = {},
            onReportIssue = {}
        )
    }
}

// Preview: successfully submitted review.
@Preview
@Composable
private fun RatingsSubmittedPreview() {
    EzybagTheme {
        RatingsContent(
            state = RatingsUiState(
                rating = 5,
                comment = "Quick, tidy and on time.",
                submitted = true,
                message = "Thanks for your review!"
            ),
            onRatingSelected = {},
            onCommentChange = {},
            onSubmit = {},
            onReportIssue = {}
        )
    }
}
