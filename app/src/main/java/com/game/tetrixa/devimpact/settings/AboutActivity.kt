package com.game.tetrixa.devimpact.settings

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.core.view.WindowCompat
import com.game.tetrixa.devimpact.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors

class AboutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContentView(R.layout.activity_about)

        findViewById<TextView>(R.id.aboutVersion).text = getString(R.string.settings_version_format, VersionHelper.versionName)
        findViewById<MaterialButton>(R.id.aboutCloseButton).setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        findViewById<android.view.View>(R.id.aboutPanel).apply {
            alpha = 0f
            scaleX = 0.94f
            scaleY = 0.94f
            animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(320L).start()
        }
    }
}
