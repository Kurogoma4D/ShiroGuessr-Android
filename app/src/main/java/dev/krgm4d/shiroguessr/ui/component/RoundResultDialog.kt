package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.DmSerifDisplayFontFamily
import dev.krgm4d.shiroguessr.ui.theme.JetBrainsMonoFontFamily
import dev.krgm4d.shiroguessr.ui.theme.SampleBorder
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.ui.theme.TextMuted
import kotlinx.coroutines.launch

/**
 * Bottom sheet displaying the result of a single round.
 *
 * Corresponds to the iOS version's `RoundResultDialog.swift`.
 * Shows a comparison between the target color and the selected color using
 * large circles, Manhattan distance in gold, a circular score progress ring,
 * and a 1-5 star rating.
 *
 * @param round The completed game round with results
 * @param onNext Callback to proceed to the next round
 * @param onDismiss Callback when the sheet is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundResultDialog(
    round: GameRound,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.4f),
    ) {
        RoundResultContent(
            round = round,
            onNext = {
                scope.launch {
                    sheetState.hide()
                    onNext()
                }
            },
        )
    }
}

/**
 * Content of the round result bottom sheet.
 *
 * Extracted as a separate composable for preview support.
 */
@Composable
private fun RoundResultContent(
    round: GameRound,
    onNext: () -> Unit,
) {
    val distance = round.distance ?: 0
    val score = round.score ?: 0
    val starCount = calculateStarRating(distance)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .navigationBarsPadding(),
    ) {
        // Header
        Text(
            text = stringResource(R.string.round_result_title, round.roundNumber),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Color comparison: large circles with "vs" text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Target color circle
            ColorCircleItem(
                label = stringResource(R.string.round_result_target),
                color = round.targetColor,
            )

            // "vs" text between circles
            Text(
                text = stringResource(R.string.round_result_vs),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            // Selected color circle
            if (round.selectedColor != null) {
                ColorCircleItem(
                    label = stringResource(R.string.round_result_your_guess),
                    color = round.selectedColor,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Distance display: large gold number
        Text(
            text = stringResource(R.string.round_result_distance),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$distance",
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = DmSerifDisplayFontFamily,
                fontSize = 56.sp,
            ),
            color = AccentPrimary,
            fontWeight = FontWeight.Normal,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Score progress ring
        ScoreProgressRing(
            score = score,
            maxScore = 1000,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Star rating
        StarRating(
            filledStars = starCount,
            totalStars = 5,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Continue button
        MdFilledButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.controls_continue))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowCircleRight,
                contentDescription = null,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * A single color item in the comparison view, displayed as a large circle
 * with label and CSS value.
 */
@Composable
private fun ColorCircleItem(
    label: String,
    color: RGBColor,
) {
    val colorDescription = stringResource(
        R.string.cd_color_sample_rgb,
        label,
        color.r,
        color.g,
        color.b,
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics {
            contentDescription = colorDescription
        },
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f),
                )
                .clip(CircleShape)
                .background(color.toComposeColor())
                .border(
                    width = 1.5.dp,
                    color = SampleBorder,
                    shape = CircleShape,
                ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = color.toCSSString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = JetBrainsMonoFontFamily,
        )
    }
}

/**
 * Circular progress ring showing the score out of [maxScore].
 *
 * The ring uses gold accent for the filled portion and a muted track color
 * for the remaining arc. The score number is displayed in the center.
 */
@Composable
private fun ScoreProgressRing(
    score: Int,
    maxScore: Int,
    modifier: Modifier = Modifier,
) {
    val progress = (score.toFloat() / maxScore).coerceIn(0f, 1f)
    val trackColor = TextMuted.copy(alpha = 0.3f)
    val progressColor = AccentPrimary
    val scoreDescription = stringResource(R.string.cd_score_ring, score, maxScore)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(120.dp)
            .semantics { contentDescription = scoreDescription }
            .drawBehind {
                val strokeWidth = 8.dp.toPx()
                val arcSize = size.minDimension - strokeWidth
                val topLeft = Offset(
                    (size.width - arcSize) / 2f,
                    (size.height - arcSize) / 2f,
                )

                // Track (background arc)
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )

                // Progress arc
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = DmSerifDisplayFontFamily,
                ),
                color = AccentPrimary,
            )
            Text(
                text = "/ $maxScore",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Star rating display with gold filled stars for earned and outlined stars for unearned.
 *
 * @param filledStars Number of filled (earned) stars
 * @param totalStars Total number of stars to display
 */
@Composable
private fun StarRating(
    filledStars: Int,
    totalStars: Int,
    modifier: Modifier = Modifier,
) {
    val ratingDescription = stringResource(
        R.string.cd_star_rating,
        filledStars,
        totalStars,
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.semantics {
            contentDescription = ratingDescription
        },
    ) {
        for (i in 1..totalStars) {
            Icon(
                imageVector = if (i <= filledStars) {
                    Icons.Filled.Star
                } else {
                    Icons.Outlined.StarOutline
                },
                contentDescription = if (i <= filledStars) {
                    stringResource(R.string.cd_star_earned)
                } else {
                    stringResource(R.string.cd_star_unearned)
                },
                tint = AccentPrimary,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
@Composable
private fun RoundResultContentPreview() {
    ShiroGuessrAndroidTheme {
        RoundResultContent(
            round = GameRound(
                roundNumber = 1,
                targetColor = RGBColor(r = 250, g = 248, b = 252),
                selectedColor = RGBColor(r = 248, g = 250, b = 250),
                distance = 6,
                score = 800,
                paletteColors = emptyList(),
            ),
            onNext = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
@Composable
private fun RoundResultContentHighScorePreview() {
    ShiroGuessrAndroidTheme {
        RoundResultContent(
            round = GameRound(
                roundNumber = 3,
                targetColor = RGBColor(r = 252, g = 250, b = 255),
                selectedColor = RGBColor(r = 252, g = 251, b = 254),
                distance = 2,
                score = 933,
                paletteColors = emptyList(),
            ),
            onNext = {},
        )
    }
}
