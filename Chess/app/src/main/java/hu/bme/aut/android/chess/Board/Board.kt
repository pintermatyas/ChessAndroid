package hu.bme.aut.android.chess.Board

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

    fun copy(): Board{
        val newBoard = Board()
        for((idx, _) in tiles.withIndex()){
            newBoard.tiles[idx] = this.tiles[idx]?.copy()
        }
        return newBoard
    }
}