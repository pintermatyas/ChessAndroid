package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class Rook(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {

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
        if(!isPathBlockedToTile(tile, board)){
            return x_new == pos_x || y_new == pos_y
        }
        else return false
    }

    override fun copy(): ChessPiece {
        var r = Rook(pos_x, pos_y, player)
        r.imagePath = imagePath
        r.isAlive = isAlive
        r.canPathBeBlocked = canPathBeBlocked
        r.first_pos_x = first_pos_x
        r.first_pos_y = first_pos_y
        return r
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        var blocked = false
        //Vertical
        if(tile.y_coord == pos_y){
            val xdiff = abs(tile.x_coord - pos_x)
            val pos = tile.x_coord - pos_x>0
            if(pos){
                for(i in 1..xdiff-1){
                    if(!board.tiles[(pos_x + i) + pos_y * 8]?.isEmpty!!){
                        blocked = true
                    }
                }
            }
            else if(!pos){
                for(i in 1..xdiff-1){
                    if(!board.tiles[(pos_x - i) + pos_y * 8]?.isEmpty!!){
                        blocked = true
                    }
                }
            }
        }
        //Horizontal
        else if(tile.x_coord == pos_x){
            val ydiff = abs(tile.y_coord - pos_y)
            val pos = tile.y_coord - pos_y>0
            if(pos){
                for(i in 1..ydiff-1){
                    if(!board.tiles[pos_x + (pos_y+i) * 8]?.isEmpty!!){
                        blocked = true
                    }
                }
            }
            else if(!pos){
                for(i in 1..ydiff-1){
                    if(!board.tiles[pos_x + (pos_y-i) * 8]?.isEmpty!!){
                        blocked = true
                    }
                }
            }
        }
        else blocked = true
        return blocked
    }

    override fun isAttackingKingOn(tile: Tile, board: Board): Boolean {
        if(tile.chessPiece?.player == this.player || tile.chessPiece !is King){
            return false
        }
        val x_new = tile.x_coord
        val y_new = tile.y_coord
        if(!isPathBlockedToTile(tile, board) &&tile.chessPiece is King){
            return x_new == pos_x || y_new == pos_y
        }
        else return false
    }
}