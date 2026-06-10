package com.game.tetrixa.devimpact.settings

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.game.tetrixa.devimpact.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    private lateinit var repository: SettingsRepository
    private lateinit var sectionsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        configureEdgeToEdge()
        setContentView(R.layout.activity_settings)

        repository = SettingsRepository(applicationContext)
        sectionsContainer = findViewById(R.id.settingsSections)
        findViewById<MaterialButton>(R.id.settingsBackButton).setOnClickListener { finish() }
        findViewById<ScrollView>(R.id.settingsScroll).viewTreeObserver.addOnScrollChangedListener {
            findViewById<PremiumSettingsBackgroundView>(R.id.settingsBackground)
                .setParallaxOffset(findViewById<ScrollView>(R.id.settingsScroll).scrollY)
        }

        lifecycleScope.launch {
            repository.hapticFeedbackEnabled.collect { enabled ->
                render(enabled)
            }
        }
    }

    private fun configureEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }

    private fun render(hapticEnabled: Boolean) {
        SettingsAdapter(
            sectionsContainer,
            listOf(
                SettingsSection(
                    R.string.settings_comfort_title,
                    listOf(
                        SettingsItem.Toggle(
                            id = "haptic_feedback",
                            titleRes = R.string.settings_haptic_title,
                            summaryRes = R.string.settings_haptic_summary,
                            icon = getString(R.string.icon_settings_haptic),
                            isChecked = hapticEnabled,
                            onChanged = { enabled -> lifecycleScope.launch { repository.setHapticFeedbackEnabled(enabled) } }
                        )
                    )
                ),
                SettingsSection(
                    R.string.settings_legal_title,
                    listOf(
                        SettingsItem.Action(
                            id = "privacy_policy",
                            titleRes = R.string.privacy_title,
                            summaryRes = R.string.settings_privacy_summary,
                            icon = getString(R.string.icon_settings_privacy),
                            onClick = { openActivity(PrivacyPolicyActivity::class.java) }
                        )
                    )
                ),
                SettingsSection(
                    R.string.settings_support_title,
                    listOf(
                        SettingsItem.Action("contact_support", R.string.settings_contact_support, R.string.settings_contact_support_summary, getString(R.string.icon_settings_contact)) {
                            EmailIntentHelper.openSupportEmail(this)
                        },
                        SettingsItem.Action("rate_game", R.string.settings_rate_game, R.string.settings_rate_game_summary, getString(R.string.icon_settings_rate)) {
                            PlayStoreHelper.openGameListing(this)
                        },
                        SettingsItem.Action("about_studio", R.string.settings_about_studio, R.string.settings_about_studio_summary, getString(R.string.icon_settings_about)) {
                            openActivity(AboutActivity::class.java)
                        }
                    )
                ),
                SettingsSection(
                    R.string.settings_about_title,
                    listOf(
                        SettingsItem.Info("game_version", getString(R.string.settings_version_format, VersionHelper.versionName), getString(R.string.settings_copyright), getString(R.string.icon_settings_info)),
                        SettingsItem.Info("developer", getString(R.string.settings_developer), getString(R.string.settings_support_email), getString(R.string.icon_settings_developer))
                    )
                )
            )
        ).render()
    }

    private fun openActivity(activityClass: Class<out Activity>) {
        startActivity(Intent(this, activityClass))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
