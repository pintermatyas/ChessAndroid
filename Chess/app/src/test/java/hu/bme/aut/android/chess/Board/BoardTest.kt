package hu.bme.aut.android.chess.Board

import android.content.ContentValues.TAG
import android.util.Log
import hu.bme.aut.android.chess.Board.Pieces.Pawn
import org.junit.Assert.*

class BoardTest {

    @org.junit.Test
    fun step() {
        var referenceBoard: Board = Board()
        var testBoard = Board()
        referenceBoard.tiles[8]?.chessPiece = null
        val newPawn = Pawn(0,1,0)
        newPawn.posY = 2
        referenceBoard.tiles[16]?.chessPiece = newPawn

        var tiles = testBoard.tiles

        testBoard.step(tiles[8]!!, tiles[16]!!)

        assertEquals(referenceBoard, testBoard)

        if(referenceBoard.equals(testBoard)){
            println( "Opening step test passed")
        } else println("Opening step test failed")
    }

    @org.junit.Test
    fun checkForAttack() {
        val testBoard = Board()
        for((i,t) in testBoard.tiles.withIndex()){
            if(Math.floor((i/8).toDouble()) == (2).toDouble()){
                assertEquals(0, testBoard.checkForAttack(t!!))
            }
            if(Math.floor((i/8).toDouble()) == (5).toDouble()){
                assertEquals(1, testBoard.checkForAttack(t!!))
            }
        }

    }
}