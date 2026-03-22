package dev.krgm4d.shiroguessr.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.AccentSecondary
import dev.krgm4d.shiroguessr.ui.theme.CanvasDeep
import dev.krgm4d.shiroguessr.ui.theme.CanvasElevated
import dev.krgm4d.shiroguessr.ui.theme.ShiroAnimation
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.ui.theme.TextPrimary
import dev.krgm4d.shiroguessr.ui.theme.TextSecondary

/**
 * Redesigned start screen following the Shiro Gallery design guideline.
 *
 * Features:
 * - Dark canvas with a subtly animated white radial gradient background
 * - "ShiroGuessr" title with "Shiro" highlighted in gold accent
 * - Two vertically stacked mode selection cards (Classic and Map)
 *
 * @param onClassicSelected Callback when Classic mode card is tapped
 * @param onMapSelected Callback when Map mode card is tapped
 */
@Composable
fun StartScreen(
    onClassicSelected: () -> Unit,
    onMapSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundGradient")

    // Animate the gradient center position for a slowly shifting effect
    val gradientOffsetX by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gradientOffsetX",
    )
    val gradientOffsetY by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gradientOffsetY",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDeep)
            .drawBehind {
                val centerX = size.width * gradientOffsetX
                val centerY = size.height * gradientOffsetY
                val radius = size.maxDimension * 0.6f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent,
                        ),
                        center = Offset(centerX, centerY),
                        radius = radius,
                    ),
                    radius = radius,
                    center = Offset(centerX, centerY),
                )
            },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Title: "ShiroGuessr" with "Shiro" in gold
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = AccentPrimary)) {
                        append("Shiro")
                    }
                    withStyle(SpanStyle(color = TextPrimary)) {
                        append("Guessr")
                    }
                },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Mode selection cards
            ModeSelectionCard(
                icon = Icons.Default.GridView,
                title = stringResource(R.string.start_classic_title),
                description = stringResource(R.string.start_classic_description),
                previewContent = { ClassicModePreview() },
                onClick = onClassicSelected,
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModeSelectionCard(
                icon = Icons.Default.Map,
                title = stringResource(R.string.start_map_title),
                description = stringResource(R.string.start_map_description),
                previewContent = { MapModePreview() },
                onClick = onMapSelected,
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

/**
 * A card representing a game mode selection.
 *
 * Follows the Shiro Gallery card style: canvas-elevated background,
 * 16dp corner radius, subtle border, and press animation.
 */
@Composable
private fun ModeSelectionCard(
    icon: ImageVector,
    title: String,
    description: String,
    previewContent: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = ShiroAnimation.standardSpring(),
        label = "cardScale",
    )

    val cardDescription = stringResource(R.string.cd_mode_card, title, description)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .semantics { contentDescription = cardDescription }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CanvasElevated,
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    AccentSecondary.copy(alpha = 0.3f),
                    AccentSecondary.copy(alpha = 0.1f),
                ),
            ),
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Icon + text column
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Preview illustration
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp),
            ) {
                previewContent()
            }
        }
    }
}

/**
 * Preview illustration for Classic mode: a small 3x3 grid of color cells
 * conveying the palette-based gameplay.
 */
@Composable
private fun ClassicModePreview() {
    val colors = listOf(
        Color(0xFFFFFAF5), Color(0xFFF8F5F0), Color(0xFFFFF5FA),
        Color(0xFFF5F8FF), Color(0xFFFAFAFA), Color(0xFFF0F5F0),
        Color(0xFFFFF8F0), Color(0xFFF5F0F8), Color(0xFFFFF5F5),
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        for (row in 0 until 3) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                for (col in 0 until 3) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                color = colors[row * 3 + col],
                                shape = RoundedCornerShape(4.dp),
                            ),
                    )
                }
            }
        }
    }
}

/**
 * Preview illustration for Map mode: a small gradient square
 * conveying the gradient-map-based gameplay.
 */
@Composable
private fun MapModePreview() {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF8F0),
                        Color(0xFFF0F5FF),
                        Color(0xFFF5FFF5),
                    ),
                ),
                shape = RoundedCornerShape(8.dp),
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun StartScreenPreview() {
    ShiroGuessrAndroidTheme {
        StartScreen(
            onClassicSelected = {},
            onMapSelected = {},
        )
    }
}
