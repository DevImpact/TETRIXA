package com.game.tetrixa.devimpact.gameover

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

private const val DEFAULT_SCORE = 0
private const val DEFAULT_LEVEL = 1
private const val DEFAULT_LINES = 0

data class GameOverSessionSummary(
    val score: Int,
    val level: Int,
    val lines: Int
) {
    companion object {
        fun fromIntentValues(score: Int?, level: Int?, lines: Int?): GameOverSessionSummary {
            return GameOverSessionSummary(
                score = max(DEFAULT_SCORE, score ?: DEFAULT_SCORE),
                level = max(DEFAULT_LEVEL, level ?: DEFAULT_LEVEL),
                lines = max(DEFAULT_LINES, lines ?: DEFAULT_LINES)
            )
        }
    }
}

data class GameOverUiState(
    val finalScoreText: String,
    val finalLevelText: String,
    val finalLinesText: String
) {
    companion object {
        fun fromSummary(summary: GameOverSessionSummary): GameOverUiState {
            val formatter = NumberFormat.getIntegerInstance(Locale.getDefault())
            return GameOverUiState(
                finalScoreText = formatter.format(summary.score),
                finalLevelText = formatter.format(summary.level),
                finalLinesText = formatter.format(summary.lines)
            )
        }
    }
}
