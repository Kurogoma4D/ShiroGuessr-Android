package dev.krgm4d.shiroguessr.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [ShiroAnimation] constants.
 *
 * Verifies that the centralized animation constants match the
 * Shiro Gallery design guideline specifications (Phase 4-3).
 */
class ShiroAnimationTest {

    @Test
    fun `standard spring stiffness matches guideline`() {
        assertEquals(300f, ShiroAnimation.SPRING_STIFFNESS)
    }

    @Test
    fun `standard spring damping ratio matches guideline`() {
        assertEquals(0.7f, ShiroAnimation.SPRING_DAMPING_RATIO)
    }

    @Test
    fun `bounce spring damping ratio is lower for overshoot`() {
        assertEquals(0.4f, ShiroAnimation.SPRING_BOUNCE_DAMPING_RATIO)
        assert(ShiroAnimation.SPRING_BOUNCE_DAMPING_RATIO < ShiroAnimation.SPRING_DAMPING_RATIO) {
            "Bounce damping should be lower than standard for overshoot"
        }
    }

    @Test
    fun `tween durations are within guideline range`() {
        // Guideline specifies 300-500ms range
        assert(ShiroAnimation.TWEEN_DURATION_MS in 300..500) {
            "Standard tween ${ShiroAnimation.TWEEN_DURATION_MS}ms should be within 300-500ms range"
        }
        assert(ShiroAnimation.TWEEN_DURATION_MEDIUM_MS in 300..500) {
            "Medium tween ${ShiroAnimation.TWEEN_DURATION_MEDIUM_MS}ms should be within 300-500ms range"
        }
        assert(ShiroAnimation.TWEEN_DURATION_LONG_MS in 300..500) {
            "Long tween ${ShiroAnimation.TWEEN_DURATION_LONG_MS}ms should be within 300-500ms range"
        }
    }

    @Test
    fun `tween durations are ordered correctly`() {
        assert(ShiroAnimation.TWEEN_DURATION_MS <= ShiroAnimation.TWEEN_DURATION_MEDIUM_MS) {
            "Standard tween should be <= medium tween"
        }
        assert(ShiroAnimation.TWEEN_DURATION_MEDIUM_MS <= ShiroAnimation.TWEEN_DURATION_LONG_MS) {
            "Medium tween should be <= long tween"
        }
    }
}
