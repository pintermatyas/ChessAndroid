package hu.bme.aut.android.chess.Board

import hu.bme.aut.android.chess.Board.Pieces.ChessPiece

class Tile(x: Int,y: Int) {
    var x_coord: Int = x
    var y_coord: Int = y
    var isEmpty: Boolean = false
    var chessPiece: ChessPiece? = null
    var tileName: String = (('A'.code +  x_coord).toChar()).toString() + (y_coord+1).toString()

}