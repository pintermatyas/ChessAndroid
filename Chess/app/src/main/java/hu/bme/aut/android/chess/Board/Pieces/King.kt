package hu.bme.aut.android.chess.Board.Pieces

import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Tile
import kotlin.math.abs

class King(x: Int, y: Int, playerId : Int) : ChessPiece(x,y, playerId) {
    var isInCheck = false
    override var canPathBeBlocked = false
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
        if(tile.chessPiece?.player == this.player){
            return false
        }

        if(pos_x == first_pos_x && pos_y == first_pos_y){
            if(tile.tileName == "c1" && player == 0){
                var possibleRook: ChessPiece? = board.searchForTileById("a1")?.chessPiece
                if(possibleRook is Rook && possibleRook.pos_y == possibleRook.first_pos_y && possibleRook.pos_x == possibleRook.first_pos_x){
                    if(board.searchForTileById("b1")!!.isEmpty && board.searchForTileById("c1")!!.isEmpty && board.searchForTileById("d1")!!.isEmpty){
                        return true
                    }
                }

            }
            else if(tile.tileName == "g1" && player == 0){
                var possibleRook: ChessPiece? = board.searchForTileById("h1")?.chessPiece
                if(possibleRook is Rook && possibleRook.pos_y == possibleRook.first_pos_y && possibleRook.pos_x == possibleRook.first_pos_x){
                    if(board.searchForTileById("f1")!!.isEmpty && board.searchForTileById("g1")!!.isEmpty){
                        return true
                    }
                }
            }
            else if(tile.tileName == "c8" && player == 1){
                var possibleRook: ChessPiece? = board.searchForTileById("a8")?.chessPiece
                if(possibleRook is Rook && possibleRook.pos_y == possibleRook.first_pos_y && possibleRook.pos_x == possibleRook.first_pos_x){
                    if(board.searchForTileById("b8")!!.isEmpty && board.searchForTileById("c8")!!.isEmpty && board.searchForTileById("d8")!!.isEmpty){
                        return true
                    }
                }

            }
            else if(tile.tileName == "g8" && player == 1){
                var possibleRook: ChessPiece? = board.searchForTileById("h8")?.chessPiece
                if(possibleRook is Rook && possibleRook.pos_y == possibleRook.first_pos_y && possibleRook.pos_x == possibleRook.first_pos_x){
                    if(board.searchForTileById("f8")!!.isEmpty && board.searchForTileById("g8")!!.isEmpty){
                        return true
                    }
                }
            }
        }

        val x_new = tile.x_coord
        val y_new = tile.y_coord
        return abs(x_new - pos_x)<=1 && abs(y_new - pos_y)<=1
    }

    override fun copy(): ChessPiece {
        var king = King(pos_x, pos_y, player)
        king.imagePath = imagePath
        king.isAlive = isAlive
        king.canPathBeBlocked = canPathBeBlocked
        king.first_pos_x = first_pos_x
        king.first_pos_y = first_pos_y
        return king
    }

    override fun isPathBlockedToTile(tile: Tile, board: Board): Boolean {
        return checkIfValidMove(tile, board)
    }
}