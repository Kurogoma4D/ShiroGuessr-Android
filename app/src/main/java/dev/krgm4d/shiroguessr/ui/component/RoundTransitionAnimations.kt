package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import dev.krgm4d.shiroguessr.ui.theme.ShiroAnimation

/**
 * Holds the animated values used during round transitions.
 *
 * All offset values are in dp (hence the "Dp" suffix).
 *
 * @property targetAlpha Fade-in alpha for the target color display (0f -> 1f).
 * @property contentOffsetYDp Slide-in offset for the main content (palette or map) in dp.
 * @property controlsOffsetYDp Slide-in offset for the controls section in dp.
 * @property controlsAlpha Fade-in alpha for the controls section (0f -> 1f).
 */
data class RoundTransitionAnimationValues(
    val targetAlpha: Animatable<Float, *>,
    val contentOffsetYDp: Animatable<Float, *>,
    val controlsOffsetYDp: Animatable<Float, *>,
    val controlsAlpha: Animatable<Float, *>,
)

/**
 * Creates and runs the shared round-transition animations.
 *
 * Both ClassicGameScreen and MapGameScreen use the same animation pattern:
 * 1. Target color fades in (standard tween: 300ms EaseInOut)
 * 2. Main content slides in from below (standard spring: stiffness 300, dampingRatio 0.7)
 * 3. Controls slide in with a staggered delay (80ms) using the same spring + fade
 *
 * Phase 4-3: All animation specs now use [ShiroAnimation] constants for consistency.
 *
 * @param roundNumber The current round number, used as a key to restart animations.
 * @return [RoundTransitionAnimationValues] containing all animated values.
 */
@Composable
fun rememberRoundTransitionAnimations(roundNumber: Int): RoundTransitionAnimationValues {
    val targetAlpha = remember(roundNumber) { Animatable(0f) }
    val contentOffsetYDp = remember(roundNumber) { Animatable(200f) }
    val controlsOffsetYDp = remember(roundNumber) { Animatable(120f) }
    val controlsAlpha = remember(roundNumber) { Animatable(0f) }

    LaunchedEffect(roundNumber) {
        // Target color fade-in (standard tween: 300ms EaseInOut)
        targetAlpha.animateTo(
            targetValue = 1f,
            animationSpec = ShiroAnimation.standardTween(),
        )
    }

    LaunchedEffect(roundNumber) {
        // Content slide-in from below (standard spring: stiffness 300, dampingRatio 0.7)
        contentOffsetYDp.animateTo(
            targetValue = 0f,
            animationSpec = ShiroAnimation.standardSpring(),
        )
    }

    LaunchedEffect(roundNumber) {
        // Controls staggered slide-in
        kotlinx.coroutines.delay(80L)
        controlsAlpha.animateTo(
            targetValue = 1f,
            animationSpec = ShiroAnimation.standardTween(),
        )
    }

    LaunchedEffect(roundNumber) {
        kotlinx.coroutines.delay(80L)
        controlsOffsetYDp.animateTo(
            targetValue = 0f,
            animationSpec = ShiroAnimation.standardSpring(),
        )
    }

    return RoundTransitionAnimationValues(
        targetAlpha = targetAlpha,
        contentOffsetYDp = contentOffsetYDp,
        controlsOffsetYDp = controlsOffsetYDp,
        controlsAlpha = controlsAlpha,
    )
}
