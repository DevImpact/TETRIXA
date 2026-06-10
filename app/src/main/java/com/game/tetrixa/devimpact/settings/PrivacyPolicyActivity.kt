package com.game.tetrixa.devimpact.settings

import android.app.Activity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.WindowCompat
import com.game.tetrixa.devimpact.R
import com.google.android.material.color.DynamicColors

class PrivacyPolicyActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContentView(R.layout.activity_privacy_policy)
        renderPrivacyPolicy()
    }

    private fun renderPrivacyPolicy() {
        val content = findViewById<LinearLayout>(R.id.privacyContent)
        content.addView(TextView(this).apply {
            setText(R.string.privacy_title)
            setTextAppearance(R.style.TextAppearance_Game_Settings_LegalTitle)
            textSize = 28f
        })
        LegalContentProvider.privacyPolicySections.forEach { section ->
            content.addView(TextView(this).apply {
                text = section.title(this@PrivacyPolicyActivity)
                setTextAppearance(R.style.TextAppearance_Game_Settings_LegalTitle)
                setPadding(0, 24.dp(), 0, 6.dp())
            })
            content.addView(TextView(this).apply {
                text = section.body(this@PrivacyPolicyActivity)
                setTextAppearance(R.style.TextAppearance_Game_Settings_LegalBody)
                movementMethod = LinkMovementMethod.getInstance()
            })
        }
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()
}
