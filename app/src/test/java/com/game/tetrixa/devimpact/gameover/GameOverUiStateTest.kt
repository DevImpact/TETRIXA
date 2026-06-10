package com.game.tetrixa.devimpact.gameover

import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class GameOverUiStateTest {
    private val previousLocale: Locale = Locale.getDefault()

    @After
    fun tearDown() {
        Locale.setDefault(previousLocale)
    }

    @Test
    fun fromIntentValues_formatsScoreLevelAndLinesForUi() {
        Locale.setDefault(Locale.US)

        val summary = GameOverSessionSummary.fromIntentValues(
            score = 123_456,
            level = 12,
            lines = 340
        )
        val uiState = GameOverUiState.fromSummary(summary)

        assertEquals("123,456", uiState.finalScoreText)
        assertEquals("12", uiState.finalLevelText)
        assertEquals("340", uiState.finalLinesText)
    }

    @Test
    fun fromIntentValues_usesSafeFallbacksForMissingValues() {
        val summary = GameOverSessionSummary.fromIntentValues(
            score = null,
            level = null,
            lines = null
        )

        assertEquals(GameOverSessionSummary(score = 0, level = 1, lines = 0), summary)
    }
}
