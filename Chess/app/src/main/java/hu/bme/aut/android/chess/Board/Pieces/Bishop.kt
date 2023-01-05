package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class Bishop(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {

    override fun getPossibleMoves() {

    }

    override fun step(tile: Tile) {
        if(isAlive){
            if(checkIfValidMove(tile)){
                pos_x = tile.x_coord
                pos_y = tile.y_coord
                if(tile.isEmpty){
                    tile.isEmpty = false
                } else tile.chessPiece?.isAlive = false
            }
        }
    }

    override fun checkIfValidMove(tile: Tile): Boolean {
        if(tile.chessPiece?.player == this.player){
            return false
        }
        val x_new = tile.x_coord
        val y_new = tile.y_coord
        return abs(pos_x - x_new) == abs(pos_y - y_new)
    }
}