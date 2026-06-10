package com.game.tetrixa.devimpact.data

import com.game.tetrixa.devimpact.domain.model.Cell
import com.game.tetrixa.devimpact.domain.model.Piece
import com.game.tetrixa.devimpact.domain.model.TetrixaGameState
import com.game.tetrixa.devimpact.domain.model.TetrominoType
import com.game.tetrixa.devimpact.domain.repository.GameRepository
import com.game.tetrixa.devimpact.domain.usecase.GameProgressionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class InMemoryGameRepository(
    private val progression: GameProgressionUseCase = GameProgressionUseCase()
) : GameRepository {

    private val _gameState = MutableStateFlow(TetrixaGameState())
    override val gameState: StateFlow<TetrixaGameState> = _gameState.asStateFlow()

    override fun startNewGame() {
        val first = randomType(1)
        val next = randomType(1)
        val initial = TetrixaGameState(
            activePiece = Piece(first, 0, Cell(3, 0)),
            nextPiece = next
        )
        _gameState.value = if (canPlace(initial.grid, initial.activePiece!!, initial.width, initial.height)) {
            initial
        } else {
            initial.copy(isGameOver = true)
        }
    }

    override fun tick() {
        if (_gameState.value.isPaused) return
        advancePiece(forceLock = false)
    }

    override fun softDrop() {
        if (_gameState.value.isPaused) return
        advancePiece(forceLock = false)
    }

    override fun hardDrop() {
        val state = _gameState.value
        val active = state.activePiece ?: return
        if (state.isGameOver || state.isPaused) return

        var falling = active
        while (true) {
            val candidate = falling.copy(position = falling.position.copy(y = falling.position.y + 1))
            if (canPlace(state.grid, candidate, state.width, state.height)) {
                falling = candidate
            } else {
                _gameState.value = state.copy(activePiece = falling)
                advancePiece(forceLock = true)
                break
            }
        }
    }

    override fun moveLeft() = moveBy(-1)

    override fun moveRight() = moveBy(1)

    override fun rotate() {
        val state = _gameState.value
        val active = state.activePiece ?: return
        if (state.isGameOver || state.isPaused) return
        val rotated = active.copy(rotation = (active.rotation + 1) % active.type.rotations.size)
        if (canPlace(state.grid, rotated, state.width, state.height)) {
            _gameState.value = state.copy(activePiece = rotated)
        }
    }

    override fun togglePause() {
        _gameState.value = _gameState.value.copy(isPaused = !_gameState.value.isPaused)
    }

    override fun quitGame() {
        _gameState.value = _gameState.value.copy(isGameOver = true, isPaused = false)
    }

    override fun consumeSlowFallBoost() {
        _gameState.value = _gameState.value.copy(slowFallTicksLeft = 40)
    }

    override fun activateExplosion() {
        _gameState.value = _gameState.value.copy(isExplosionReady = true)
    }

    private fun moveBy(dx: Int) {
        val state = _gameState.value
        val active = state.activePiece ?: return
        if (state.isGameOver || state.isPaused) return
        val moved = active.copy(position = active.position.copy(x = active.position.x + dx))
        if (canPlace(state.grid, moved, state.width, state.height)) {
            _gameState.value = state.copy(activePiece = moved)
        }
    }

    private fun advancePiece(forceLock: Boolean) {
        val state = _gameState.value
        val active = state.activePiece ?: return
        if (state.isGameOver) return

        val dropped = active.copy(position = active.position.copy(y = active.position.y + 1))
        if (!forceLock && canPlace(state.grid, dropped, state.width, state.height)) {
            _gameState.value = state.copy(
                activePiece = dropped,
                slowFallTicksLeft = (state.slowFallTicksLeft - 1).coerceAtLeast(0)
            )
            return
        }

        var mutableGrid = state.grid.map { it.toMutableList() }
        active.cells().forEach { cell ->
            if (cell.y in mutableGrid.indices && cell.x in 0 until state.width) {
                mutableGrid[cell.y][cell.x] = active.type.color
            }
        }

        var fullRows = mutableGrid.count { row -> row.all { it != null } }
        if (fullRows >= 1 && state.isExplosionReady) {
            mutableGrid = MutableList(state.height) { MutableList(state.width) { null } }
            fullRows = state.height
        } else {
            mutableGrid = mutableGrid.filterNot { row -> row.all { it != null } }.toMutableList()
            repeat(fullRows) {
                mutableGrid.add(0, MutableList(state.width) { null })
            }
        }

        val newClearedLines = state.clearedLines + fullRows
        val newLevel = progression.levelFromClearedLines(newClearedLines)
        val scored = state.score + progression.scoreForClear(
            fullRows,
            newLevel,
            false
        )

        val obstacleApplied = newLevel >= 3 && newLevel % 3 == 0 && fullRows == 0
        if (obstacleApplied) {
            applyObstacleRow(mutableGrid, state.width)
        }

        val spawnedPiece = Piece(state.nextPiece, 0, Cell(3, 0))
        val next = randomType(newLevel)
        val canSpawn = canPlace(mutableGrid, spawnedPiece, state.width, state.height)

        _gameState.value = state.copy(
            grid = mutableGrid.map { it.toList() },
            activePiece = if (canSpawn) spawnedPiece else null,
            nextPiece = next,
            level = newLevel,
            score = scored,
            clearedLines = newClearedLines,
            isGameOver = !canSpawn,
            pendingRevive = !canSpawn,
            slowFallTicksLeft = (state.slowFallTicksLeft - 1).coerceAtLeast(0),
            isExplosionReady = if (fullRows >= 1 && state.isExplosionReady) false else state.isExplosionReady
        )
    }

    private fun canPlace(grid: List<List<Long?>>, piece: Piece, width: Int, height: Int): Boolean {
        return piece.cells().all { cell ->
            cell.x in 0 until width &&
                cell.y in 0 until height &&
                grid[cell.y][cell.x] == null
        }
    }

    private fun applyObstacleRow(grid: MutableList<MutableList<Long?>>, width: Int) {
        if (grid.isEmpty()) return
        grid.removeAt(0)
        val hole = Random.nextInt(width)
        val row = MutableList<Long?>(width) { 0xFF455A64 }
        row[hole] = null
        grid.add(row)
    }

    private fun randomType(level: Int): TetrominoType {
        val available = TetrominoType.entries.filter { it.requiredLevel <= level }
        return available.random()
    }
}
