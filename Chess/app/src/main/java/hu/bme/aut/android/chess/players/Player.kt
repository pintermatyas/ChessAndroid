package hu.bme.aut.android.chess.players

import hu.bme.aut.android.chess.board.Board
import hu.bme.aut.android.chess.board.pieces.ChessPiece

open class Player(var board: Board, var playerNumber: Int, var timeLeftInMillis: Int) {
    var pieces = ArrayList<ChessPiece>()


    private fun step(){
        pieces = getChessPieces()



    }

    private fun getChessPieces(): ArrayList<ChessPiece>{
        val piecesOnBoard = ArrayList<ChessPiece>()
        for(tile in board.tiles){
            if(tile!!.chessPiece?.player == playerNumber){
                piecesOnBoard.add(tile.chessPiece!!.copy())
            }
        }
        return piecesOnBoard
    }

    private fun evaluateBoard(){

    }
}