package com.game.tetrixa.devimpact.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.game.tetrixa.devimpact.R

object PlayStoreHelper {
    fun openGameListing(activity: Activity) {
        val packageName = activity.packageName
        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        }
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))

        when {
            marketIntent.resolveActivity(activity.packageManager) != null -> activity.startActivity(marketIntent)
            webIntent.resolveActivity(activity.packageManager) != null -> activity.startActivity(webIntent)
            else -> Toast.makeText(activity, R.string.settings_play_store_unavailable, Toast.LENGTH_SHORT).show()
        }
    }
}
