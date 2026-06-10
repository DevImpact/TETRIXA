package com.game.tetrixa.devimpact.domain.repository

import com.game.tetrixa.devimpact.domain.model.ShopItem
import com.game.tetrixa.devimpact.domain.model.ShopItemType
import com.game.tetrixa.devimpact.domain.model.WalletState
import kotlinx.coroutines.flow.StateFlow

interface ShopRepository {
    val walletState: StateFlow<WalletState>
    val items: List<ShopItem>

    fun addCoins(amount: Int)
    fun spendCoins(amount: Int): Boolean
    fun buyItem(itemType: ShopItemType): Boolean
    fun consumeOwnedItem(itemType: ShopItemType): Boolean
}
