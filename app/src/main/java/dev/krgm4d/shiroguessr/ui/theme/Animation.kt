package dev.krgm4d.shiroguessr.ui.theme

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Centralized animation constants for ShiroGuessr, following the
 * Shiro Gallery design guideline (Phase 4-3).
 *
 * All animations across the app should use these shared specs to
 * maintain visual consistency:
 * - Spring: stiffness 300, dampingRatio 0.7
 * - Tween: 300-500ms, EaseInOut
 *
 * Deviations are permitted only when explicitly justified (e.g.,
 * bounce effects that intentionally use a lower dampingRatio for
 * overshoot, or continuous background animations that use LinearEasing).
 */
object ShiroAnimation {
    // -- Spring defaults (Shiro Gallery spec) ---------------------------------

    /** Standard spring stiffness per Shiro Gallery guideline. */
    const val SPRING_STIFFNESS = 300f

    /** Standard spring damping ratio per Shiro Gallery guideline. */
    const val SPRING_DAMPING_RATIO = 0.7f

    /**
     * Bounce spring damping ratio for effects that intentionally overshoot
     * (e.g., score burst, pin drop). Lower values produce more overshoot.
     */
    const val SPRING_BOUNCE_DAMPING_RATIO = 0.4f

    // -- Tween defaults (Shiro Gallery spec) ----------------------------------

    /** Standard tween duration in milliseconds (short end of 300-500ms range). */
    const val TWEEN_DURATION_MS = 300

    /** Medium tween duration in milliseconds (mid-range). */
    const val TWEEN_DURATION_MEDIUM_MS = 400

    /** Long tween duration in milliseconds (upper end of 300-500ms range). */
    const val TWEEN_DURATION_LONG_MS = 500

    /**
     * Pulse duration for the timer warning state.
     * Exceeds the standard 300-500ms range for visual emphasis on the slow pulse.
     */
    const val TWEEN_DURATION_PULSE_MS = 700

    // -- Convenience factory methods ------------------------------------------

    /**
     * Creates the standard Shiro Gallery spring spec.
     * Use for selection animations, press effects, entrance transitions, etc.
     */
    fun <T> standardSpring() = spring<T>(
        dampingRatio = SPRING_DAMPING_RATIO,
        stiffness = SPRING_STIFFNESS,
    )

    /**
     * Creates a bounce spring spec with more overshoot.
     * Use for celebratory effects like score bursts.
     */
    fun <T> bounceSpring() = spring<T>(
        dampingRatio = SPRING_BOUNCE_DAMPING_RATIO,
        stiffness = SPRING_STIFFNESS,
    )

    /**
     * Creates the standard Shiro Gallery tween spec (300ms, EaseInOut).
     */
    fun <T> standardTween(durationMillis: Int = TWEEN_DURATION_MS) = tween<T>(
        durationMillis = durationMillis,
        easing = EaseInOut,
    )
}
