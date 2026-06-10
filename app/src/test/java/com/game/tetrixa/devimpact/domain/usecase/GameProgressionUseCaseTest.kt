package com.game.tetrixa.devimpact.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class GameProgressionUseCaseTest {

    private val useCase = GameProgressionUseCase()

    @Test
    fun `level increases every 10 lines`() {
        assertEquals(1, useCase.levelFromClearedLines(0))
        assertEquals(2, useCase.levelFromClearedLines(10))
        assertEquals(4, useCase.levelFromClearedLines(35))
    }

    @Test
    fun `score boost increases score outcome`() {
        val base = useCase.scoreForClear(lines = 2, level = 3, scoreBoostEnabled = false)
        val boosted = useCase.scoreForClear(lines = 2, level = 3, scoreBoostEnabled = true)
        assertEquals(900, base)
        assertEquals(1350, boosted)
    }
}
