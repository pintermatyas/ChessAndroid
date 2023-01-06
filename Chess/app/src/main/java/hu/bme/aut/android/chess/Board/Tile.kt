package hu.bme.aut.android.chess.Board

import hu.bme.aut.android.chess.Board.Pieces.ChessPiece

class Tile(x: Int,y: Int) {
    var x_coord: Int = x
    var y_coord: Int = y
    var isEmpty: Boolean = false
    var chessPiece: ChessPiece? = null
    var tileName: String = (('a'.code +  x_coord).toChar()).toString() + (y_coord+1).toString()
    var attacked: Boolean = false


    fun copy(): Tile{
        val new: Tile = Tile(x_coord, y_coord)
        new.isEmpty = isEmpty
        if(chessPiece == null){
            new.chessPiece = null
        }
        else {
            new.chessPiece = chessPiece?.copy()
        }
        new.tileName = tileName
        new.attacked = attacked
        return new
    }

}