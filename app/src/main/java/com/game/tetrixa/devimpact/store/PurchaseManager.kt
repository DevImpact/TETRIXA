package com.game.tetrixa.devimpact.store

import android.content.Context
import com.game.tetrixa.devimpact.EconomyPrefs
import kotlin.math.max

class PurchaseManager(context: Context) {
    private val prefs = context.getSharedPreferences(EconomyPrefs.PREFS_NAME, Context.MODE_PRIVATE)

    var coins: Int
        private set(value) = prefs.edit().putInt(EconomyPrefs.KEY_COINS, value).apply()
        get() = prefs.getInt(EconomyPrefs.KEY_COINS, EconomyPrefs.STARTER_COINS)

    val lives: Int get() = prefs.getInt(EconomyPrefs.KEY_LIVES, 5)
    val boosters: Int get() = prefs.getInt(EconomyPrefs.KEY_BOOSTERS, 2)

    fun purchase(item: StoreItem): PurchaseResult {
        val cooldownRemaining = purchaseCooldownRemainingMillis(item)
        if (cooldownRemaining > 0L) {
            return PurchaseResult.Cooldown(cooldownRemaining)
        }

        val currentCoins = coins
        if (currentCoins < item.priceCoins) {
            return PurchaseResult.Failed(currentCoins)
        }

        val newBalance = currentCoins - item.priceCoins + item.coinReward
        val slowFallReward = (item.boosterReward + 1) / 2
        val explosionReward = item.boosterReward / 2
        prefs.edit()
            .putInt(EconomyPrefs.KEY_COINS, newBalance)
            .putInt(EconomyPrefs.KEY_LIVES, lives + item.livesReward)
            .putInt(EconomyPrefs.KEY_BOOSTERS, boosters + item.boosterReward)
            .putInt(
                EconomyPrefs.KEY_SLOW_FALL,
                prefs.getInt(EconomyPrefs.KEY_SLOW_FALL, 0) + slowFallReward
            )
            .putInt(
                EconomyPrefs.KEY_EXPLOSION,
                prefs.getInt(EconomyPrefs.KEY_EXPLOSION, 0) + explosionReward
            )
            .putLong(lastPurchaseKey(item), System.currentTimeMillis())
            .apply()
        return PurchaseResult.Success(newBalance)
    }

    fun purchaseCooldownRemainingMillis(item: StoreItem): Long {
        val lastPurchaseAt = prefs.getLong(lastPurchaseKey(item), 0L)
        if (lastPurchaseAt <= 0L) return 0L

        val nextPurchaseAt = lastPurchaseAt + PURCHASE_COOLDOWN_MILLIS
        return max(0L, nextPurchaseAt - System.currentTimeMillis())
    }

    private fun lastPurchaseKey(item: StoreItem): String = "${EconomyPrefs.KEY_LAST_PURCHASE_PREFIX}${item.id}"

    companion object {
        const val PURCHASE_COOLDOWN_MILLIS = 12L * 60L * 60L * 1000L
    }
}

sealed class PurchaseResult {
    data class Success(val newBalance: Int) : PurchaseResult()
    data class Failed(val currentBalance: Int) : PurchaseResult()
    data class Cooldown(val remainingMillis: Long) : PurchaseResult()
}
