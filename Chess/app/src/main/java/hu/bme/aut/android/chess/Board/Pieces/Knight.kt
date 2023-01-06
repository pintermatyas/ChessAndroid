package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class Knight(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
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
        if(tile.chessPiece?.player == this.player){
            return false
        }
        val x_new = tile.x_coord
        val y_new = tile.y_coord
        val dx = abs(pos_x - x_new)
        val dy = abs(pos_y - y_new)
        return (dx==2 && dy==1) || (dx==1 && dy == 2)
    }

    override fun copy(): ChessPiece {
        var k = Knight(pos_x, pos_y, player)
        k.imagePath = imagePath
        k.isAlive = isAlive
        k.canPathBeBlocked = canPathBeBlocked
        k.first_pos_x = first_pos_x
        k.first_pos_y = first_pos_y
        return k
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        return checkIfValidMove(tile, board)
    }
}