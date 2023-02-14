package hu.bme.aut.android.chess.Board

import android.content.ContentValues.TAG
import android.util.Log
import hu.bme.aut.android.chess.Board.Pieces.King
import hu.bme.aut.android.chess.Board.Pieces.Pawn
import hu.bme.aut.android.chess.Board.Pieces.Queen
import org.junit.Assert.*

class BoardTest {

    @org.junit.Test
    fun step() {
        var referenceBoard = Board()
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

    @org.junit.Test
    fun testCheck(){
        var testBoard = Board()

        testBoard.empty()

        for(t in testBoard.tiles){
            assertEquals(null, t?.chessPiece)
            assertTrue(t!!.isEmpty)
        }


        testBoard.tiles[0]?.chessPiece = King(0,0,0)
        testBoard.tiles[0]?.isEmpty = false

        testBoard.tiles[56]?.chessPiece = Queen(0,7,1)
        testBoard.tiles[56]?.isEmpty = false

        assertTrue(testBoard.checkForKingAttack(testBoard.tiles[0]!!))

        testBoard.tiles[56]?.chessPiece = Queen(0,5,0)
        testBoard.tiles[56]?.isEmpty = false

        assertFalse(testBoard.checkForKingAttack(testBoard.tiles[0]!!))

    }
}