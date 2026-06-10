package com.game.tetrixa.devimpact.domain.repository

import com.game.tetrixa.devimpact.domain.model.TetrixaGameState
import kotlinx.coroutines.flow.StateFlow

interface GameRepository {
    val gameState: StateFlow<TetrixaGameState>

    fun startNewGame()
    fun tick()
    fun moveLeft()
    fun moveRight()
    fun softDrop()
    fun hardDrop()
    fun rotate()
    fun togglePause()
    fun quitGame()
    fun consumeSlowFallBoost()
    fun activateExplosion()
}
