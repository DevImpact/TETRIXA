package com.game.tetrixa.devimpact.gameover

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameOverSessionResultPersisterTest {
    @Test
    fun persistSessionResultIfNeeded_isIdempotentForRepeatedCalls() = runBlocking {
        val storage = FakeGameOverSessionStorage(bestScore = 500, credits = 10)
        val persister = GameOverSessionResultPersister(storage, Dispatchers.Unconfined)
        val summary = GameOverSessionSummary(score = 1_250, level = 4, lines = 30)

        val firstResult = persister.persistSessionResultIfNeeded(summary)
        val secondResult = persister.persistSessionResultIfNeeded(summary)

        assertTrue(firstResult is PersistSessionResult.Persisted)
        assertTrue(secondResult is PersistSessionResult.AlreadyPersisted)
        assertEquals(1_250, storage.bestScore)
        assertEquals(22, storage.credits)
        assertEquals(1, storage.bestScoreWrites)
        assertEquals(1, storage.creditWrites)
    }

    private class FakeGameOverSessionStorage(
        var bestScore: Int,
        var credits: Int
    ) : GameOverSessionStorage {
        var bestScoreWrites = 0
        var creditWrites = 0

        override fun readBestScore(): Int = bestScore

        override fun writeBestScore(score: Int) {
            bestScore = score
            bestScoreWrites++
        }

        override fun readCredits(): Int = credits

        override fun writeCredits(credits: Int) {
            this.credits = credits
            creditWrites++
        }
    }
}
