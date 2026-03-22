package dev.krgm4d.shiroguessr.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.GameRound
import dev.krgm4d.shiroguessr.model.GameState
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.component.MdFilledButton
import dev.krgm4d.shiroguessr.ui.component.MdOutlinedButton
import dev.krgm4d.shiroguessr.ui.component.calculateStarRating
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.ScoreHigh
import dev.krgm4d.shiroguessr.ui.theme.ScoreLow
import dev.krgm4d.shiroguessr.ui.theme.ScoreMid
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.viewmodel.ResultViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.sin
import kotlin.random.Random

/** Count-up animation duration in milliseconds. */
private const val SCORE_COUNT_UP_DURATION_MS = 1500

/** Maximum possible total score across all rounds. */
private const val MAX_TOTAL_SCORE = 5000

/** Score threshold for large burst effect. */
private const val BURST_LARGE_THRESHOLD = 4000

/** Score threshold for small burst effect. */
private const val BURST_SMALL_THRESHOLD = 3000

/** Stagger delay between round rows in milliseconds. */
private const val ROUND_STAGGER_DELAY_MS = 100L

/**
 * Result screen displayed after all game rounds are completed.
 *
 * Shows a trophy icon, animated total score counter with color gradient,
 * gold particle effects for high scores, round-by-round results with
 * star ratings and staggered entrance animation, and action buttons.
 *
 * Corresponds to the iOS version's `ResultScreen.swift`.
 *
 * @param onPlayAgain Callback invoked when the player taps "Play Again"
 * @param onShareResults Callback for sharing results
 * @param onCopyToClipboard Callback for copying results to clipboard
 * @param modifier Optional modifier for the root layout
 * @param resultViewModel ViewModel holding the completed game state
 */
@Composable
fun ResultScreen(
    onPlayAgain: () -> Unit,
    onShareResults: () -> Unit = {},
    onCopyToClipboard: () -> Unit = {},
    modifier: Modifier = Modifier,
    resultViewModel: ResultViewModel,
) {
    val gameState by resultViewModel.gameState.collectAsState()

    if (gameState == null) {
        // Fallback if no game state is available
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(R.string.game_no_results),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        return
    }

    ResultScreenContent(
        gameState = gameState!!,
        onPlayAgain = onPlayAgain,
        onShareResults = onShareResults,
        onCopyToClipboard = onCopyToClipboard,
        modifier = modifier,
    )
}

/**
 * Content of the result screen displaying game results.
 *
 * Features:
 * - Gold particle background for high scores (4000+)
 * - Score count-up with gradient color change (low -> mid -> high)
 * - Scale burst on count-up completion (score-dependent)
 * - Staggered fade-in for round result rows
 * - Star rating per round row
 * - Enlarged color swatches
 * - "Play Again" as gold filled CTA, "Share"/"Copy" as outlined side by side
 *
 * @param gameState The completed game state
 * @param onPlayAgain Callback for replay
 * @param onShareResults Callback for sharing
 * @param onCopyToClipboard Callback for copying
 * @param modifier Optional modifier
 */
