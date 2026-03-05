package dev.krgm4d.shiroguessr.ui.component

import android.graphics.Bitmap
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.GradientMap
import dev.krgm4d.shiroguessr.model.MapCoordinate
import dev.krgm4d.shiroguessr.model.Pin
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Displays an interactive gradient map with pins.
 *
 * Corresponds to the iOS version's `GradientMapView.swift`.
 * Renders a bilinear gradient using Canvas, supports tap-to-place pins,
 * and shows animated target pins and dashed lines after submission.
 *
 * @param gradientMap The gradient map to display
 * @param userPin Optional pin placed by the user
 * @param targetPin Optional target pin to show after submission
 * @param showTargetPinAnimated Whether to show target pin with pop-in animation
 * @param lineDrawProgress Progress of dashed line drawing (0.0-1.0)
 * @param mapSize The size (width and height) of the square map canvas
 * @param isInteractionEnabled Whether pin placement is enabled
 * @param onPinPlacement Callback when user taps to place a pin
 * @param modifier Optional modifier for the root layout
 */
@Composable
fun GradientMapView(
    gradientMap: GradientMap,
    userPin: Pin? = null,
    targetPin: Pin? = null,
    showTargetPinAnimated: Boolean = false,
    lineDrawProgress: Float = 0f,
    mapSize: Dp = 300.dp,
    isInteractionEnabled: Boolean = true,
    onPinPlacement: ((MapCoordinate) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    // Cache the gradient as an ImageBitmap so we do not redraw 2500 rects every frame.
    // Regenerate only when the gradient map instance changes.
    val gradientBitmap = remember(gradientMap) { renderGradientBitmap(gradientMap) }

    Box(
        modifier = modifier
            .size(mapSize)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = outlineColor,
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        // Gradient map as a pre-rendered bitmap
        Image(
            bitmap = gradientBitmap,
            contentDescription = stringResource(R.string.cd_gradient_map),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(mapSize)
                .pointerInput(isInteractionEnabled) {
                    if (isInteractionEnabled) {
                        detectTapGestures { offset ->
                            val normalizedX = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                            val normalizedY = (offset.y / size.height.toFloat()).coerceIn(0f, 1f)
                            onPinPlacement?.invoke(
                                MapCoordinate(
                                    x = normalizedX.toDouble(),
                                    y = normalizedY.toDouble(),
                                ),
                            )
                        }
                    }
                },
        )

        // Dashed line between user pin and target pin
        if (userPin != null && targetPin != null && lineDrawProgress > 0f) {
            DashedLinePath(
                fromX = userPin.coordinate.x.toFloat(),
                fromY = userPin.coordinate.y.toFloat(),
                toX = targetPin.coordinate.x.toFloat(),
                toY = targetPin.coordinate.y.toFloat(),
                progress = lineDrawProgress,
                mapSize = mapSize,
                lineColor = onSurfaceColor.copy(alpha = 0.6f),
            )
        }

        // User pin (blue circle)
        if (userPin != null) {
            PinMarker(
                coordinate = userPin.coordinate,
                color = Color.Blue,
                mapSize = mapSize,
            )
        }

        // Target pin (red circle, optionally animated)
        if (targetPin != null && showTargetPinAnimated) {
            AnimatedTargetPin(
                coordinate = targetPin.coordinate,
                mapSize = mapSize,
            )
        } else if (targetPin != null) {
            PinMarker(
                coordinate = targetPin.coordinate,
                color = Color.Red,
                mapSize = mapSize,
            )
        }
    }
}

/**
 * Pre-renders the gradient map into an [ImageBitmap] using bilinear interpolation.
 *
 * This is called once per gradient map change and cached via [remember],
 * avoiding the cost of drawing 2500 individual rects on every frame.
 */
private fun renderGradientBitmap(gradientMap: GradientMap): ImageBitmap {
    val width = gradientMap.width
    val height = gradientMap.height

    val topLeft = gradientMap.cornerColors[0]
    val topRight = gradientMap.cornerColors[1]
    val bottomLeft = gradientMap.cornerColors[2]
    val bottomRight = gradientMap.cornerColors[3]

    val pixels = IntArray(width * height)

    for (y in 0 until height) {
        val ny = if (height > 1) y.toDouble() / (height - 1) else 0.0
        for (x in 0 until width) {
            val nx = if (width > 1) x.toDouble() / (width - 1) else 0.0

            // Bilinear interpolation
            val topR = topLeft.r + (topRight.r - topLeft.r) * nx
            val topG = topLeft.g + (topRight.g - topLeft.g) * nx
            val topB = topLeft.b + (topRight.b - topLeft.b) * nx

            val bottomR = bottomLeft.r + (bottomRight.r - bottomLeft.r) * nx
            val bottomG = bottomLeft.g + (bottomRight.g - bottomLeft.g) * nx
            val bottomB = bottomLeft.b + (bottomRight.b - bottomLeft.b) * nx

            val r = (topR + (bottomR - topR) * ny).toInt().coerceIn(0, 255)
            val g = (topG + (bottomG - topG) * ny).toInt().coerceIn(0, 255)
            val b = (topB + (bottomB - topB) * ny).toInt().coerceIn(0, 255)

            pixels[y * width + x] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap.asImageBitmap()
}

/**
 * A pin marker drawn on the map at the given coordinate.
 *
 * Renders as a filled circle with a white border and a white center dot,
 * matching the iOS version's pin appearance.
 */
@Composable
private fun PinMarker(
    coordinate: MapCoordinate,
    color: Color,
    mapSize: Dp,
) {
    Canvas(modifier = Modifier.size(mapSize)) {
        val pinX = coordinate.x.toFloat() * size.width
        val pinY = coordinate.y.toFloat() * size.height
        val pinRadius = 8.dp.toPx()
        val center = Offset(pinX, pinY)

        // Shadow
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            radius = pinRadius + 1.dp.toPx(),
            center = center.copy(y = center.y + 1.dp.toPx()),
        )

        // Filled circle
        drawCircle(
            color = color,
            radius = pinRadius,
            center = center,
        )

        // White border
        drawCircle(
            color = Color.White,
            radius = pinRadius,
            center = center,
            style = Stroke(width = 2.dp.toPx()),
        )

        // White center dot
        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = center,
        )
    }
}

/**
 * Target pin that animates in with a spring pop-in effect.
 *
 * Corresponds to the iOS version's `AnimatedTargetPin`.
 */
@Composable
private fun AnimatedTargetPin(
    coordinate: MapCoordinate,
    mapSize: Dp,
) {
    var targetScale by remember { mutableStateOf(0f) }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "targetPinScale",
    )

    LaunchedEffect(Unit) {
        targetScale = 1f
    }

    Canvas(modifier = Modifier.size(mapSize)) {
        val pinX = coordinate.x.toFloat() * size.width
        val pinY = coordinate.y.toFloat() * size.height
        val pinRadius = 8.dp.toPx() * scale
        val center = Offset(pinX, pinY)

        if (scale > 0f) {
            // Shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.3f),
                radius = pinRadius + 1.dp.toPx() * scale,
                center = center.copy(y = center.y + 1.dp.toPx()),
            )

            // Filled circle (red)
            drawCircle(
                color = Color.Red,
                radius = pinRadius,
                center = center,
            )

            // White border
            drawCircle(
                color = Color.White,
                radius = pinRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx() * scale),
            )

            // White center dot
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx() * scale,
                center = center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GradientMapViewEmptyPreview() {
    ShiroGuessrAndroidTheme {
        GradientMapView(
            gradientMap = GradientMap(
                width = 50,
                height = 50,
                cornerColors = listOf(
                    RGBColor(r = 245, g = 245, b = 245),
                    RGBColor(r = 255, g = 245, b = 255),
                    RGBColor(r = 245, g = 255, b = 255),
                    RGBColor(r = 255, g = 255, b = 245),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GradientMapViewWithPinsPreview() {
    ShiroGuessrAndroidTheme {
        GradientMapView(
            gradientMap = GradientMap(
                width = 50,
                height = 50,
                cornerColors = listOf(
                    RGBColor(r = 245, g = 245, b = 245),
                    RGBColor(r = 255, g = 245, b = 255),
                    RGBColor(r = 245, g = 255, b = 255),
                    RGBColor(r = 255, g = 255, b = 245),
                ),
            ),
            userPin = Pin(
                coordinate = MapCoordinate(x = 0.3, y = 0.7),
                color = RGBColor(r = 250, g = 252, b = 248),
            ),
            targetPin = Pin(
                coordinate = MapCoordinate(x = 0.8, y = 0.2),
                color = RGBColor(r = 253, g = 248, b = 251),
            ),
            showTargetPinAnimated = false,
            lineDrawProgress = 1f,
        )
    }
}
