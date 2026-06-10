package com.game.tetrixa.devimpact.gameover

import android.content.Context
import android.content.SharedPreferences
import com.game.tetrixa.devimpact.EconomyPrefs
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

interface GameOverSessionStorage {
    fun readBestScore(): Int
    fun writeBestScore(score: Int)
    fun readCredits(): Int
    fun writeCredits(credits: Int)
}

class SharedPreferencesGameOverSessionStorage(context: Context) : GameOverSessionStorage {
    private val prefs: SharedPreferences = context.getSharedPreferences(EconomyPrefs.PREFS_NAME, Context.MODE_PRIVATE)

    override fun readBestScore(): Int = prefs.getInt(EconomyPrefs.KEY_BEST_SCORE, 0)

    override fun writeBestScore(score: Int) {
        prefs.edit().putInt(EconomyPrefs.KEY_BEST_SCORE, score).apply()
    }

    override fun readCredits(): Int = prefs.getInt(EconomyPrefs.KEY_COINS, EconomyPrefs.STARTER_COINS)

    override fun writeCredits(credits: Int) {
        prefs.edit().putInt(EconomyPrefs.KEY_COINS, credits).apply()
    }
}

class GameOverSessionResultPersister(
    private val storage: GameOverSessionStorage,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val didPersistSessionResult = AtomicBoolean(false)

    suspend fun persistSessionResultIfNeeded(summary: GameOverSessionSummary): PersistSessionResult {
        if (!didPersistSessionResult.compareAndSet(false, true)) {
            return PersistSessionResult.AlreadyPersisted
        }

        return withContext(ioDispatcher) {
            val previousBestScore = storage.readBestScore()
            val bestScore = max(previousBestScore, summary.score)
            if (bestScore != previousBestScore) {
                storage.writeBestScore(bestScore)
            }

            val creditsReward = summary.score / CREDIT_SCORE_DIVISOR
            val updatedCredits = storage.readCredits() + creditsReward
            storage.writeCredits(updatedCredits)

            PersistSessionResult.Persisted(
                bestScore = bestScore,
                creditsReward = creditsReward,
                totalCredits = updatedCredits
            )
        }
    }

    companion object {
        const val CREDIT_SCORE_DIVISOR = 100
    }
}

sealed class PersistSessionResult {
    data class Persisted(
        val bestScore: Int,
        val creditsReward: Int,
        val totalCredits: Int
    ) : PersistSessionResult()

    data object AlreadyPersisted : PersistSessionResult()
}
