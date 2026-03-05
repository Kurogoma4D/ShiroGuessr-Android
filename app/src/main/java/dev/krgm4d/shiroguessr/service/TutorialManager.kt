package dev.krgm4d.shiroguessr.service

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages the tutorial display state using SharedPreferences.
 *
 * Tracks whether the user has completed the first-launch tutorial
 * bottom sheet. Corresponds to the iOS version's TutorialManager.swift.
 */
class TutorialManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Whether the tutorial has already been shown to the user.
     */
    val hasShownTutorial: Boolean
        get() = prefs.getBoolean(KEY_HAS_SHOWN_TUTORIAL, false)

    /**
     * Marks the tutorial as shown so it will not appear on subsequent launches.
     */
    fun markTutorialAsShown() {
        prefs.edit().putBoolean(KEY_HAS_SHOWN_TUTORIAL, true).apply()
    }

    /**
     * Resets the tutorial flag so it will be shown again on next launch.
     * Intended for debugging purposes.
     */
    fun resetTutorial() {
        prefs.edit().putBoolean(KEY_HAS_SHOWN_TUTORIAL, false).apply()
    }

    companion object {
        private const val PREFS_NAME = "shiroguessr_tutorial"
        private const val KEY_HAS_SHOWN_TUTORIAL = "has_shown_tutorial"
    }
}
