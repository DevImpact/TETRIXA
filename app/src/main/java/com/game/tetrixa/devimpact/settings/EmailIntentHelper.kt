package com.game.tetrixa.devimpact.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.game.tetrixa.devimpact.R

object EmailIntentHelper {
    fun openSupportEmail(activity: Activity) {
        val email = activity.getString(R.string.settings_support_email)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.settings_email_subject))
            putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.settings_email_body))
        }
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            Toast.makeText(activity, R.string.settings_no_email_app, Toast.LENGTH_SHORT).show()
        }
    }
}
