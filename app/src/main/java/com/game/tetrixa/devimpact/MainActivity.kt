package com.game.tetrixa.devimpact

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.game.tetrixa.devimpact.data.AppContainer
import com.game.tetrixa.devimpact.gameover.GameOverActivity
import com.game.tetrixa.devimpact.presentation.TetrixaViewModel
import com.game.tetrixa.devimpact.ui.TetrixaApp
import com.game.tetrixa.devimpact.ui.theme.TetrixaTheme

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer
    private var tetrixaViewModel: TetrixaViewModel? = null
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = AppContainer(applicationContext)
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
        interstitialAdManager = InterstitialAdManager(applicationContext, googleMobileAdsConsentManager)

        googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
            if (consentError != null) {
                Log.w(TAG, "${consentError.errorCode}: ${consentError.message}")
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                interstitialAdManager.initializeAndPreload()
            }
        }

        if (googleMobileAdsConsentManager.canRequestAds) {
            interstitialAdManager.initializeAndPreload()
        }

        enableEdgeToEdge()
        setContent {
            TetrixaTheme {
                val vm: TetrixaViewModel = viewModel(factory = TetrixaViewModel.factory(appContainer))
                tetrixaViewModel = vm
                TetrixaApp(
                    viewModel = vm,
                    activity = this,
                    showInterstitialBeforeNewGame = { onContinue ->
                        interstitialAdManager.showIfAvailable(this, onContinue)
                    },
                    showInterstitialBeforeNextLevel = { completedLevel, onContinue ->
                        interstitialAdManager.showBetweenLevelsIfAllowed(
                            activity = this,
                            completedLevel = completedLevel,
                            onComplete = onContinue
                        )
                    },
                    onGameOver = { score, level, lines ->
                        startActivity(GameOverActivity.newIntent(this, score, level, lines))
                        finish()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tetrixaViewModel?.startFreshGameOnReturnIfNeeded()
    }

    companion object {
        private const val TAG = "MainActivity"

        fun newGameIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}
