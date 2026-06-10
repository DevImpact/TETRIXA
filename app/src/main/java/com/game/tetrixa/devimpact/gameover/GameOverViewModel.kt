package com.game.tetrixa.devimpact.gameover

import androidx.lifecycle.ViewModel

class GameOverViewModel(
    private val persister: GameOverSessionResultPersister
) : ViewModel() {
    suspend fun persistSessionResultIfNeeded(summary: GameOverSessionSummary): PersistSessionResult {
        return persister.persistSessionResultIfNeeded(summary)
    }
}