@Composable
private fun ResultScreenContent(
    gameState: GameState,
    onPlayAgain: () -> Unit,
    onShareResults: () -> Unit,
    onCopyToClipboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Trigger the count-up animation
    var animateScore by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateScore = true
    }

    val animatedScore by animateIntAsState(
        targetValue = if (animateScore) gameState.totalScore else 0,
        animationSpec = tween(durationMillis = SCORE_COUNT_UP_DURATION_MS),
        label = "scoreCountUp",
    )

    // Score color gradient: transitions based on current animated value
    val scoreProgress = if (MAX_TOTAL_SCORE > 0) {
        (animatedScore.toFloat() / MAX_TOTAL_SCORE).coerceIn(0f, 1f)
    } else {
        0f
    }
    val scoreColor = scoreGradientColor(scoreProgress)

    // Burst animation on count-up completion
    val burstScale = remember { Animatable(1f) }
    var hasTriggeredBurst by remember { mutableStateOf(false) }

    LaunchedEffect(animatedScore) {
        if (animatedScore == gameState.totalScore && !hasTriggeredBurst && animateScore) {
            hasTriggeredBurst = true
            val targetScale = when {
                gameState.totalScore >= BURST_LARGE_THRESHOLD -> 1.25f
                gameState.totalScore >= BURST_SMALL_THRESHOLD -> 1.12f
                else -> 1f
            }
            if (targetScale > 1f) {
                burstScale.animateTo(
                    targetValue = targetScale,
                    animationSpec = spring(
                        dampingRatio = 0.4f,
                        stiffness = 300f,
                    ),
                )
                burstScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = 0.7f,
                        stiffness = 300f,
                    ),
                )
            }
        }
    }

    // Show particles only for high scores
    val showParticles = gameState.totalScore >= BURST_LARGE_THRESHOLD

    Box(modifier = modifier.fillMaxSize()) {
        // Gold particle background for high scores
        if (showParticles) {
            GoldParticleEffect(
                modifier = Modifier.fillMaxSize(),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Trophy icon
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = AccentPrimary,
                modifier = Modifier.size(80.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = stringResource(R.string.game_complete),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Total score label
            Text(
                text = stringResource(R.string.result_total_score),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Animated total score with gradient color and burst scale
            Text(
                text = "$animatedScore",
                style = MaterialTheme.typography.displayLarge,
                color = scoreColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(burstScale.value),
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Round results header
            Text(
                text = stringResource(R.string.result_round_results),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Round-by-round results with staggered fade-in
            gameState.rounds.forEachIndexed { index, round ->
                StaggeredRoundResultRow(
                    round = round,
                    delayMs = index * ROUND_STAGGER_DELAY_MS,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons: Play Again as primary CTA
            MdFilledButton(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.result_play_again))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Share and Copy as outlined buttons side by side
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                MdOutlinedButton(
                    onClick = onShareResults,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.result_share))
                }

                MdOutlinedButton(
                    onClick = onCopyToClipboard,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.result_copy))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Computes a gradient color for the score display based on progress (0..1).
 *
 * The gradient transitions:
 * - 0.0 .. 0.4: ScoreLow (red) -> ScoreMid (gold)
 * - 0.4 .. 0.8: ScoreMid (gold) -> ScoreHigh (green)
 * - 0.8 .. 1.0: ScoreHigh (green), stays
 *
 * @param progress A float in [0, 1] representing how far through the score range
 * @return The interpolated [Color]
 */
private fun scoreGradientColor(progress: Float): Color {
    return when {
        progress < 0.4f -> lerp(ScoreLow, ScoreMid, progress / 0.4f)
        progress < 0.8f -> lerp(ScoreMid, ScoreHigh, (progress - 0.4f) / 0.4f)
        else -> ScoreHigh
    }
}

/**
 * A round result row that fades in with a staggered delay.
 *
 * Each row animates from fully transparent to fully opaque after a delay
 * proportional to its index, creating a cascading entrance effect.
 *
 * @param round The game round data
 * @param delayMs Delay before the fade-in starts
 */
@Composable
private fun StaggeredRoundResultRow(
    round: GameRound,
    delayMs: Long,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs)
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "roundFadeIn",
    )

    Box(modifier = Modifier.alpha(alpha)) {
        RoundResultRow(round = round)
    }
}

/**
 * A single row displaying the result for one round.
 *
 * Shows the round number, target color swatch, selected color swatch,
 * star rating, Manhattan distance, and the score earned.
 *
 * @param round The game round with results
 */
@Composable
private fun RoundResultRow(round: GameRound) {
    val starCount = calculateStarRating(round.distance ?: Int.MAX_VALUE)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
    ) {
        // Round number
        Text(
            text = "R${round.roundNumber}",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(32.dp),
        )

        // Target color swatch (enlarged)
        ColorSwatch(
            color = round.targetColor,
            label = stringResource(R.string.round_result_target),
        )

        // Arrow and selected color swatch
        if (round.selectedColor != null) {
            Text(
                text = "\u2192",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp),
            )
            ColorSwatch(
                color = round.selectedColor,
                label = stringResource(R.string.round_result_your_guess),
            )
        }

        // Small star rating next to swatches (hidden for unanswered rounds)
        if (round.distance != null) {
            Spacer(modifier = Modifier.width(8.dp))
            MiniStarRating(filledStars = starCount)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Distance and score
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = if (round.distance != null) "d=${round.distance}" else "\u2014",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = if (round.score != null) {
                    stringResource(R.string.round_result_pts, round.score)
                } else {
                    "\u2014"
                },
                style = MaterialTheme.typography.titleSmall,
                color = AccentPrimary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/**
 * A small color swatch rectangle for displaying RGB colors.
 * Slightly enlarged compared to the original for better visibility.
 *
 * @param color The RGB color to display
 * @param label Content description for accessibility
 */
@Composable
private fun ColorSwatch(
    color: RGBColor,
    label: String,
) {
    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color.toComposeColor())
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(6.dp),
            ),
    )
}

/**
 * Compact star rating display for use inside round result rows.
 *
 * Shows 1-5 small gold stars (filled for earned, outlined for unearned).
 *
 * @param filledStars Number of filled (earned) stars
 */
@Composable
private fun MiniStarRating(
    filledStars: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        modifier = modifier,
    ) {
        for (i in 1..5) {
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
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Gold Particle Effect
// ---------------------------------------------------------------------------

/**
 * Data class representing a single floating gold particle.
 *
 * @property x Horizontal position (0..1 normalized)
 * @property y Vertical position (0..1 normalized)
 * @property radius Particle radius in pixels
 * @property alpha Opacity (0..1)
 * @property speedY Vertical floating speed (negative = upward)
 * @property drift Horizontal drift speed
 * @property phase Phase offset for sinusoidal horizontal drift
 */
private class GoldParticle(
    var x: Float,
    var y: Float,
    val radius: Float,
    var alpha: Float,
    val speedY: Float,
    val drift: Float,
    val phase: Float,
)

/**
 * Canvas-based particle effect that draws small floating gold light particles.
 *
 * Particles drift upward with gentle sinusoidal horizontal movement.
 * Each particle has a randomized size, speed, and opacity for a natural look.
 * Only shown for high-score results (4000+ total score).
 */
@Composable
private fun GoldParticleEffect(
    modifier: Modifier = Modifier,
) {
    val particleCount = 30
    val particles = remember {
        List(particleCount) { createRandomParticle() }
    }
    var tick by remember { mutableStateOf(0L) }

    // Particles are intentionally mutated in-place for performance: allocating new
    // particle lists every frame would create GC pressure. The `tick++` state change
    // is sufficient to trigger Canvas recomposition, which then reads the updated
    // particle positions. This is safe because the particle list is only read during
    // the Canvas draw phase, which always runs after this coroutine yields via delay.
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(16L) // ~60 fps
            tick++
            particles.forEach { particle ->
                particle.y += particle.speedY
                // Reset particle to bottom when it drifts above the top
                if (particle.y < -0.05f) {
                    particle.y = 1.05f
                    particle.x = Random.nextFloat()
                    particle.alpha = Random.nextFloat() * 0.4f + 0.1f
                }
            }
        }
    }

    val goldColor = AccentPrimary

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val time = tick * 0.02f

        particles.forEach { particle ->
            val px = particle.x * w + sin(time + particle.phase) * particle.drift * w * 0.05f
            val py = particle.y * h
            drawCircle(
                color = goldColor.copy(alpha = particle.alpha),
                radius = particle.radius,
                center = Offset(px, py),
            )
        }
    }
}

