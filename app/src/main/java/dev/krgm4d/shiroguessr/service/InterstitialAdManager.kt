package dev.krgm4d.shiroguessr.service

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Singleton manager for interstitial ads.
 *
 * Handles preloading and displaying interstitial ads with a graceful
 * fallback: if the ad fails to load or display, the dismiss callback
 * is invoked immediately so gameplay is never blocked.
 *
 * Corresponds to the iOS version's `InterstitialAdManager.swift`.
 *
 * Usage:
 * 1. Call [loadAd] early (e.g., when entering the game screen) to preload.
 * 2. Call [showAd] when you want to display the ad (e.g., on "Play Again").
 *    The [onDismissed] callback fires after the ad is closed or if no ad
 *    is available.
 */
object InterstitialAdManager {

    private const val TAG = "InterstitialAdManager"

    /** The currently loaded interstitial ad, or null if not yet loaded. */
    private var interstitialAd: InterstitialAd? = null

    /** Whether an ad is currently being loaded. */
    private var isLoading = false

    /**
     * Preloads an interstitial ad so it is ready to display.
     *
     * If an ad is already loaded or a load is in progress, this is a no-op.
     * Load failures are logged but do not throw; the next call to [showAd]
     * will simply invoke the dismiss callback immediately.
     *
     * @param context Application or Activity context for the ad request
     */
    fun loadAd(context: Context) {
        if (interstitialAd != null || isLoading) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            AdMobConfig.interstitialAdUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Interstitial ad failed to load: ${error.message}")
                    interstitialAd = null
                    isLoading = false
                }
            },
        )
    }

    /**
     * Shows the preloaded interstitial ad.
     *
     * If no ad is available (not yet loaded or load failed), [onDismissed]
     * is called immediately so the caller can proceed without waiting.
     * After the ad is shown and dismissed, a new ad is automatically
     * preloaded for the next use.
     *
     * @param activity The Activity context required to show the ad
     * @param onDismissed Callback invoked when the ad is dismissed or unavailable
     */
    fun showAd(activity: Activity, onDismissed: () -> Unit) {
        val ad = interstitialAd

        if (ad == null) {
            Log.d(TAG, "No interstitial ad available, proceeding without ad")
            onDismissed()
            // Try to load for next time
            loadAd(activity)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad dismissed")
                interstitialAd = null
                onDismissed()
                // Preload the next ad
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "Interstitial ad failed to show: ${adError.message}")
                interstitialAd = null
                onDismissed()
                // Preload for next time
                loadAd(activity)
            }
        }

        ad.show(activity)
    }
}
