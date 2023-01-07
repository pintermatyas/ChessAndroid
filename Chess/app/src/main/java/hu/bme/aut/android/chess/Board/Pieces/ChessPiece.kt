package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile

abstract class ChessPiece(x: Int, y: Int, playerId: Int) {
    var pos_x: Int = x
    var pos_y: Int = y
    var first_pos_x: Int = x
    var first_pos_y: Int = y
    var isAlive: Boolean = true
    val player: Int = playerId
    open var canPathBeBlocked: Boolean = true
    var imagePath: String = ""
    var stepCount = 0

    abstract fun getPossibleMoves()

    abstract fun step(tile: Tile?, board: Board)

    abstract fun checkIfValidMove(tile: Tile, board: Board): Boolean

    abstract fun copy(): ChessPiece

    abstract fun isPathBlockedToTile(tile: Tile, board: Board): Boolean

    abstract fun isAttackingKingOn(tile: Tile, board: Board): Boolean
}