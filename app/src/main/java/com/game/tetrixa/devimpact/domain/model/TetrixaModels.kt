package com.game.tetrixa.devimpact.domain.model

import kotlin.math.max

data class Cell(val x: Int, val y: Int)

enum class ShopItemType {
    SLOW_FALL,
    EXPLOSION
}

data class ShopItem(
    val type: ShopItemType,
    val displayName: String,
    val description: String,
    val price: Int
)

data class Piece(
    val type: TetrominoType,
    val rotation: Int,
    val position: Cell
) {
    fun cells(): List<Cell> = type.rotations[rotation].map { Cell(it.x + position.x, it.y + position.y) }
}

enum class TetrominoType(val color: Long, val rotations: List<List<Cell>>, val requiredLevel: Int = 1) {
    I(
        0xFF4DD0E1,
        listOf(
            listOf(Cell(0, 1), Cell(1, 1), Cell(2, 1), Cell(3, 1)),
            listOf(Cell(2, 0), Cell(2, 1), Cell(2, 2), Cell(2, 3))
        )
    ),
    O(
        0xFFFFD54F,
        listOf(
            listOf(Cell(1, 0), Cell(2, 0), Cell(1, 1), Cell(2, 1))
        )
    ),
    T(
        0xFFBA68C8,
        listOf(
            listOf(Cell(1, 0), Cell(0, 1), Cell(1, 1), Cell(2, 1)),
            listOf(Cell(1, 0), Cell(1, 1), Cell(2, 1), Cell(1, 2)),
            listOf(Cell(0, 1), Cell(1, 1), Cell(2, 1), Cell(1, 2)),
            listOf(Cell(1, 0), Cell(0, 1), Cell(1, 1), Cell(1, 2))
        )
    ),
    L(
        0xFFFF8A65,
        listOf(
            listOf(Cell(2, 0), Cell(0, 1), Cell(1, 1), Cell(2, 1)),
            listOf(Cell(1, 0), Cell(1, 1), Cell(1, 2), Cell(2, 2)),
            listOf(Cell(0, 1), Cell(1, 1), Cell(2, 1), Cell(0, 2)),
            listOf(Cell(0, 0), Cell(1, 0), Cell(1, 1), Cell(1, 2))
        )
    ),
    J(
        0xFF64B5F6,
        listOf(
            listOf(Cell(0, 0), Cell(0, 1), Cell(1, 1), Cell(2, 1)),
            listOf(Cell(1, 0), Cell(2, 0), Cell(1, 1), Cell(1, 2)),
            listOf(Cell(0, 1), Cell(1, 1), Cell(2, 1), Cell(2, 2)),
            listOf(Cell(1, 0), Cell(1, 1), Cell(0, 2), Cell(1, 2))
        )
    ),
    S(
        0xFF81C784,
        listOf(
            listOf(Cell(1, 0), Cell(2, 0), Cell(0, 1), Cell(1, 1)),
            listOf(Cell(1, 0), Cell(1, 1), Cell(2, 1), Cell(2, 2))
        )
    ),
    Z(
        0xFFE57373,
        listOf(
            listOf(Cell(0, 0), Cell(1, 0), Cell(1, 1), Cell(2, 1)),
            listOf(Cell(2, 0), Cell(1, 1), Cell(2, 1), Cell(1, 2))
        )
    ),
    DOT(
        0xFFE91E63,
        listOf(listOf(Cell(0, 0))),
        2
    ),
    BAR_2(
        0xFF9C27B0,
        listOf(
            listOf(Cell(0, 0), Cell(1, 0)),
            listOf(Cell(0, 0), Cell(0, 1))
        ),
        3
    ),
    PLUS(
        0xFFFF9800,
        listOf(
            listOf(Cell(1, 0), Cell(0, 1), Cell(1, 1), Cell(2, 1), Cell(1, 2))
        ),
        4
    ),
    BRACKET(
        0xFF795548,
        listOf(
            listOf(Cell(0, 0), Cell(1, 0), Cell(2, 0), Cell(0, 1), Cell(2, 1)),
            listOf(Cell(0, 0), Cell(1, 0), Cell(1, 1), Cell(1, 2), Cell(0, 2)),
            listOf(Cell(0, 0), Cell(2, 0), Cell(0, 1), Cell(1, 1), Cell(2, 1)),
            listOf(Cell(0, 0), Cell(1, 0), Cell(0, 1), Cell(0, 2), Cell(1, 2))
        ),
        5
    ),
    SMALL_T(
        0xFFCDDC39,
        listOf(
            listOf(Cell(1, 0), Cell(0, 1), Cell(1, 1)),
            listOf(Cell(0, 0), Cell(0, 1), Cell(1, 1)),
            listOf(Cell(0, 0), Cell(1, 0), Cell(0, 1)),
            listOf(Cell(0, 0), Cell(1, 0), Cell(1, 1))
        ),
        6
    )
}

data class TetrixaGameState(
    val width: Int = 10,
    val height: Int = 20,
    val grid: List<List<Long?>> = List(20) { List<Long?>(10) { null } },
    val activePiece: Piece? = null,
    val score: Int = 0,
    val clearedLines: Int = 0,
    val level: Int = 1,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val pendingRevive: Boolean = false,
    val nextPiece: TetrominoType = TetrominoType.I,
    val slowFallTicksLeft: Int = 0,
    val isExplosionReady: Boolean = false
) {
    val effectiveTickMillis: Long
        get() {
            val base = max(120L, 900L - ((level - 1) * 75L))
            return if (slowFallTicksLeft > 0) base + 250L else base
        }
}

data class WalletState(
    val coins: Int = 0,
    val inventory: Map<ShopItemType, Int> = emptyMap()
)
