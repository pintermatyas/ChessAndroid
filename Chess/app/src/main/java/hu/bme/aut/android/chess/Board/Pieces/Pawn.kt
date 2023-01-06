package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class Pawn(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    override var canPathBeBlocked = false
    override fun getPossibleMoves() {
        TODO("Not yet implemented")
    }

    override fun step(tile: Tile?, board: Board) {
        if(isAlive && tile != null){
            if(checkIfValidMove(tile,board)){
                pos_x = tile.x_coord
                pos_y = tile.y_coord
                if(tile.isEmpty){
                    tile.isEmpty = false
                } else tile.chessPiece?.isAlive = false
            }
        }
    }

    override fun checkIfValidMove(tile: Tile, board: Board): Boolean {
        if(tile.chessPiece?.player == this.player || tile.chessPiece is King){
            return false
        }
        val x_new = tile.x_coord
        val y_new = tile.y_coord
        if(pos_y == first_pos_y){
            return when (first_pos_y) {
                1 -> {
                    ((y_new == pos_y+2 || y_new == pos_y+1) && x_new == pos_x) || ((y_new == pos_y+1 && abs(x_new - pos_x)==1) && !tile.isEmpty)
                }
                6 -> {
                    ((y_new == pos_y-2 || y_new == pos_y-1) && x_new == pos_x) || ((y_new == pos_y-1 && abs(x_new - pos_x)==1) && !tile.isEmpty)
                }
                else -> false
            }
        }
        if(!tile.isEmpty){
            return when (first_pos_y) {
                1 -> {
                    y_new == pos_y+1 && abs(x_new - pos_x)==1
                }
                6 -> {
                    y_new == pos_y-1 && abs(x_new - pos_x)==1
                }
                else -> false
            }
        }
        return when (first_pos_y) {
            1 -> {
                y_new == pos_y+1 && x_new == pos_x
            }
            6 -> {
                y_new == pos_y-1 && x_new == pos_x
            }
            else -> false
        }
    }

    override fun copy(): ChessPiece {
        var p = Pawn(pos_x, pos_y, player)
        p.imagePath = imagePath
        p.isAlive = isAlive
        p.canPathBeBlocked = canPathBeBlocked
        p.first_pos_x = first_pos_x
        p.first_pos_y = first_pos_y
        return p
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        return checkIfValidMove(tile, board)
    }

    override fun isAttackingKingOn(tile: Tile, board: Board): Boolean {
        if(tile.chessPiece?.player == this.player || tile.chessPiece !is King){
            return false
        }
        val x_new = tile.x_coord
        val y_new = tile.y_coord
        val isKing = tile.chessPiece is King
        if(pos_y == first_pos_y){
            return when (first_pos_y) {
                1 -> {
                    ((y_new == pos_y+1 && abs(x_new - pos_x)==1) && !tile.isEmpty) && isKing
                }
                6 -> {
                    ((y_new == pos_y-1 && abs(x_new - pos_x)==1) && !tile.isEmpty) && isKing
                }
                else -> false
            }
        }
        if(!tile.isEmpty){
            return when (first_pos_y) {
                1 -> {
                    y_new == pos_y+1 && abs(x_new - pos_x)==1 && isKing
                }
                6 -> {
                    y_new == pos_y-1 && abs(x_new - pos_x)==1 && isKing
                }
                else -> false
            }
        }
        return false
    }
}