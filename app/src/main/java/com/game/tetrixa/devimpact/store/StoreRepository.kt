package com.game.tetrixa.devimpact.store

import com.game.tetrixa.devimpact.R

object StoreRepository {
    fun getPacks(): List<StoreItem> = listOf(
        StoreItem(
            id = "starter_pack",
            section = StoreSection.Starter,
            iconRes = R.string.icon_store_starter,
            titleRes = R.string.starter_pack,
            descriptionRes = R.string.starter_pack_desc,
            rewardHighlightRes = R.string.reward_starter_pack,
            priceCoins = 90,
            oldPriceCoins = 150,
            bonusPercent = 60,
            badgeRes = R.string.recommended,
            coinReward = 300,
            livesReward = 3,
            boosterReward = 4
        ),
        StoreItem(
            id = "coins_small",
            section = StoreSection.Coins,
            iconRes = R.string.icon_store_coins_small,
            titleRes = R.string.coins_small_title,
            descriptionRes = R.string.coins_pack_desc,
            rewardHighlightRes = R.string.reward_coins_small,
            priceCoins = 120,
            bonusPercent = 20,
            coinReward = 180
        ),
        StoreItem(
            id = "coins_medium",
            section = StoreSection.Coins,
            iconRes = R.string.icon_store_coins_medium,
            titleRes = R.string.coins_medium_title,
            descriptionRes = R.string.coins_pack_desc,
            rewardHighlightRes = R.string.reward_coins_medium,
            priceCoins = 260,
            oldPriceCoins = 340,
            bonusPercent = 35,
            badgeRes = R.string.most_popular,
            coinReward = 450
        ),
        StoreItem(
            id = "coins_big",
            section = StoreSection.Coins,
            iconRes = R.string.icon_store_coins_big,
            titleRes = R.string.coins_big_title,
            descriptionRes = R.string.coins_pack_desc,
            rewardHighlightRes = R.string.reward_coins_big,
            priceCoins = 520,
            oldPriceCoins = 760,
            bonusPercent = 50,
            coinReward = 1000
        ),
        StoreItem(
            id = "coins_mega",
            section = StoreSection.Coins,
            iconRes = R.string.icon_store_coins_mega,
            titleRes = R.string.coins_mega_title,
            descriptionRes = R.string.coins_pack_desc,
            rewardHighlightRes = R.string.reward_coins_mega,
            priceCoins = 950,
            oldPriceCoins = 1500,
            bonusPercent = 75,
            badgeRes = R.string.best_value,
            coinReward = 2400
        ),
        StoreItem(
            id = "extra_lives",
            section = StoreSection.Lives,
            iconRes = R.string.icon_store_lives,
            titleRes = R.string.extra_lives_title,
            descriptionRes = R.string.lives_pack_desc,
            rewardHighlightRes = R.string.reward_extra_lives,
            priceCoins = 180,
            bonusPercent = 25,
            livesReward = 5
        ),
        StoreItem(
            id = "booster_bundle",
            section = StoreSection.Boosters,
            iconRes = R.string.icon_store_boosters,
            titleRes = R.string.booster_bundle_title,
            descriptionRes = R.string.booster_pack_desc,
            rewardHighlightRes = R.string.reward_booster_bundle,
            priceCoins = 320,
            oldPriceCoins = 460,
            bonusPercent = 45,
            badgeRes = R.string.special_bundle,
            boosterReward = 9
        ),
        StoreItem(
            id = "continue_pack",
            section = StoreSection.Boosters,
            iconRes = R.string.icon_store_continue,
            titleRes = R.string.continue_pack_title,
            descriptionRes = R.string.continue_pack_desc,
            rewardHighlightRes = R.string.reward_continue_pack,
            priceCoins = 210,
            oldPriceCoins = 290,
            badgeRes = R.string.best_deal,
            coinReward = 120,
            livesReward = 2,
            boosterReward = 3
        ),
        StoreItem(
            id = "limited_bundle",
            section = StoreSection.Limited,
            iconRes = R.string.icon_store_limited,
            titleRes = R.string.limited_offer,
            descriptionRes = R.string.limited_offer_desc,
            rewardHighlightRes = R.string.reward_limited_bundle,
            priceCoins = 690,
            oldPriceCoins = 1200,
            bonusPercent = 80,
            badgeRes = R.string.limited_time,
            coinReward = 1800,
            livesReward = 6,
            boosterReward = 12
        )
    )
}
