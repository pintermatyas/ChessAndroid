package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class Bishop(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {

    override fun getPossibleMoves() {

    }

    override fun step(tile: Tile?, board: Board) {
        if(isAlive && tile != null){
            if(!isPathBlockedToTile(tile,board)){
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
            return abs(pos_x - x_new) == abs(pos_y - y_new)
        }
        else return false
    }

    override fun copy(): ChessPiece {
        var b = Bishop(pos_x, pos_y, player)
        b.imagePath = imagePath
        b.isAlive = isAlive
        b.canPathBeBlocked = canPathBeBlocked
        b.first_pos_x = first_pos_x
        b.first_pos_y = first_pos_y
        return b
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        var blocked = false
        //Diagonal
        if(abs(tile.x_coord - pos_x) == abs(tile.y_coord - pos_y)){
            //Diagonal up
            if((tile.x_coord - pos_x) == (tile.y_coord - pos_y)){
                val diff = abs(tile.x_coord - pos_x)
                val pos = (tile.y_coord - pos_y > 0)
                if(pos){
                    for(i in 1 until diff){
                        if(!board.tiles[(pos_x + i) + (pos_y + i)*8]?.isEmpty!!){
                            blocked = true
                        }
                    }
                }
                else if(!pos){
                    for(i in 1 until diff){
                        if(!board.tiles[(pos_x - i) + (pos_y - i)*8]?.isEmpty!!){
                            blocked = true
                        }
                    }
                }
            }
            //Diagonal down
            else if((tile.x_coord - pos_x) == -(tile.y_coord - pos_y)){
                val diff = abs(tile.x_coord - pos_x)
                val pos = (tile.y_coord - pos_y > 0)
                if(pos){
                    for(i in 1 until diff){
                        if(!board.tiles[(pos_x - i) + (pos_y + i)*8]?.isEmpty!!){
                            blocked = true
                        }
                    }
                }
                else if(!pos){
                    for(i in 1 until diff){
                        if(!board.tiles[(pos_x + i) + (pos_y - i)*8]?.isEmpty!!){
                            blocked = true
                        }
                    }
                }
            }
        }
        return blocked
    }

    override fun isAttackingKingOn(tile: Tile, board: Board): Boolean {
        if(tile.chessPiece?.player == this.player || tile.chessPiece !is King){
            return false
        }
        val x_new = tile.x_coord
        val y_new = tile.y_coord

        if(!isPathBlockedToTile(tile, board) && tile.chessPiece is King){
            return abs(pos_x - x_new) == abs(pos_y - y_new)
        }
        else return false
    }
}