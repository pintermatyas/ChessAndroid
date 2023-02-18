package hu.bme.aut.android.chess.Board

import android.content.ContentValues.TAG
import android.util.Log
import hu.bme.aut.android.chess.Board.Pieces.*

class Board {
    var tiles =  initBoard()

    private fun initBoard(): Array<Tile?> {
        val tileInit: Array<Tile?> = Array(64){null}
        for(i in 0..63){
            val x = i%8
            val y = i/8
            val tile = Tile(x, y)
            var chessPiece: ChessPiece? = null
            var playerId: Int = -1
            if(y == 0 || y == 1){
                //White
                playerId = 0
            }
            else if(y == 6 || y == 7){
                //Black
                playerId = 1
            }

            if (y == 1 || y == 6){
                chessPiece = Pawn(x,y, playerId)
                if(y==1) chessPiece.imagePath = "drawable/chess_pieces/pawn_white"
                else chessPiece.imagePath = "drawable/chess_pieces/pawn_black"
            }
            if(y == 0 || y == 7){
                if(x == 0 || x == 7){
                    chessPiece = Rook(x,y, playerId)
                    if(y==0) chessPiece.imagePath = "drawable/chess_pieces/rook_white"
                    else chessPiece.imagePath = "drawable/chess_pieces/rook_black"
                }
                else if(x == 1 || x == 6){
                    chessPiece = Knight(x,y, playerId)
                    if(y==0) chessPiece.imagePath = "drawable/chess_pieces/knight_white"
                    else chessPiece.imagePath = "drawable/chess_pieces/knight_black"
                }
                else if(x==2 || x==5){
                    chessPiece = Bishop(x,y, playerId)
                    if(y==0) chessPiece.imagePath = "drawable/chess_pieces/bishop_white"
                    else chessPiece.imagePath = "drawable/chess_pieces/bishop_black"
                }
                else if(x==3){
                    chessPiece = Queen(x,y, playerId)
                    if(y==0) chessPiece.imagePath = "drawable/chess_pieces/queen_white"
                    else chessPiece.imagePath = "drawable/chess_pieces/queen_black"
                }
                else if(x==4){
                    chessPiece = King(x,y, playerId)
                    if(y==0) chessPiece.imagePath = "drawable/chess_pieces/king_white"
                    else chessPiece.imagePath = "drawable/chess_pieces/king_black"
                }
            }

            tile.chessPiece = chessPiece
            if(tile.chessPiece == null){
                tile.isEmpty = true
            }
            tile.x_coord = x
            tile.y_coord = y
            tileInit[i] = tile

        }
        return tileInit
    }

    fun searchForTileById(id: String): Tile? {
        for(t in tiles){
            if(t?.tileName == id){
                return t
            }
        }
        return null
    }

    fun step(from: Tile, to: Tile){
        val piece = from.chessPiece
        if(piece != null){
            if(piece.isPathBlockedToTile(to,this)){
                tiles[from.x_coord + from.y_coord*8]?.chessPiece = null
                tiles[from.x_coord + from.y_coord*8]?.isEmpty = true
                if(!to.isEmpty){
                    tiles[to.x_coord + to.y_coord*8]?.chessPiece?.isAlive = false
                }
                tiles[to.x_coord + to.y_coord*8]?.chessPiece = piece
                tiles[to.x_coord + to.y_coord*8]?.isEmpty = false
                piece.step(tiles[to.x_coord + to.y_coord*8],this)
                piece.posX = tiles[to.x_coord + to.y_coord*8]?.x_coord!!
                piece.posY = tiles[to.x_coord + to.y_coord*8]?.y_coord!!
            }
        }


    }

    fun checkForKingAttack(tile: Tile): Boolean{
        var attacked = false

        if(tile.chessPiece !is King) return false

        for(t in this.tiles){
            val piece = t?.chessPiece
            if(piece != null){
                //If the selected piece is attacking the enemy King
                if(piece.isAttackingTile(tile,this) && piece.player != (tile.chessPiece as King).player){
                    attacked = true
//                    Log.d(TAG, "King ${tile.chessPiece!!.player} is under attack on ${tile.tileName} by ${piece.shortenedName} ${piece.player}")
                }
            }
        }
        return attacked
    }

    fun checkForAttack(tile: Tile): Int{
        var white = false
        var black = false
        for(t in this.tiles){
            val piece = t?.chessPiece
            if(piece != null){
                //If the selected piece is attacking the tile
                if(piece.isAttackingTile(tile,this)){
                    if(piece.player == 0){
                        white = true
                    }
                    else if(piece.player == 1){
                        black = true
                    }
                }
            }
        }
        return if(white && black) 2
        else if(!white && black) 1
        else if(white && !black) 0
        else -1
    }

    fun promotePawnTo(id: Int, tile: Tile?, player: Int){
        var piece: ChessPiece? = null
        //Queen
        when (id) {
            1 -> {
                piece = Queen(tile!!.x_coord, tile.y_coord, player)
            }
            //Rook
            2 -> {
                piece = Rook(tile!!.x_coord, tile.y_coord, player)
            }
            //Bishop
            3 -> {
                piece = Bishop(tile!!.x_coord, tile.y_coord, player)
            }
            //Knight
            4 -> {
                piece = Knight(tile!!.x_coord, tile.y_coord, player)
            }
        }
        tiles[tile!!.x_coord + tile.y_coord*8]?.chessPiece = piece
    }

