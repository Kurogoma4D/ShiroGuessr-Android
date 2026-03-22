package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.ui.theme.JetBrainsMonoFontFamily
import dev.krgm4d.shiroguessr.ui.theme.LocalShiroGuessrColors
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Displays the remaining time in MM:SS format with enhanced visual effects.
 *
 * Corresponds to the iOS version's `TimerDisplay.swift`.
 * Shows the remaining time with visual indicators and animations:
 * - Normal: default text color, no pulse
 * - Warning (< 10s): timer warning color, pulse animation begins
 * - Critical (< 5s): timer critical color, faster pulse
 * - Timeout (0s): timer critical color, no pulse
 *
 * A linear progress bar below the timer digits shrinks in sync with the
 * remaining time to provide an at-a-glance visual indicator of elapsed time.
 *
 * @param timeRemaining Remaining time in seconds
 * @param totalTime Total time for the round in seconds (used for progress bar)
 * @param warningThreshold Threshold in seconds for warning state (default: 10)
 * @param criticalThreshold Threshold in seconds for critical state (default: 5)
 * @param modifier Optional modifier for the root layout
 */
@Composable
fun TimerDisplay(
    timeRemaining: Int,
    totalTime: Int = 60,
    warningThreshold: Int = 10,
    criticalThreshold: Int = 5,
    modifier: Modifier = Modifier,
) {
    val shiroColors = LocalShiroGuessrColors.current
    val isWarning = timeRemaining in (criticalThreshold + 1)..<warningThreshold
    val isCritical = timeRemaining in 1..criticalThreshold
    val isTimeout = timeRemaining == 0
    val isPulsing = timeRemaining in 1..<warningThreshold

    val timerColor by animateColorAsState(
        targetValue = when {
            isTimeout -> shiroColors.timerCritical
            isCritical -> shiroColors.timerCritical
            isWarning -> shiroColors.timerWarning
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 300),
        label = "timerColor",
    )

    // Pulse animation: scale oscillation for warning/critical states
    val pulseScale = if (isPulsing) {
        val pulseDuration = if (isCritical) 400 else 700
        val infiniteTransition = rememberInfiniteTransition(label = "timerPulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = pulseDuration,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "pulseScale",
        )
        scale
    } else {
        1f
    }

    // Progress bar fraction
    val progress = if (totalTime > 0) {
        timeRemaining.toFloat() / totalTime.toFloat()
    } else {
        0f
    }

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = timerColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                )
                .border(
                    width = 1.dp,
                    color = timerColor.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = timerColor,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = formattedTime,
                style = MaterialTheme.typography.headlineSmall,
                color = timerColor,
                fontFamily = JetBrainsMonoFontFamily,
                modifier = Modifier.scale(pulseScale),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Thin linear progress bar synced with remaining time
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp)),
            color = timerColor,
            trackColor = timerColor.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerDisplayNormalPreview() {
    ShiroGuessrAndroidTheme {
        Column {
            TimerDisplay(timeRemaining = 45, totalTime = 60)
            TimerDisplay(timeRemaining = 8, totalTime = 60)
            TimerDisplay(timeRemaining = 3, totalTime = 60)
            TimerDisplay(timeRemaining = 0, totalTime = 60)
        }
    }
}
