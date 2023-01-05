package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class Pawn(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    var firstPos = y

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
        if(!tile.isEmpty){
            return when (firstPos) {
                1 -> {
                    y_new == pos_y+1 && abs(x_new - pos_x)==1
                }
                6 -> {
                    y_new == pos_y-1 && abs(x_new - pos_x)==1
                }
                else -> false
            }
        }
        return when (firstPos) {
            1 -> {
                y_new == pos_y+1 && x_new == pos_x
            }
            6 -> {
                y_new == pos_y-1 && x_new == pos_x
            }
            else -> false
        }
    }
}