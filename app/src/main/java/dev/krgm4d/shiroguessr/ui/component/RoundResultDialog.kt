package dev.krgm4d.shiroguessr.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
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
 * Full-screen overlay dialog displaying the result of a single round.
 *
 * Corresponds to the iOS version's `RoundResultDialog.swift`.
 * Shows a comparison between the target color and the selected color using
 * large circles, Manhattan distance in gold, a circular score progress ring,
 * and a 1-5 star rating.
 *
 * Entrance animation: scale-up from center + fade-in using spring animation
 * with stiffness 300 and dampingRatio 0.7 per Shiro Gallery guideline.
 *
 * @param round The completed game round with results
 * @param onNext Callback to proceed to the next round
 * @param onDismiss Callback when the backdrop is tapped
 */
@Composable
fun RoundResultDialog(
    round: GameRound,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
) {
    BackHandler(onBack = onDismiss)

    // Entrance animation state
    val scaleAnim = remember { Animatable(0.8f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = 300f,
                ),
            )
        }
        launch {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300),
            )
        }
    }

    // Dark semi-transparent backdrop
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Content with scale + fade animation
        Box(
            modifier = Modifier
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* Prevent click-through to backdrop */ },
                ),
        ) {
            RoundResultContent(
                round = round,
                onNext = onNext,
            )
        }
    }
}

/**
 * Content of the round result overlay.
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
            .padding(horizontal = 32.dp),
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
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

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(120.dp)
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
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
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

/**
 * Calculates a 1-5 star rating based on Manhattan distance.
 *
 * Distance ranges (out of max 30):
 * - 0-2: 5 stars (near perfect)
 * - 3-6: 4 stars (great)
 * - 7-12: 3 stars (good)
 * - 13-20: 2 stars (fair)
 * - 21+: 1 star (needs improvement)
 */
private fun calculateStarRating(distance: Int): Int {
    return when {
        distance <= 2 -> 5
        distance <= 6 -> 4
        distance <= 12 -> 3
        distance <= 20 -> 2
        else -> 1
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
