package hu.bme.aut.android.chess.board.pieces

import hu.bme.aut.android.chess.board.Board
import hu.bme.aut.android.chess.board.Tile
import kotlin.math.abs

class King(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    override var shortenedName: String = "K"
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
        if(tile.chessPiece?.player == this.player){
            return false
        }

        if(posX == firstPosX && posY == firstPosY){
            if(tile.tileName == "c1" && player == 0){
                val possibleRook: ChessPiece? = board.searchForTileById("a1")?.chessPiece
                if(possibleRook is Rook && possibleRook.posY == possibleRook.firstPosY && possibleRook.posX == possibleRook.firstPosX){
                    if(board.searchForTileById("b1")!!.isEmpty && board.searchForTileById("c1")!!.isEmpty && board.searchForTileById("d1")!!.isEmpty){
                        return true
                    }
                }

            }
            else if(tile.tileName == "g1" && player == 0){
                val possibleRook: ChessPiece? = board.searchForTileById("h1")?.chessPiece
                if(possibleRook is Rook && possibleRook.posY == possibleRook.firstPosY && possibleRook.posX == possibleRook.firstPosX){
                    if(board.searchForTileById("f1")!!.isEmpty && board.searchForTileById("g1")!!.isEmpty){
                        return true
                    }
                }
            }
            else if(tile.tileName == "c8" && player == 1){
                val possibleRook: ChessPiece? = board.searchForTileById("a8")?.chessPiece
                if(possibleRook is Rook && possibleRook.posY == possibleRook.firstPosY && possibleRook.posX == possibleRook.firstPosX){
                    if(board.searchForTileById("b8")!!.isEmpty && board.searchForTileById("c8")!!.isEmpty && board.searchForTileById("d8")!!.isEmpty){
                        return true
                    }
                }

            }
            else if(tile.tileName == "g8" && player == 1){
                val possibleRook: ChessPiece? = board.searchForTileById("h8")?.chessPiece
                if(possibleRook is Rook && possibleRook.posY == possibleRook.firstPosY && possibleRook.posX == possibleRook.firstPosX){
                    if(board.searchForTileById("f8")!!.isEmpty && board.searchForTileById("g8")!!.isEmpty){
                        return true
                    }
                }
            }
        }

        val xNew = tile.xCoord
        val yNew = tile.yCoord
        return abs(xNew - posX)<=1 && abs(yNew - posY)<=1
    }

    override fun copy(): ChessPiece {
        val king = King(posX, posY, player)
        king.imagePath = imagePath
        king.isAlive = isAlive
        king.canPathBeBlocked = canPathBeBlocked
        king.firstPosX = firstPosX
        king.firstPosY = firstPosY
        return king
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        return checkIfValidMove(tile, board)
    }

    override fun isAttackingTile(tile: Tile, board: Board): Boolean {
        return (abs(tile.xCoord - posX) <= 1 && abs(tile.yCoord - posY) <= 1)
    }
}