/**
 * Creates a single particle with randomized properties.
 */
private fun createRandomParticle(): GoldParticle {
    return GoldParticle(
        x = Random.nextFloat(),
        y = Random.nextFloat(),
        radius = Random.nextFloat() * 3f + 1.5f,
        alpha = Random.nextFloat() * 0.4f + 0.1f,
        speedY = -(Random.nextFloat() * 0.001f + 0.0005f),
        drift = Random.nextFloat() * 2f + 0.5f,
        phase = Random.nextFloat() * 6.28f,
    )
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
@Composable
private fun ResultScreenContentPreview() {
    val sampleState = GameState(
        rounds = (1..5).map { roundNumber ->
            GameRound(
                roundNumber = roundNumber,
                targetColor = RGBColor(250, 248, 252),
                selectedColor = RGBColor(248, 250, 250),
                distance = roundNumber * 2,
                score = 1000 - roundNumber * 67,
            )
        },
        currentRoundIndex = 5,
        isCompleted = true,
        totalScore = 4335,
    )

    ShiroGuessrAndroidTheme {
        ResultScreenContent(
            gameState = sampleState,
            onPlayAgain = {},
            onShareResults = {},
            onCopyToClipboard = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
@Composable
private fun ResultScreenContentLowScorePreview() {
    val sampleState = GameState(
        rounds = (1..5).map { roundNumber ->
            GameRound(
                roundNumber = roundNumber,
                targetColor = RGBColor(250, 248, 252),
                selectedColor = RGBColor(230, 240, 235),
                distance = 30 + roundNumber * 5,
                score = 500 - roundNumber * 50,
            )
        },
        currentRoundIndex = 5,
        isCompleted = true,
        totalScore = 1250,
    )

    ShiroGuessrAndroidTheme {
        ResultScreenContent(
            gameState = sampleState,
            onPlayAgain = {},
            onShareResults = {},
            onCopyToClipboard = {},
        )
    }
}