    fun manageCastling(tileOfKing: Tile){
        when (tileOfKing.tileName) {
            "c1" -> {
                //Remove old rook from board and place it on its new field
                tiles[0]?.chessPiece = null
                tiles[0]?.isEmpty = true
                tiles[3] = Tile(3,0)
                tiles[3]?.chessPiece = Rook(3,0,0)
                tiles[3]?.isEmpty = false

                //Remove old king from board and place it on its new field
                tiles[4]?.chessPiece = null
                tiles[4]?.isEmpty = true
                tiles[2] = Tile(2,0)
                tiles[2]?.chessPiece = King(2,0,0)
                tiles[2]?.isEmpty = false
            }
            "g1" -> {
                //Remove old rook from board and place it on its new field
                tiles[7]?.chessPiece = null
                tiles[7]?.isEmpty = true
                tiles[5] = Tile(5,0)
                tiles[5]?.chessPiece = Rook(5,0,0)
                tiles[5]?.isEmpty = false

                //Remove old king from board and place it on its new field
                tiles[4]?.chessPiece = null
                tiles[4]?.isEmpty = true
                tiles[6] = Tile(6,0)
                tiles[6]?.chessPiece = King(6,0,0)
                tiles[6]?.isEmpty = false
            }
            "c8" -> {
                //Remove old rook from board and place it on its new field
                tiles[56]?.chessPiece = null
                tiles[56]?.isEmpty = true
                tiles[59] = Tile(3,7)
                tiles[59]?.chessPiece = Rook(3,7,1)
                tiles[59]?.isEmpty = false

                //Remove old king from board and place it on its new field
                tiles[60]?.chessPiece = null
                tiles[60]?.isEmpty = true
                tiles[58] = Tile(2,7)
                tiles[58]?.chessPiece = King(2,7,1)
                tiles[58]?.isEmpty = false
            }
            "g8" -> {
                //Remove old rook from board and place it on its new field
                tiles[63]?.chessPiece = null
                tiles[63]?.isEmpty = true
                tiles[61] = Tile(5,7)
                tiles[61]?.chessPiece = Rook(5,7,1)
                tiles[61]?.isEmpty = false

                //Remove old king from board and place it on its new field
                tiles[60]?.chessPiece = null
                tiles[60]?.isEmpty = true
                tiles[62] = Tile(6,7)
                tiles[62]?.chessPiece = King(6,7,1)
                tiles[62]?.isEmpty = false
            }
        }
    }

    fun copy(): Board{
        val newBoard = Board()
        for((idx, _) in tiles.withIndex()){
            newBoard.tiles[idx] = this.tiles[idx]?.copy()
        }
        return newBoard
    }

    fun emptyBoard(){
        for(t in tiles){
            t?.chessPiece = null
            t?.isEmpty = true
        }
    }

    override fun toString(): String {
        var boardString = ""
        for(t in tiles){
            if(t?.chessPiece == null){
                boardString+="0"
            }
            else if(t.chessPiece!!.player == 0){
                boardString += t.chessPiece!!.shortenedName.lowercase()
            }
            else if(t.chessPiece!!.player == 1){
                boardString += t.chessPiece!!.shortenedName.uppercase()
            }
        }
        return boardString
    }

    fun constructBoardFromString(boardString: String){
        if(boardString.length != 64) return

//        emptyBoard()

        for(i in 0..63){
            var char = boardString.get(i)
            var xCoordinate = i%8
            var yCoordinate = i/8

            tiles[i] = Tile(xCoordinate, yCoordinate)

            if(char == '0'){
                tiles[i]?.isEmpty = true
                tiles[i]?.chessPiece = null
            }
            else{
                tiles[i]?.isEmpty = false
            }


            var player: Int
            if(char.isLowerCase()){ // Player is white
                player = 0
            }
            else {
                player = 1
            }


            when(char.lowercase()){
                // Bishop
                "b" -> {
                    tiles[i]?.chessPiece = Bishop(xCoordinate, yCoordinate, player)
                }
                // King
                "k" -> {
                    tiles[i]?.chessPiece = King(xCoordinate, yCoordinate, player)
                }
                // Knight
                "h" -> {
                    tiles[i]?.chessPiece = Knight(xCoordinate, yCoordinate, player)
                }
                // Pawn
                "p" -> {
                    if(player == 0){
                        tiles[i]?.chessPiece = Pawn(xCoordinate, 1, player)
                        tiles[i]?.chessPiece?.posY = yCoordinate
                    }
                    else{
                        tiles[i]?.chessPiece = Pawn(xCoordinate, 6, player)
                        tiles[i]?.chessPiece?.posY = yCoordinate
                    }
                }
                // Queen
                "q" -> {
                    tiles[i]?.chessPiece = Queen(xCoordinate, yCoordinate, player)
                }
                // Rook
                "r" -> {
                    tiles[i]?.chessPiece = Rook(xCoordinate, yCoordinate, player)
                }
            }

//            tiles[i]?.isEmpty = false

        }
    }

    override fun equals(other: Any?): Boolean {
        if(other !is Board) return false
        var allEquals: Boolean = true
        for(i in 0..63){
            if(tiles[i]?.chessPiece?.equals(other.tiles[i]?.chessPiece) == false){
                allEquals = false
            }
        }
        return allEquals
    }
}