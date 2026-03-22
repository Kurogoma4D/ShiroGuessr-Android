package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.ui.theme.JetBrainsMonoFontFamily
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Displays the remaining time in MM:SS format with color-coded warnings.
 *
 * Corresponds to the iOS version's `TimerDisplay.swift`.
 * Shows the remaining time with visual indicators:
 * - Normal: default text color
 * - Warning (< 10s): orange
 * - Timeout (0s): red
 *
 * @param timeRemaining Remaining time in seconds
 * @param warningThreshold Threshold in seconds for warning state (default: 10)
 * @param modifier Optional modifier for the root layout
 */
@Composable
fun TimerDisplay(
    timeRemaining: Int,
    warningThreshold: Int = 10,
    modifier: Modifier = Modifier,
) {
    val isWarning = timeRemaining in 1..<warningThreshold
    val isTimeout = timeRemaining == 0

    val timerColor by animateColorAsState(
        targetValue = when {
            isTimeout -> Color.Red
            isWarning -> Color(0xFFFF9800) // Orange
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 300),
        label = "timerColor",
    )

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
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
            style = MaterialTheme.typography.titleLarge,
            color = timerColor,
            fontWeight = FontWeight.Medium,
            fontFamily = JetBrainsMonoFontFamily,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerDisplayNormalPreview() {
    ShiroGuessrAndroidTheme {
        Column {
            TimerDisplay(timeRemaining = 45)
            TimerDisplay(timeRemaining = 8)
            TimerDisplay(timeRemaining = 0)
        }
    }
}
