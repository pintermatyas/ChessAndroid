package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class Pawn(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    override var canPathBeBlocked = false
    override var stepCount = 0

    override fun step(tile: Tile?, board: Board) {
        if(isAlive && tile != null){
            if(checkIfValidMove(tile,board)){
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
        if(tile.chessPiece?.player == this.player || tile.chessPiece is King){
            return false
        }
        val xNew = tile.x_coord
        val yNew = tile.y_coord
        if(posY == firstPosY){
            return when (firstPosY) {
                1 -> {
                    (yNew == posY+2 && xNew == posX && board.tiles[posX + (posY + 1) * 8]!!.isEmpty && board.tiles[posX + (posY + 2) * 8]!!.isEmpty) || (yNew == posY+1 && xNew == posX && board.tiles[posX + (posY + 1) * 8]!!.isEmpty) || ((yNew == posY+1 && abs(xNew - posX)==1) && !tile.isEmpty)
                }
                6 -> {
                    (yNew == posY-2 && xNew == posX && board.tiles[posX + (posY - 1) * 8]!!.isEmpty && board.tiles[posX + (posY - 2) * 8]!!.isEmpty) || (yNew == posY-1 && xNew == posX && board.tiles[posX + (posY - 1) * 8]!!.isEmpty) || ((yNew == posY-1 && abs(xNew - posX)==1) && !tile.isEmpty)
                }
                else -> false
            }
        }
        if(!tile.isEmpty){
            return when (firstPosY) {
                1 -> {
                    yNew == posY+1 && abs(xNew - posX)==1
                }
                6 -> {
                    yNew == posY-1 && abs(xNew - posX)==1
                }
                else -> false
            }
        }
        return when (firstPosY) {
            1 -> {
                yNew == posY+1 && xNew == posX
            }
            6 -> {
                yNew == posY-1 && xNew == posX
            }
            else -> false
        }
    }

    override fun copy(): ChessPiece {
        val p = Pawn(posX, posY, player)
        p.imagePath = imagePath
        p.isAlive = isAlive
        p.canPathBeBlocked = canPathBeBlocked
        p.firstPosX = firstPosX
        p.firstPosY = firstPosY
        return p
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        return checkIfValidMove(tile, board)
    }

    override fun isAttackingTile(tile: Tile, board: Board): Boolean {
        if(tile.chessPiece?.player == this.player){
            return false
        }
        val xNew = tile.x_coord
        val yNew = tile.y_coord
        return when (firstPosY) {
            1 -> {
                yNew == posY+1 && abs(xNew - posX)==1
            }
            6 -> {
                yNew == posY-1 && abs(xNew - posX)==1
            }
            else -> false
        }
    }

    fun checkForTradeability(): Boolean{
        if(firstPosY == 1){
            return posY == 7
        }
        if(firstPosY == 6){
            return posY == 0
        }
        return false
    }
}