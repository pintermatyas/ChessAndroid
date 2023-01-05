package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class King(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    override fun getPossibleMoves() {
        TODO("Not yet implemented")
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
        return abs(x_new - pos_x)==1 || abs(y_new - pos_y)==1
    }
}