package com.game.tetrixa.devimpact.domain.usecase

import kotlin.math.max

class GameProgressionUseCase {
    fun levelFromClearedLines(clearedLines: Int): Int = (clearedLines / 10) + 1

    fun scoreForClear(lines: Int, level: Int, scoreBoostEnabled: Boolean): Int {
        val base = when (lines) {
            1 -> 100
            2 -> 300
            3 -> 500
            4 -> 800
            else -> 0
        }
        val boostMultiplier = if (scoreBoostEnabled) 1.5 else 1.0
        return max(0, (base * level * boostMultiplier).toInt())
    }

    fun coinsForLevel(level: Int): Int = 5 + (level * 2)
    fun coinsForLineClear(lines: Int): Int = lines * 3
}
