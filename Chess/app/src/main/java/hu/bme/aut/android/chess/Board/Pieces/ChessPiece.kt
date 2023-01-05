package hu.bme.aut.android.chess.Board.Pieces

abstract class ChessPiece(x: Int, y: Int, playerId: Int) {
    var pos_x: Int = x
    var pos_y: Int = y
    var isAlive: Boolean = true
    val player: Int = playerId

    open fun getPossibleMoves(){}

    open fun step(){}
}