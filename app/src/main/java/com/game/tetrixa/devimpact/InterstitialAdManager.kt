package com.game.tetrixa.devimpact

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Production-safe wrapper around Google Mobile Ads interstitial loading and display.
 *
 * Uses production AdMob IDs while preserving the existing consent-gated initialization,
 * preload, callback, and frequency behavior.
 */
class InterstitialAdManager(
    private val context: Context,
    private val consentManager: GoogleMobileAdsConsentManager
) {
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var interstitialAd: InterstitialAd? = null
    private var adIsLoading = false

    fun initializeAndPreload() {
        if (!consentManager.canRequestAds) return
        if (isMobileAdsInitializeCalled.getAndSet(true)) return

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(context) {}
            mainHandler.post { loadAd() }
        }
    }

    fun loadAd() {
        if (!consentManager.canRequestAds || adIsLoading || interstitialAd != null) return

        adIsLoading = true
        InterstitialAd.load(
            context,
            AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.w(TAG, "Interstitial failed to load: ${adError.code} ${adError.message}")
                    interstitialAd = null
                    adIsLoading = false
                }
            }
        )
    }

    fun showBetweenLevelsIfAllowed(
        activity: Activity,
        completedLevel: Int,
        onComplete: () -> Unit
    ) {
        if (!AdPolicy.shouldShowInterstitialAfterCompletedLevel(completedLevel)) {
            onComplete()
            loadAd()
            return
        }

        showIfAvailable(activity, onComplete)
    }

    fun showIfAvailable(activity: Activity, onComplete: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            onComplete()
            loadAd()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial dismissed.")
                interstitialAd = null
                onComplete()
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "Interstitial failed to show: ${adError.code} ${adError.message}")
                interstitialAd = null
                onComplete()
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial shown.")
            }

            override fun onAdImpression() {
                Log.d(TAG, "Interstitial impression recorded.")
            }

            override fun onAdClicked() {
                Log.d(TAG, "Interstitial clicked.")
            }
        }

        ad.show(activity)
    }

    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-7977505325397665/1415221963"
        private const val TAG = "InterstitialAdManager"
    }
}

object AdPolicy {
    private const val SHOW_INTERSTITIAL_EVERY_N_LEVELS = 2

    fun shouldShowInterstitialAfterCompletedLevel(completedLevel: Int): Boolean {
        return completedLevel > 0 && (completedLevel - 1) % SHOW_INTERSTITIAL_EVERY_N_LEVELS == 0
    }
}
