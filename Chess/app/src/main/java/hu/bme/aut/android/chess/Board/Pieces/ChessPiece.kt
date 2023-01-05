package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Tile

abstract class ChessPiece(x: Int, y: Int, playerId: Int) {
    var pos_x: Int = x
    var pos_y: Int = y
    var isAlive: Boolean = true
    val player: Int = playerId
    open var canPathBeBlocked: Boolean = true
    var imagePath: String = ""

    open abstract fun getPossibleMoves()

    open abstract fun step(tile: Tile)

    open abstract fun checkIfValidMove(tile: Tile): Boolean
}