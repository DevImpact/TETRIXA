package com.game.tetrixa.devimpact.settings

import androidx.annotation.StringRes

sealed class SettingsItem(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val summaryRes: Int,
    val icon: String
) {
    class Toggle(
        id: String,
        @StringRes titleRes: Int,
        @StringRes summaryRes: Int,
        icon: String,
        val isChecked: Boolean,
        val onChanged: (Boolean) -> Unit
    ) : SettingsItem(id, titleRes, summaryRes, icon)

    class Action(
        id: String,
        @StringRes titleRes: Int,
        @StringRes summaryRes: Int,
        icon: String,
        val onClick: () -> Unit
    ) : SettingsItem(id, titleRes, summaryRes, icon)

    class Info(
        id: String,
        val title: String,
        val summary: String,
        icon: String
    ) : SettingsItem(id, 0, 0, icon)
}

data class SettingsSection(
    @StringRes val titleRes: Int,
    val items: List<SettingsItem>
)
