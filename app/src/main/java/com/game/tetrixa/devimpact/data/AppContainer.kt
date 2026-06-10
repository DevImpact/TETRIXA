package com.game.tetrixa.devimpact.data

import android.content.Context
import com.game.tetrixa.devimpact.domain.repository.GameRepository
import com.game.tetrixa.devimpact.domain.repository.ShopRepository

class AppContainer(context: Context) {
    val shopRepository: ShopRepository = InMemoryShopRepository(context.applicationContext)
    val gameRepository: GameRepository = InMemoryGameRepository()
}
