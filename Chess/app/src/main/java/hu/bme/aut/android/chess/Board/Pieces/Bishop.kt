package hu.bme.aut.android.chess.Board.Pieces

import android.util.Log
import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class Bishop(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    override var shortenedName: String = "B"
    override var stepCount = 0

    override fun step(tile: Tile?, board: Board) {
        if(isAlive && tile != null){
            if(!isPathBlockedToTile(tile,board)){
                stepCount++
                posX = tile.x_coord
                posY = tile.y_coord
                if(tile.isEmpty){
                    tile.isEmpty = false
                } else tile.chessPiece?.isAlive = false
            }
        }
    }

    override fun checkIfValidMove(tile: Tile, board: Board): Boolean {
//        Log.d("Checking if move valid:", "$player with $shortenedName from $posX,$posY to ${tile.tileName}")
        if(tile.chessPiece?.player == this.player || tile.chessPiece is King){
            return false
        }
        val xNew = tile.x_coord
        val yNew = tile.y_coord

        return if(!isPathBlockedToTile(tile, board)){
            abs(posX - xNew) == abs(posY - yNew)
        } else false
    }

    override fun copy(): ChessPiece {
        val b = Bishop(posX, posY, player)
        b.imagePath = imagePath
        b.isAlive = isAlive
        b.canPathBeBlocked = canPathBeBlocked
        b.firstPosX = firstPosX
        b.firstPosY = firstPosY
        return b
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        var blocked = false
        //Diagonal
        if(abs(tile.x_coord - posX) == abs(tile.y_coord - posY)){
            //Diagonal up
            if((tile.x_coord - posX) == (tile.y_coord - posY)){
                val diff = abs(tile.x_coord - posX)
                val pos = (tile.y_coord - posY > 0)
                if(pos){
                    for(i in 1 until diff){
                        if(!board.tiles[(posX + i) + (posY + i)*8]?.isEmpty!!){
                            blocked = true
                        }
                    }
                }
                else if(!pos){
                    for(i in 1 until diff){
                        if(!board.tiles[(posX - i) + (posY - i)*8]?.isEmpty!!){
                            blocked = true
                        }
                    }
                }
            }
            //Diagonal down
            else if((tile.x_coord - posX) == -(tile.y_coord - posY)){
                val diff = abs(tile.x_coord - posX)
                val pos = (tile.y_coord - posY > 0)
                if(pos){
                    for(i in 1 until diff){
                        if(!board.tiles[(posX - i) + (posY + i)*8]?.isEmpty!!){
                            blocked = true
                        }
                    }
                }
                else if(!pos){
                    for(i in 1 until diff){
                        if(!board.tiles[(posX + i) + (posY - i)*8]?.isEmpty!!){
                            blocked = true
                        }
                    }
                }
            }
        }
        return blocked
    }

    override fun isAttackingTile(tile: Tile, board: Board): Boolean {
        if(tile.chessPiece?.player == this.player){
            return false
        }
        val xNew = tile.x_coord
        val yNew = tile.y_coord

        return if(!isPathBlockedToTile(tile, board)){
            abs(posX - xNew) == abs(posY - yNew)
        } else false
    }
}