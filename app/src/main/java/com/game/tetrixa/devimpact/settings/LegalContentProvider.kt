package com.game.tetrixa.devimpact.settings

import android.content.Context
import androidx.annotation.StringRes
import com.game.tetrixa.devimpact.R

data class LegalSection(
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int
) {
    fun title(context: Context): String = context.getString(titleRes)
    fun body(context: Context): String = context.getString(bodyRes)
}

object LegalContentProvider {
    val privacyPolicySections: List<LegalSection> = listOf(
        LegalSection(R.string.privacy_intro_title, R.string.privacy_intro_text),
        LegalSection(R.string.privacy_data_collection_title, R.string.privacy_data_collection_text),
        LegalSection(R.string.privacy_processing_title, R.string.privacy_processing_text),
        LegalSection(R.string.privacy_ads_title, R.string.privacy_ads_text),
        LegalSection(R.string.privacy_sharing_title, R.string.privacy_sharing_text),
        LegalSection(R.string.privacy_gdpr_title, R.string.privacy_gdpr_text),
        LegalSection(R.string.privacy_retention_title, R.string.privacy_retention_text),
        LegalSection(R.string.privacy_security_title, R.string.privacy_security_text),
        LegalSection(R.string.privacy_transfer_title, R.string.privacy_transfer_text),
        LegalSection(R.string.privacy_rights_title, R.string.privacy_rights_text),
        LegalSection(R.string.privacy_delete_title, R.string.privacy_delete_text),
        LegalSection(R.string.privacy_children_title, R.string.privacy_children_text),
        LegalSection(R.string.privacy_changes_title, R.string.privacy_changes_text),
        LegalSection(R.string.privacy_contact_title, R.string.privacy_contact_text)
    )
}
