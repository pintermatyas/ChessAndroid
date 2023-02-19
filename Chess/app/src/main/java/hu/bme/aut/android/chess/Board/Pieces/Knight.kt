package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class Knight(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    override var shortenedName: String = "H" //Horse
    override var stepCount = 0
    override var canPathBeBlocked = false

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
        val dx = abs(posX - xNew)
        val dy = abs(posY - yNew)
        return (dx==2 && dy==1) || (dx==1 && dy == 2)
    }

    override fun copy(): ChessPiece {
        val k = Knight(posX, posY, player)
        k.imagePath = imagePath
        k.isAlive = isAlive
        k.canPathBeBlocked = canPathBeBlocked
        k.firstPosX = firstPosX
        k.firstPosY = firstPosY
        return k
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        return checkIfValidMove(tile, board)
    }

    override fun isAttackingTile(tile: Tile, board: Board): Boolean {
        if(tile.chessPiece?.player == this.player){
            return false
        }
        val xNew = tile.xCoord
        val yNew = tile.yCoord
        val dx = abs(posX - xNew)
        val dy = abs(posY - yNew)
        return ((dx==2 && dy==1) || (dx==1 && dy == 2))
    }
}