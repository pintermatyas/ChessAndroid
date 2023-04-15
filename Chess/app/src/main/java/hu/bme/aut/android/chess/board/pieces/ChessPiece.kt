package hu.bme.aut.android.chess.board.pieces

import hu.bme.aut.android.chess.board.Board
import hu.bme.aut.android.chess.board.Tile

abstract class ChessPiece(x: Int, y: Int, playerId: Int) {
    abstract var shortenedName: String
    var posX: Int = x
    var posY: Int = y
    var firstPosX: Int = x
    var firstPosY: Int = y
    var isAlive: Boolean = true
    val player: Int = playerId
    open var canPathBeBlocked: Boolean = true
    var imagePath: String = ""
    open var stepCount: Int = 0

    abstract fun step(tile: Tile?, board: Board)

    abstract fun checkIfValidMove(tile: Tile, board: Board): Boolean

    abstract fun copy(): ChessPiece

    abstract fun isPathBlockedToTile(tile: Tile, board: Board): Boolean

    abstract fun isAttackingTile(tile: Tile, board: Board): Boolean

    override fun equals(other: Any?): Boolean {
        if(other !is ChessPiece) return false

        if(posX == other.posX && posY == other.posY){
            if(this.javaClass == other.javaClass){
                if(firstPosX == other.firstPosX && firstPosY == other.firstPosY){
                    if(player == other.player){
                        return true
                    }
                }
            }
        }
        return super.equals(other)
    }
}