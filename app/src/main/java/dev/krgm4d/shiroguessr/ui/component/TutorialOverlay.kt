package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.CanvasDeep
import dev.krgm4d.shiroguessr.ui.theme.CanvasElevated
import dev.krgm4d.shiroguessr.ui.theme.SampleBorder
import dev.krgm4d.shiroguessr.ui.theme.ShiroAnimation
import dev.krgm4d.shiroguessr.ui.theme.TextMuted
import dev.krgm4d.shiroguessr.ui.theme.TextSecondary

/**
 * Tutorial fullscreen overlay displayed on first launch.
 *
 * Redesigned from BottomSheet to a fullscreen overlay with card-style content
 * on a dark background, following the Shiro Gallery design guideline (Phase 4-1).
 *
 * Contains 3 pages with crossfade transitions:
 * 1. Welcome - Two white color samples side by side ("They look the same, but they're different")
 * 2. How to Play - Diagram of tapping to select from a palette
 * 3. Game Modes - Diagram of placing a pin on a gradient map
 *
 * Corresponds to the iOS version's TutorialOverlay.swift.
 */
@Composable
fun TutorialOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentPage by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDeep.copy(alpha = 0.95f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            // Card-style content with crossfade transition
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CanvasElevated)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        fadeIn(
                            animationSpec = ShiroAnimation.standardTween(
                                ShiroAnimation.TWEEN_DURATION_MEDIUM_MS,
                            ),
                        ) togetherWith fadeOut(
                            animationSpec = ShiroAnimation.standardTween(
                                ShiroAnimation.TWEEN_DURATION_MEDIUM_MS,
                            ),
                        )
                    },
                    label = "tutorialPageTransition",
                ) { page ->
                    when (page) {
                        0 -> WelcomePage()
                        1 -> HowToPlayPage()
                        2 -> GameModesPage()
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Page indicator dots
            PageIndicator(
                pageCount = PAGE_COUNT,
                currentPage = currentPage,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation button
            val isLastPage = currentPage == PAGE_COUNT - 1
            MdFilledButton(
                onClick = {
                    if (isLastPage) {
                        onDismiss()
                    } else {
                        currentPage = (currentPage + 1).coerceAtMost(PAGE_COUNT - 1)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(if (isLastPage) R.string.tutorial_start else R.string.tutorial_next),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Page 1: Welcome - Two white color samples side by side
// ---------------------------------------------------------------------------

/**
 * Page 1: Two white color samples displayed side by side, illustrating
 * that they look the same but are actually different.
 */
@Composable
private fun WelcomePage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Diagram: two white color samples side by side
        ColorComparisonDiagram()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.tutorial_welcome_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.tutorial_welcome_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = TextSecondary,
        )
    }
}

/**
 * Diagram showing two subtly different white color samples side by side.
 * Visually represents the core concept: "They look the same, but they're different."
 */
@Composable
private fun ColorComparisonDiagram() {
    val sampleColor1 = Color(0xFFFAF8FC) // Slightly purple-white
    val sampleColor2 = Color(0xFFFCF8F0) // Slightly warm-white
    val borderColor = SampleBorder

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Left sample
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(borderColor)
                .padding(1.5.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(sampleColor1),
        )

        Spacer(modifier = Modifier.width(16.dp))

        // "vs" or comparison indicator
        Text(
            text = "?",
            style = MaterialTheme.typography.headlineMedium,
            color = AccentPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Right sample
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(borderColor)
                .padding(1.5.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(sampleColor2),
        )
    }
}

// ---------------------------------------------------------------------------
// Page 2: How to Play - Palette tap diagram
// ---------------------------------------------------------------------------

/**
 * Page 2: Diagram showing a small palette grid with one cell highlighted,
 * representing tapping to select from a palette of colors.
 */
@Composable
private fun HowToPlayPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Diagram: palette selection
        PaletteSelectionDiagram()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.tutorial_howto_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.tutorial_howto_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = TextSecondary,
        )
    }
}

/**
 * Simplified 3x3 palette grid diagram with one cell highlighted by a gold ring,
 * representing the action of selecting a color from options.
 */
