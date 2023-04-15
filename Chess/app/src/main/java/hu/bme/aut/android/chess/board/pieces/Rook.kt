package hu.bme.aut.android.chess.board.pieces

import hu.bme.aut.android.chess.board.Board
import hu.bme.aut.android.chess.board.Tile
import kotlin.math.abs

class Rook(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    override var shortenedName: String = "R"
    override var stepCount = 0

    override fun step(tile: Tile?, board: Board) {
        if(isAlive && tile != null){
            if(checkIfValidMove(tile,board)){
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
            xNew == posX || yNew == posY
        } else false
    }

    override fun copy(): ChessPiece {
        val r = Rook(posX, posY, player)
        r.imagePath = imagePath
        r.isAlive = isAlive
        r.canPathBeBlocked = canPathBeBlocked
        r.firstPosX = firstPosX
        r.firstPosY = firstPosY
        return r
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        var blocked = false
        //Vertical
        if(tile.yCoord == posY){
            val xDiff = abs(tile.xCoord - posX)
            val pos = tile.xCoord - posX>0
            if(pos){
                for(i in 1 until xDiff){
                    if(!board.tiles[(posX + i) + posY * 8]?.isEmpty!!){
                        blocked = true
                    }
                }
            }
            else if(!pos){
                for(i in 1 until xDiff){
                    if(!board.tiles[(posX - i) + posY * 8]?.isEmpty!!){
                        blocked = true
                    }
                }
            }
        }
        //Horizontal
        else if(tile.xCoord == posX){
            val yDiff = abs(tile.yCoord - posY)
            val pos = tile.yCoord - posY>0
            if(pos){
                for(i in 1 until yDiff){
                    if(!board.tiles[posX + (posY+i) * 8]?.isEmpty!!){
                        blocked = true
                    }
                }
            }
            else if(!pos){
                for(i in 1 until yDiff){
                    if(!board.tiles[posX + (posY-i) * 8]?.isEmpty!!){
                        blocked = true
                    }
                }
            }
        }
        else blocked = true
        return blocked
    }

    override fun isAttackingTile(tile: Tile, board: Board): Boolean {
        if(tile.chessPiece?.player == this.player){
            return false
        }
        val xNew = tile.xCoord
        val yNew = tile.yCoord
        return if(!isPathBlockedToTile(tile, board)){
            xNew == posX || yNew == posY
        } else false
    }
}