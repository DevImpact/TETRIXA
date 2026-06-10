package com.game.tetrixa.devimpact.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.game.tetrixa.devimpact.data.AppContainer
import com.game.tetrixa.devimpact.domain.model.ShopItemType
import com.game.tetrixa.devimpact.domain.model.TetrixaGameState
import com.game.tetrixa.devimpact.domain.model.WalletState
import com.game.tetrixa.devimpact.domain.repository.GameRepository
import com.game.tetrixa.devimpact.domain.repository.ShopRepository
import com.game.tetrixa.devimpact.domain.usecase.GameProgressionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class TetrixaUiState(
    val game: TetrixaGameState = TetrixaGameState(),
    val wallet: WalletState = WalletState(),
    val currentTab: Int = 0,
    val isLevelSuccess: Boolean = false,
    val successLevel: Int = 0
)

class TetrixaViewModel(
    private val gameRepository: GameRepository,
    private val shopRepository: ShopRepository,
    private val progression: GameProgressionUseCase = GameProgressionUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TetrixaUiState())
    val uiState: StateFlow<TetrixaUiState> = _uiState.asStateFlow()

    val shopItems = shopRepository.items

    private var gameLoopJob: Job? = null
    private var shouldStartFreshGameOnReturn = false
    private var trackedLevel = 1
    private var trackedLines = 0

    init {
        viewModelScope.launch {
            combine(gameRepository.gameState, shopRepository.walletState) { game, wallet ->
                val current = _uiState.value
                TetrixaUiState(
                    game = game,
                    wallet = wallet,
                    currentTab = current.currentTab,
                    isLevelSuccess = current.isLevelSuccess,
                    successLevel = current.successLevel
                )
            }.collect { state ->
                _uiState.value = state
                rewardForProgress(state.game)
            }
        }
        startNewGame()
    }

    fun changeTab(index: Int) {
        _uiState.value = _uiState.value.copy(currentTab = index)
        if (index == GAME_TAB_INDEX) {
            startFreshGameOnReturnIfNeeded()
        } else {
            stopGameUntilPlayerReturns()
        }
    }

    fun openExternalGameScreen() {
        stopGameUntilPlayerReturns()
    }

    fun startFreshGameOnReturnIfNeeded() {
        if (!shouldStartFreshGameOnReturn) return
        shouldStartFreshGameOnReturn = false
        startNewGame()
    }

    fun startNewGame() {
        trackedLevel = 1
        trackedLines = 0
        _uiState.value = _uiState.value.copy(
            isLevelSuccess = false,
            successLevel = 0
        )
        gameRepository.startNewGame()
        startLoop()
    }

    fun moveLeft() = gameRepository.moveLeft()
    fun moveRight() = gameRepository.moveRight()
    fun softDrop() = gameRepository.softDrop()
    fun rotate() = gameRepository.rotate()
    fun hardDrop() = gameRepository.hardDrop()
    fun togglePause() = gameRepository.togglePause()
    fun quitGame() = gameRepository.quitGame()

    fun buyItem(type: ShopItemType) {
        shopRepository.buyItem(type)
    }

    fun useItem(type: ShopItemType) {
        if (!shopRepository.consumeOwnedItem(type)) return

        when (type) {
            ShopItemType.SLOW_FALL -> gameRepository.consumeSlowFallBoost()
            ShopItemType.EXPLOSION -> gameRepository.activateExplosion()
        }
    }

    private fun stopGameUntilPlayerReturns() {
        val game = _uiState.value.game
        shouldStartFreshGameOnReturn = true
        gameLoopJob?.cancel()
        gameLoopJob = null
        if (!game.isGameOver && !game.isPaused) {
            gameRepository.togglePause()
        }
    }

    private fun startLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (true) {
                val uiState = _uiState.value
                if (uiState.game.isGameOver || uiState.game.isPaused || uiState.isLevelSuccess) {
                    delay(200)
                    continue
                }
                delay(uiState.game.effectiveTickMillis)
                gameRepository.tick()
            }
        }
    }

    private fun rewardForProgress(game: TetrixaGameState) {
        val clearedDelta = game.clearedLines - trackedLines
        if (clearedDelta > 0) {
            shopRepository.addCoins(progression.coinsForLineClear(clearedDelta))
            trackedLines = game.clearedLines
        }

        if (game.level > trackedLevel) {
            val newLevel = game.level
            for (level in (trackedLevel + 1)..newLevel) {
                shopRepository.addCoins(progression.coinsForLevel(level))
            }
            showLevelSuccess(trackedLevel)
            trackedLevel = newLevel
        }
    }

    private fun showLevelSuccess(completedLevel: Int) {
        _uiState.value = _uiState.value.copy(
            isLevelSuccess = true,
            successLevel = completedLevel
        )
    }

    fun continueToNextLevel() {
        _uiState.value = _uiState.value.copy(isLevelSuccess = false)
    }

    companion object {
        private const val GAME_TAB_INDEX = 0

        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TetrixaViewModel(
                        gameRepository = container.gameRepository,
                        shopRepository = container.shopRepository
                    ) as T
                }
            }
    }
}
