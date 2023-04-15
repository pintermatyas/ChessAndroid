package hu.bme.aut.android.chess.board.pieces

import android.content.ContentValues.TAG
import android.util.Log
import hu.bme.aut.android.chess.board.Board
import hu.bme.aut.android.chess.board.Tile
import kotlin.math.abs

class Pawn(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    override var shortenedName: String = "P"
    override var canPathBeBlocked = false
    override var stepCount = 0
    var enPassant = false
    var dir = determineDirection()

    override fun step(tile: Tile?, board: Board) {
        if(isAlive && tile != null){
            if(checkIfValidMove(tile,board)){
                stepCount++
                if(stepCount == 1){
                    enPassant = true
                }
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


        if(tile.yCoord == posY+dir){
            if(abs(posX - tile.xCoord) == 1 && posY == firstPosY + 3*dir){
//                Log.d("asd", checkForEnPassant(tile, board.tiles[tile.yCoord*8-dir + tile.xCoord]?.chessPiece).toString())
                Log.d(TAG, "Checking En Passant on target tile (${tile.xCoord},${tile.yCoord}) ")
                Log.d(TAG, "Current position is ($posX, $posY)")
                Log.d(TAG, "Target piece ${board.tiles[(tile.yCoord-dir)*8 + tile.xCoord]?.chessPiece} at (${tile.xCoord}, $${tile.yCoord-dir})")
                Log.d(TAG, "Target piece player ${board.tiles[(tile.yCoord-dir)*8 + tile.xCoord]?.chessPiece?.player}, current player: $player")
                if(checkForEnPassant(tile, board.tiles[(tile.yCoord-dir)*8 + tile.xCoord]?.chessPiece)){
                    return true
                }
            }
        }

        val xNew = tile.xCoord
        val yNew = tile.yCoord
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
        p.dir = dir
        p.enPassant = enPassant
        return p
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

    fun checkForEnPassant(targetTile: Tile, passantedPiece: ChessPiece?): Boolean {
        if(passantedPiece == null || passantedPiece.player == player){
            return false
        }
        Log.d("ENPASSANT", "Checking en passant on (${targetTile.xCoord}, ${targetTile.yCoord}), this: (${posX}, ${posY}), neighbor: $passantedPiece: (${passantedPiece.posX}, ${passantedPiece.posY})")

        if(passantedPiece.posY == posY && passantedPiece is Pawn && passantedPiece.enPassant && targetTile.yCoord == posY+dir){
            Log.d("ENPASSANT", "Google en passant on ${targetTile.xCoord} ${targetTile.yCoord}, this: ${posX} ${posY}, neighbor: ${passantedPiece.posX} ${passantedPiece.posY}")
            return true
        }
        return false
    }

    fun determineDirection(): Int{
        if(player == 0) return 1
        else return -1
    }

    fun revertEnPassant(){
        if(enPassant){
            Log.d(TAG, "reverted enpassant")
            enPassant = false
        }
    }
}