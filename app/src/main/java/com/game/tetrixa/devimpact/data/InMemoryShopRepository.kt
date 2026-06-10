package com.game.tetrixa.devimpact.data

import android.content.Context
import android.content.SharedPreferences
import com.game.tetrixa.devimpact.EconomyPrefs
import com.game.tetrixa.devimpact.domain.model.ShopItem
import com.game.tetrixa.devimpact.domain.model.ShopItemType
import com.game.tetrixa.devimpact.domain.model.WalletState
import com.game.tetrixa.devimpact.domain.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryShopRepository(context: Context) : ShopRepository {
    private val prefs = context.getSharedPreferences(EconomyPrefs.PREFS_NAME, Context.MODE_PRIVATE)
    private val _wallet = MutableStateFlow(readWallet())

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key != null && key in walletKeys) {
            _wallet.value = readWallet()
        }
    }

    override val walletState: StateFlow<WalletState> = _wallet.asStateFlow()

    override val items: List<ShopItem> = listOf(
        ShopItem(ShopItemType.SLOW_FALL, "", "", 40),
        ShopItem(ShopItemType.EXPLOSION, "", "", 100)
    )

    init {
        prefs.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun addCoins(amount: Int) {
        if (amount <= 0) return
        updateCoins(_wallet.value.coins + amount)
    }

    override fun spendCoins(amount: Int): Boolean {
        val current = _wallet.value
        if (amount <= 0 || current.coins < amount) return false
        updateCoins(current.coins - amount)
        return true
    }

    override fun buyItem(itemType: ShopItemType): Boolean {
        val item = items.firstOrNull { it.type == itemType } ?: return false
        if (!spendCoins(item.price)) return false

        val currentInventory = _wallet.value.inventory
        val currentCount = currentInventory[itemType] ?: 0
        updateInventory(itemType, currentCount + 1)
        return true
    }

    override fun consumeOwnedItem(itemType: ShopItemType): Boolean {
        val count = _wallet.value.inventory[itemType] ?: 0
        if (count <= 0) return false

        updateInventory(itemType, count - 1)
        return true
    }

    private fun updateCoins(coins: Int) {
        prefs.edit()
            .putInt(EconomyPrefs.KEY_COINS, coins)
            .apply()
        _wallet.value = readWallet()
    }

    private fun updateInventory(itemType: ShopItemType, count: Int) {
        val key = itemType.preferenceKey()
        val editor = prefs.edit()
        if (count <= 0) {
            editor.remove(key)
        } else {
            editor.putInt(key, count)
        }
        editor.apply()
        _wallet.value = readWallet()
    }

    private fun readWallet(): WalletState {
        val inventory = buildMap {
            val slowFallCount = prefs.getInt(EconomyPrefs.KEY_SLOW_FALL, 0)
            if (slowFallCount > 0) put(ShopItemType.SLOW_FALL, slowFallCount)

            val explosionCount = prefs.getInt(EconomyPrefs.KEY_EXPLOSION, 0)
            if (explosionCount > 0) put(ShopItemType.EXPLOSION, explosionCount)
        }

        return WalletState(
            coins = prefs.getInt(EconomyPrefs.KEY_COINS, EconomyPrefs.STARTER_COINS),
            inventory = inventory
        )
    }

    private fun ShopItemType.preferenceKey(): String = when (this) {
        ShopItemType.SLOW_FALL -> EconomyPrefs.KEY_SLOW_FALL
        ShopItemType.EXPLOSION -> EconomyPrefs.KEY_EXPLOSION
    }

    private companion object {
        val walletKeys = setOf(
            EconomyPrefs.KEY_COINS,
            EconomyPrefs.KEY_SLOW_FALL,
            EconomyPrefs.KEY_EXPLOSION
        )
    }
}