@Composable
private fun PaletteSelectionDiagram() {
    val gridSize = 3
    val accentColor = AccentPrimary
    val borderColor = SampleBorder

    // Generate slightly different white shades for the palette cells
    val cellColors = listOf(
        Color(0xFFF8F8FC), Color(0xFFFCF8F4), Color(0xFFF6FAF8),
        Color(0xFFFAF6F8), Color(0xFFFCFAF0), Color(0xFFF8F4FA),
        Color(0xFFF4F8FC), Color(0xFFFAFCF8), Color(0xFFFCF6F4),
    )
    // The highlighted cell index (center cell)
    val selectedIndex = 4

    Canvas(
        modifier = Modifier.size(140.dp),
    ) {
        val cellSize = size.width / gridSize
        val cellPadding = 4.dp.toPx()
        val cornerRadius = CornerRadius(12.dp.toPx())

        for (i in 0 until gridSize * gridSize) {
            val col = i % gridSize
            val row = i / gridSize
            val x = col * cellSize + cellPadding
            val y = row * cellSize + cellPadding
            val rectSize = cellSize - cellPadding * 2

            // Cell border
            drawRoundRect(
                color = if (i == selectedIndex) accentColor else borderColor,
                topLeft = Offset(x, y),
                size = Size(rectSize, rectSize),
                cornerRadius = cornerRadius,
                style = Stroke(width = if (i == selectedIndex) 3.dp.toPx() else 1.5.dp.toPx()),
            )

            // Cell fill
            val inset = if (i == selectedIndex) 3.dp.toPx() else 1.5.dp.toPx()
            drawRoundRect(
                color = cellColors[i],
                topLeft = Offset(x + inset, y + inset),
                size = Size(rectSize - inset * 2, rectSize - inset * 2),
                cornerRadius = CornerRadius((12.dp - 1.5.dp).toPx()),
                style = Fill,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Page 3: Game Modes - Gradient map with pin diagram
// ---------------------------------------------------------------------------

/**
 * Page 3: Diagram showing a gradient map with a pin placed on it,
 * representing the Map mode gameplay.
 */
@Composable
private fun GameModesPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Diagram: gradient map with pin
        GradientMapDiagram()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.tutorial_modes_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Classic mode description
        GameModeItem(
            label = stringResource(R.string.tutorial_modes_classic_title),
            description = stringResource(R.string.tutorial_modes_classic_desc),
            indicatorColor = AccentPrimary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Map mode description
        GameModeItem(
            label = stringResource(R.string.tutorial_modes_map_title),
            description = stringResource(R.string.tutorial_modes_map_desc),
            indicatorColor = AccentPrimary,
        )
    }
}

/**
 * Simplified gradient map diagram: a rounded rectangle filled with a subtle
 * white gradient, with a gold pin marker and a dashed line from pin to a target.
 */
@Composable
private fun GradientMapDiagram() {
    val accentColor = AccentPrimary
    val borderColor = SampleBorder

    Canvas(
        modifier = Modifier.size(140.dp),
    ) {
        val cornerRadius = CornerRadius(12.dp.toPx())

        // Map border
        drawRoundRect(
            color = borderColor,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = cornerRadius,
            style = Stroke(width = 2.dp.toPx()),
        )

        // Map fill with a subtle gradient simulation using four corner quadrants
        val topLeftColor = Color(0xFFF8F4F0)
        val topRightColor = Color(0xFFF0F4F8)
        val bottomLeftColor = Color(0xFFFCF8F4)
        val bottomRightColor = Color(0xFFF4F8FC)

        // Draw simplified gradient as four quadrants
        val halfW = size.width / 2
        val halfH = size.height / 2
        val inset = 2.dp.toPx()
        drawRect(topLeftColor, Offset(inset, inset), Size(halfW - inset, halfH - inset))
        drawRect(topRightColor, Offset(halfW, inset), Size(halfW - inset, halfH - inset))
        drawRect(bottomLeftColor, Offset(inset, halfH), Size(halfW - inset, halfH - inset))
        drawRect(bottomRightColor, Offset(halfW, halfH), Size(halfW - inset, halfH - inset))

        // Target position (small white circle with red outline)
        val targetX = size.width * 0.35f
        val targetY = size.height * 0.4f
        drawCircle(
            color = Color.White,
            radius = 6.dp.toPx(),
            center = Offset(targetX, targetY),
        )
        drawCircle(
            color = Color(0xFFC87E7E),
            radius = 6.dp.toPx(),
            center = Offset(targetX, targetY),
            style = Stroke(width = 1.5.dp.toPx()),
        )

        // User pin position (gold circle with shadow)
        val pinX = size.width * 0.65f
        val pinY = size.height * 0.6f

        // Dashed line between target and pin
        drawLine(
            color = accentColor.copy(alpha = 0.6f),
            start = Offset(targetX, targetY),
            end = Offset(pinX, pinY),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(6.dp.toPx(), 4.dp.toPx()),
            ),
        )

        // Pin shadow
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            radius = 8.dp.toPx(),
            center = Offset(pinX + 1.dp.toPx(), pinY + 1.dp.toPx()),
        )

        // Pin
        drawCircle(
            color = accentColor,
            radius = 7.dp.toPx(),
            center = Offset(pinX, pinY),
        )
        drawCircle(
            color = Color.White,
            radius = 3.dp.toPx(),
            center = Offset(pinX, pinY),
        )
    }
}

/**
 * A single game mode item with a colored dot indicator, title, and description.
 * Replaces the previous Material Icon-based GameModeItem.
 */
@Composable
private fun GameModeItem(
    label: String,
    description: String,
    indicatorColor: Color,
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Simple dot indicator instead of Material Icon
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(indicatorColor),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}

/**
 * Page indicator dots showing the current page within the tutorial.
 */
@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val size by animateDpAsState(
                targetValue = if (isSelected) 10.dp else 8.dp,
                animationSpec = ShiroAnimation.standardSpring(),
                label = "dotSize",
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) AccentPrimary else TextMuted,
                    ),
            )
        }
    }
}

private const val PAGE_COUNT = 3
