package dev.krgm4d.shiroguessr.ui.component

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.GradientMap
import dev.krgm4d.shiroguessr.model.MapCoordinate
import dev.krgm4d.shiroguessr.model.Pin
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.SampleBorder
import dev.krgm4d.shiroguessr.ui.theme.ShiroAnimation
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Displays an interactive gradient map with pins, wrapped in a gallery-style frame.
 *
 * Corresponds to the iOS version's `GradientMapView.swift`.
 * Renders a bilinear gradient using Canvas, supports tap-to-place pins,
 * and shows animated target pins and dashed lines after submission.
 *
 * The map is framed with a dark border and subtle shadow, evoking a gallery
 * exhibit aesthetic consistent with the Shiro Gallery design system.
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
    // Cache the gradient as an ImageBitmap so we do not redraw 2500 rects every frame.
    // Regenerate only when the gradient map instance changes.
    val gradientBitmap = remember(gradientMap) { renderGradientBitmap(gradientMap) }

    // Gallery-style frame: dark border (3dp) with subtle shadow
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(4.dp),
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.4f),
            )
            .clip(RoundedCornerShape(4.dp)),
    ) {
        // Dark border frame
        Box(
            modifier = Modifier
                .size(mapSize + 6.dp) // 3dp border on each side
                .clip(RoundedCornerShape(4.dp)),
        ) {
            // Dark border background
            Canvas(modifier = Modifier.size(mapSize + 6.dp)) {
                drawRect(color = SampleBorder)
            }

            // Inner map content with 3dp padding (the border)
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(mapSize)
                    .clip(RoundedCornerShape(2.dp)),
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
                                    val normalizedX =
                                        (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                                    val normalizedY =
                                        (offset.y / size.height.toFloat()).coerceIn(0f, 1f)
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

                // Dashed line between user pin and target pin (gold color)
                if (userPin != null && targetPin != null && lineDrawProgress > 0f) {
                    DashedLinePath(
                        fromX = userPin.coordinate.x.toFloat(),
                        fromY = userPin.coordinate.y.toFloat(),
                        toX = targetPin.coordinate.x.toFloat(),
                        toY = targetPin.coordinate.y.toFloat(),
                        progress = lineDrawProgress,
                        mapSize = mapSize,
                        lineColor = AccentPrimary.copy(alpha = 0.8f),
                    )
                }

                // User pin (gold accent circle with bounce-drop animation)
                if (userPin != null) {
                    UserPinMarker(
                        coordinate = userPin.coordinate,
                        mapSize = mapSize,
                    )
                }

                // Target pin (white circle with red outline, optionally animated)
                if (targetPin != null && showTargetPinAnimated) {
                    AnimatedTargetPin(
                        coordinate = targetPin.coordinate,
                        mapSize = mapSize,
                    )
                } else if (targetPin != null) {
                    TargetPinMarker(
                        coordinate = targetPin.coordinate,
                        mapSize = mapSize,
                    )
                }
            }
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
 * User pin marker with gold accent color and ease-out drop animation.
 *
 * Renders as a gold-filled circle with a white border, drop shadow, and
 * an ease-out animation that replays every time the pin is placed.
 */
@Composable
private fun UserPinMarker(
    coordinate: MapCoordinate,
    mapSize: Dp,
) {
    // Ease-out drop animation: pin drops from above with scale-in
    val offsetY = remember { Animatable(-30f) }
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(coordinate) {
        // Reset to initial values for each pin placement
        offsetY.snapTo(-15f)
        scale.snapTo(0.8f)
        // Animate to final position with ease-out (run both in parallel)
        coroutineScope {
            launch { offsetY.animateTo(0f, tween(150, easing = EaseOut)) }
            launch { scale.animateTo(1f, tween(150, easing = EaseOut)) }
        }
    }

    val animatedOffsetY = offsetY.value
    val animatedScale = scale.value

    Canvas(modifier = Modifier.size(mapSize)) {
        val pinX = coordinate.x.toFloat() * size.width
        val pinY = coordinate.y.toFloat() * size.height + animatedOffsetY.dp.toPx()
        val pinRadius = 8.dp.toPx() * animatedScale
        val center = Offset(pinX, pinY)

        if (animatedScale > 0f) {
            // Drop shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.4f),
                radius = pinRadius + 2.dp.toPx() * animatedScale,
                center = center.copy(y = center.y + 2.dp.toPx()),
            )

            // Filled circle (gold accent)
            drawCircle(
                color = AccentPrimary,
                radius = pinRadius,
                center = center,
            )

            // White border
            drawCircle(
                color = Color.White,
                radius = pinRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx() * animatedScale),
            )

            // White center dot
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx() * animatedScale,
                center = center,
            )
        }
    }
}

/**
 * Static target pin marker with white fill and red outline.
 *
 * Used when the target pin is displayed without animation (e.g., on revisit).
 */
@Composable
private fun TargetPinMarker(
    coordinate: MapCoordinate,
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

        // Filled circle (white)
        drawCircle(
            color = Color.White,
            radius = pinRadius,
            center = center,
        )

        // Red outline
        drawCircle(
            color = Color.Red,
            radius = pinRadius,
            center = center,
            style = Stroke(width = 2.5.dp.toPx()),
        )

        // Red center dot
        drawCircle(
            color = Color.Red,
            radius = 2.dp.toPx(),
            center = center,
        )
    }
}

/**
 * Target pin that animates in with a spring pop-in effect and continuous pulse.
 *
 * Renders as a white circle with a red outline. On appearance, the pin
 * scales up with spring physics, then continuously pulses to draw attention.
 *
 * Corresponds to the iOS version's `AnimatedTargetPin`.
 *
 * Phase 4-3: Spring parameters unified to stiffness 300, dampingRatio 0.7.
 * Pulse easing unified to EaseInOut for consistency.
 */
@Composable
private fun AnimatedTargetPin(
    coordinate: MapCoordinate,
    mapSize: Dp,
) {
    // Pop-in scale animation
    var targetScale by remember { mutableStateOf(0f) }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = ShiroAnimation.SPRING_DAMPING_RATIO,
            stiffness = ShiroAnimation.SPRING_STIFFNESS,
        ),
        label = "targetPinScale",
    )

    // Pulse animation (continuous after pop-in)
    // Phase 4-3: unified to EaseInOut easing
    val infiniteTransition = rememberInfiniteTransition(label = "targetPinPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "targetPinPulseScale",
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "targetPinPulseAlpha",
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
            // Pulse ring (outer glow effect)
            if (scale > 0.8f) {
                drawCircle(
                    color = Color.Red.copy(alpha = pulseAlpha),
                    radius = pinRadius * pulseScale * 1.8f,
                    center = center,
                )
            }

            // Shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.3f),
                radius = pinRadius + 1.dp.toPx() * scale,
                center = center.copy(y = center.y + 1.dp.toPx()),
            )

            // Filled circle (white)
            drawCircle(
                color = Color.White,
                radius = pinRadius,
                center = center,
            )

            // Red outline
            drawCircle(
                color = Color.Red,
                radius = pinRadius,
                center = center,
                style = Stroke(width = 2.5.dp.toPx() * scale),
            )

            // Red center dot
            drawCircle(
                color = Color.Red,
                radius = 2.dp.toPx() * scale,
                center = center,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
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
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
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
            modifier = Modifier.padding(16.dp),
        )
    }
}
