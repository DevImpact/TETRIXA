package com.game.tetrixa.devimpact.store

import androidx.annotation.StringRes

enum class StoreSection(@StringRes val titleRes: Int) {
    Starter(com.game.tetrixa.devimpact.R.string.section_starter_pack),
    Coins(com.game.tetrixa.devimpact.R.string.section_coin_packs),
    Lives(com.game.tetrixa.devimpact.R.string.section_extra_lives),
    Boosters(com.game.tetrixa.devimpact.R.string.section_booster_bundles),
    Limited(com.game.tetrixa.devimpact.R.string.section_limited_time_offer)
}

data class StoreItem(
    val id: String,
    val section: StoreSection,
    @StringRes val iconRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val rewardHighlightRes: Int,
    val priceCoins: Int,
    val oldPriceCoins: Int? = null,
    val bonusPercent: Int? = null,
    @StringRes val badgeRes: Int? = null,
    val coinReward: Int = 0,
    val livesReward: Int = 0,
    val boosterReward: Int = 0
)
