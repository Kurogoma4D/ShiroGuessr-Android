package dev.krgm4d.shiroguessr.service

import dev.krgm4d.shiroguessr.BuildConfig

/**
 * Configuration object for AdMob ad unit IDs.
 *
 * Manages the mapping between debug and release ad unit IDs.
 * In debug builds, Google-provided test ad IDs are used to avoid
 * policy violations. In release builds, production ad IDs are loaded
 * from BuildConfig (sourced from local.properties).
 *
 * Corresponds to the iOS version's `AdMobConfig.swift`.
 */
object AdMobConfig {

    /**
     * The interstitial ad unit ID for the current build variant.
     *
     * - Debug: Google test interstitial ID (`ca-app-pub-3940256099942544/1033173712`)
     * - Release: Production ID from `local.properties` via BuildConfig
     */
    val interstitialAdUnitId: String
        get() = BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID
}
