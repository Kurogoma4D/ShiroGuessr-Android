package dev.krgm4d.shiroguessr.service

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Unit tests for [TutorialManager].
 *
 * Verifies that the tutorial shown flag is correctly read, written,
 * and reset via SharedPreferences.
 */
class TutorialManagerTest {

    private lateinit var prefs: FakeSharedPreferences
    private lateinit var context: Context
    private lateinit var tutorialManager: TutorialManager

    @Before
    fun setUp() {
        prefs = FakeSharedPreferences()
        context = mock(Context::class.java)
        `when`(context.getSharedPreferences("shiroguessr_tutorial", Context.MODE_PRIVATE))
            .thenReturn(prefs)
        tutorialManager = TutorialManager(context)
    }

    @Test
    fun `hasShownTutorial is false by default`() {
        assertFalse(tutorialManager.hasShownTutorial)
    }

    @Test
    fun `markTutorialAsShown sets flag to true`() {
        tutorialManager.markTutorialAsShown()
        assertTrue(tutorialManager.hasShownTutorial)
    }

    @Test
    fun `resetTutorial sets flag back to false`() {
        tutorialManager.markTutorialAsShown()
        assertTrue(tutorialManager.hasShownTutorial)

        tutorialManager.resetTutorial()
        assertFalse(tutorialManager.hasShownTutorial)
    }

    /**
     * Minimal in-memory SharedPreferences implementation for testing.
     */
    private class FakeSharedPreferences : SharedPreferences {
        private val data = mutableMapOf<String, Any?>()

        override fun getBoolean(key: String, defValue: Boolean): Boolean {
            return data[key] as? Boolean ?: defValue
        }

        override fun contains(key: String) = data.containsKey(key)
        override fun getAll(): MutableMap<String, *> = data
        override fun getString(key: String, defValue: String?) = data[key] as? String ?: defValue
        override fun getStringSet(key: String, defValues: MutableSet<String>?) =
            @Suppress("UNCHECKED_CAST") (data[key] as? MutableSet<String> ?: defValues)
        override fun getInt(key: String, defValue: Int) = data[key] as? Int ?: defValue
        override fun getLong(key: String, defValue: Long) = data[key] as? Long ?: defValue
        override fun getFloat(key: String, defValue: Float) = data[key] as? Float ?: defValue

        override fun edit(): SharedPreferences.Editor = FakeEditor()

        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) { /* no-op */ }

        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) { /* no-op */ }

        private inner class FakeEditor : SharedPreferences.Editor {
            private val pending = mutableMapOf<String, Any?>()
            private var clear = false

            override fun putString(key: String, value: String?) = apply { pending[key] = value }
            override fun putStringSet(key: String, values: MutableSet<String>?) = apply { pending[key] = values }
            override fun putInt(key: String, value: Int) = apply { pending[key] = value }
            override fun putLong(key: String, value: Long) = apply { pending[key] = value }
            override fun putFloat(key: String, value: Float) = apply { pending[key] = value }
            override fun putBoolean(key: String, value: Boolean) = apply { pending[key] = value }
            override fun remove(key: String) = apply { pending[key] = null }
            override fun clear() = apply { clear = true }

            override fun commit(): Boolean {
                applyChanges()
                return true
            }

            override fun apply() {
                applyChanges()
            }

            private fun applyChanges() {
                if (clear) data.clear()
                for ((k, v) in pending) {
                    if (v == null) data.remove(k) else data[k] = v
                }
            }
        }
    }
}
