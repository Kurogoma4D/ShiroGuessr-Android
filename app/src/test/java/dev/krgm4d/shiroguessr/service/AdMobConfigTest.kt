package dev.krgm4d.shiroguessr.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [AdMobConfig].
 *
 * Verifies that the ad unit ID is properly configured and non-empty.
 * In test/debug builds, the Google test ad ID should be used.
 */
class AdMobConfigTest {

    @Test
    fun `interstitialAdUnitId is not null or blank`() {
        val adUnitId = AdMobConfig.interstitialAdUnitId
        assertNotNull("Ad unit ID should not be null", adUnitId)
        assertTrue("Ad unit ID should not be blank", adUnitId.isNotBlank())
    }

    @Test
    fun `interstitialAdUnitId follows AdMob format`() {
        val adUnitId = AdMobConfig.interstitialAdUnitId
        // AdMob ad unit IDs start with "ca-app-pub-"
        assertTrue(
            "Ad unit ID should follow AdMob format (ca-app-pub-...)",
            adUnitId.startsWith("ca-app-pub-"),
        )
    }

    @Test
    fun `debug build uses test ad unit ID`() {
        val adUnitId = AdMobConfig.interstitialAdUnitId
        // In debug/test builds, the test interstitial ID should be used
        val testInterstitialId = "ca-app-pub-3940256099942544/1033173712"
        assertTrue(
            "Debug build should use Google test interstitial ad unit ID",
            adUnitId == testInterstitialId,
        )
    }

    @Test
    fun `ad unit ID does not contain placeholder text`() {
        val adUnitId = AdMobConfig.interstitialAdUnitId
        assertFalse(
            "Ad unit ID should not contain placeholder text",
            adUnitId.contains("YOUR_AD_UNIT_ID") || adUnitId.contains("PLACEHOLDER"),
        )
    }
}
