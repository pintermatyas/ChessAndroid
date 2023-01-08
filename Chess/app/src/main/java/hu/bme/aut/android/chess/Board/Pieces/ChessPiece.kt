package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile

abstract class ChessPiece(x: Int, y: Int, playerId: Int) {
    var posX: Int = x
    var posY: Int = y
    var firstPosX: Int = x
    var firstPosY: Int = y
    var isAlive: Boolean = true
    val player: Int = playerId
    open var canPathBeBlocked: Boolean = true
    var imagePath: String = ""
    open var stepCount: Int = 0

    abstract fun step(tile: Tile?, board: Board)

    abstract fun checkIfValidMove(tile: Tile, board: Board): Boolean

    abstract fun copy(): ChessPiece

    abstract fun isPathBlockedToTile(tile: Tile, board: Board): Boolean

    abstract fun isAttackingTile(tile: Tile, board: Board): Boolean
}