package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.ShiroAnimation
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Draws an animated dashed line between two points in normalized coordinates (0-1).
 *
 * Corresponds to the iOS version's `DashedLinePath.swift`.
 * The line is drawn progressively based on [progress], allowing
 * animated reveal of the path from user pin to target pin.
 *
 * @param fromX Normalized x coordinate of the start point (0.0-1.0)
 * @param fromY Normalized y coordinate of the start point (0.0-1.0)
 * @param toX Normalized x coordinate of the end point (0.0-1.0)
 * @param toY Normalized y coordinate of the end point (0.0-1.0)
 * @param progress Drawing progress (0.0-1.0). 0 = not visible, 1 = fully drawn
 * @param mapSize The size of the map canvas
 * @param lineColor Color of the dashed line
 * @param modifier Optional modifier
 */
@Composable
fun DashedLinePath(
    fromX: Float,
    fromY: Float,
    toX: Float,
    toY: Float,
    progress: Float,
    mapSize: Dp,
    lineColor: Color = AccentPrimary.copy(alpha = 0.8f),
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ShiroAnimation.standardTween(ShiroAnimation.TWEEN_DURATION_MEDIUM_MS),
        label = "dashedLineProgress",
    )

    if (animatedProgress <= 0f) return

    Canvas(
        modifier = modifier.size(mapSize),
    ) {
        val startX = fromX * size.width
        val startY = fromY * size.height
        val endX = toX * size.width
        val endY = toY * size.height

        // Calculate the endpoint based on progress
        val currentEndX = startX + (endX - startX) * animatedProgress
        val currentEndY = startY + (endY - startY) * animatedProgress

        drawLine(
            color = lineColor,
            start = Offset(startX, startY),
            end = Offset(currentEndX, currentEndY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(8.dp.toPx(), 6.dp.toPx()),
                phase = 0f,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DashedLinePathPreview() {
    ShiroGuessrAndroidTheme {
        Box(modifier = Modifier.size(300.dp)) {
            DashedLinePath(
                fromX = 0.2f,
                fromY = 0.3f,
                toX = 0.8f,
                toY = 0.7f,
                progress = 1f,
                mapSize = 300.dp,
            )
        }
    }
}
