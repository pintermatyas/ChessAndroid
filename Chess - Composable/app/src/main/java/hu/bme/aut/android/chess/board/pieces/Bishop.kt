package hu.bme.aut.android.chess.board.pieces

import hu.bme.aut.android.chess.board.Board
import hu.bme.aut.android.chess.board.Tile
import kotlin.math.abs

class Bishop(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    override var shortenedName: String = "B"
    override var stepCount = 0

    override fun step(tile: Tile?, board: Board) {
        if(isAlive && tile != null){
            if(!isPathBlockedToTile(tile,board)){
                stepCount++
                posX = tile.xCoord
                posY = tile.yCoord
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
        val xNew = tile.xCoord
        val yNew = tile.yCoord

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
        if(abs(tile.xCoord - posX) == abs(tile.yCoord - posY)){
            //Diagonal up
            if((tile.xCoord - posX) == (tile.yCoord - posY)){
                val diff = abs(tile.xCoord - posX)
                val pos = (tile.yCoord - posY > 0)
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
            else if((tile.xCoord - posX) == -(tile.yCoord - posY)){
                val diff = abs(tile.xCoord - posX)
                val pos = (tile.yCoord - posY > 0)
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
        val xNew = tile.xCoord
        val yNew = tile.yCoord

        return if(!isPathBlockedToTile(tile, board)){
            abs(posX - xNew) == abs(posY - yNew)
        } else false
    }
}