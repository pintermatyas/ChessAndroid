package hu.bme.aut.android.chess.board

import hu.bme.aut.android.chess.board.pieces.ChessPiece

class Tile(x: Int,y: Int) {
    var xCoord: Int = x
    var yCoord: Int = y
    var isEmpty: Boolean = false
    var chessPiece: ChessPiece? = null
    var tileName: String = (('a'.code +  xCoord).toChar()).toString() + (yCoord+1).toString()
    var attacked: Boolean = false


    fun copy(): Tile{
        val new: Tile = Tile(xCoord, yCoord)
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