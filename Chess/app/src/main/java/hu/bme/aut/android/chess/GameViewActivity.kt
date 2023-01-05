package hu.bme.aut.android.chess

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.chess.Board.*
import hu.bme.aut.android.chess.Board.Pieces.*
import hu.bme.aut.android.chess.databinding.ActivityGameViewBinding


class GameViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameViewBinding
    var board = Board()
    var buttons =  ArrayList<ImageButton>()
    var selectedPiece: ChessPiece? = null
    var selectedTile: Tile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpButtons()

        for(b in buttons){
            b.setOnClickListener {
                onTileClick(it)
            }
        }
        drawBoard()

    }

    fun onTileClick(view: View){
        val tileId = view.contentDescription.toString()
        var tile = board.searchForTileById(tileId)
        var piece = tile?.chessPiece
        if(selectedPiece!=null){
            if(tile?.let { selectedPiece!!.checkIfValidMove(it) } == true){
                selectedTile?.let { board.step(selectedPiece!!, it, tile) }
                selectedPiece!!.step(tile)
                findButtonFromTile(tile!!)?.setImageResource(getImageFromChessPiece(selectedPiece!!))
                findButtonFromTile(selectedTile!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                return
            }
        }
        selectedTile = tile
        piece?.pos_x = tile?.x_coord!!
        piece?.pos_y = tile?.y_coord!!
        selectedPiece = piece


        drawBoard()
        highlightTile(view)

        if(piece != null){
            for(b in buttons){
                val tempTileId = b.contentDescription.toString()
                var tempTile = board.searchForTileById(tempTileId)
                if(tempTile != null){
                    if(piece.checkIfValidMove(tempTile)){
                        findButtonFromTile(tempTile)?.let { highlightTile(it) }
                    }
                }
            }
        }

    }

    fun highlightTile(view: View){
        val tileId = view.contentDescription.toString()
        var tile = board.searchForTileById(tileId)
        if(tile!=null){
            if(tile.x_coord % 2 == 0){
                if(tile.y_coord % 2 == 0){
                    view.setBackgroundResource(R.drawable.tile_brown_dark)
                }
                else{
                    view.setBackgroundResource(R.drawable.tile_brown_light)
                }
            }
            else{
                if(tile.y_coord % 2 == 0){
                    view.setBackgroundResource(R.drawable.tile_brown_light)
                }
                else{
                    view.setBackgroundResource(R.drawable.tile_brown_dark)
                }
            }
        }
    }

    fun findButtonFromTile(tile: Tile): ImageButton? {
        for(b in buttons){
            val tileId = b.contentDescription.toString()
            var tempTile = board.searchForTileById(tileId)
            if(tempTile?.x_coord == tile.x_coord && tempTile.y_coord == tile.y_coord){
                return b
            }
        }
        return null
    }

    fun setUpButtons(){
        buttons.add(binding.a1)
        buttons.add(binding.a2)
        buttons.add(binding.a3)
        buttons.add(binding.a4)
        buttons.add(binding.a5)
        buttons.add(binding.a6)
        buttons.add(binding.a7)
        buttons.add(binding.a8)

        buttons.add(binding.b1)
        buttons.add(binding.b2)
        buttons.add(binding.b3)
        buttons.add(binding.b4)
        buttons.add(binding.b5)
        buttons.add(binding.b6)
        buttons.add(binding.b7)
        buttons.add(binding.b8)

        buttons.add(binding.c1)
        buttons.add(binding.c2)
        buttons.add(binding.c3)
        buttons.add(binding.c4)
        buttons.add(binding.c5)
        buttons.add(binding.c6)
        buttons.add(binding.c7)
        buttons.add(binding.c8)

        buttons.add(binding.d1)
        buttons.add(binding.d2)
        buttons.add(binding.d3)
        buttons.add(binding.d4)
        buttons.add(binding.d5)
        buttons.add(binding.d6)
        buttons.add(binding.d7)
        buttons.add(binding.d8)

        buttons.add(binding.e1)
        buttons.add(binding.e2)
        buttons.add(binding.e3)
        buttons.add(binding.e4)
        buttons.add(binding.e5)
        buttons.add(binding.e6)
        buttons.add(binding.e7)
        buttons.add(binding.e8)

        buttons.add(binding.f1)
        buttons.add(binding.f2)
        buttons.add(binding.f3)
        buttons.add(binding.f4)
        buttons.add(binding.f5)
        buttons.add(binding.f6)
        buttons.add(binding.f7)
        buttons.add(binding.f8)

        buttons.add(binding.g1)
        buttons.add(binding.g2)
        buttons.add(binding.g3)
        buttons.add(binding.g4)
        buttons.add(binding.g5)
        buttons.add(binding.g6)
        buttons.add(binding.g7)
        buttons.add(binding.g8)

        buttons.add(binding.h1)
        buttons.add(binding.h2)
        buttons.add(binding.h3)
        buttons.add(binding.h4)
        buttons.add(binding.h5)
        buttons.add(binding.h6)
        buttons.add(binding.h7)
        buttons.add(binding.h8)
    }

    fun drawBoard(){
        for(b in buttons){
            val piece = board.searchForTileById(b.contentDescription.toString())?.chessPiece
            val tileId = b.contentDescription.toString()
            var tile = board.searchForTileById(tileId)

            if(piece?.pos_x == tile?.x_coord && piece?.pos_y == tile?.y_coord){
                piece?.let { getImageFromChessPiece(it) }?.let { b.setImageResource(it) }
            }

            if(tile!=null){
                if(tile.x_coord % 2 == 0){
                    if(tile.y_coord % 2 == 0){
                        b.setBackgroundResource(R.drawable.tile_beige_dark)
                    }
                    else{
                        b.setBackgroundResource(R.drawable.tile_beige_light)
                    }
                }
                else{
                    if(tile.y_coord % 2 == 0){
                        b.setBackgroundResource(R.drawable.tile_beige_light)
                    }
                    else{
                        b.setBackgroundResource(R.drawable.tile_beige_dark)
                    }
                }
            }
        }
    }

    fun getImageFromChessPiece(piece: ChessPiece): Int {
        if(piece is Queen){
            if(piece.player==0){
                return R.drawable.queen_white
            }
            else if(piece.player==1){
                return R.drawable.queen_black
            }
        }
        else if(piece is Bishop){
            if(piece.player==0){
                return R.drawable.bishop_white
            }
            else if(piece.player==1){
                return R.drawable.bishop_black
            }
        }
        else if(piece is King){
            if(piece.player==0){
                return R.drawable.king_white
            }
            else if(piece.player==1){
                return R.drawable.king_black
            }
        }
        else if(piece is Knight){
            if(piece.player==0){
                return R.drawable.knight_white
            }
            else if(piece.player==1){
                return R.drawable.knight_black
            }
        }
        else if(piece is Pawn){
            if(piece.player==0){
                return R.drawable.pawn_white
            }
            else if(piece.player==1){
                return R.drawable.pawn_black
            }
        }
        else if(piece is Rook){
            if(piece.player==0){
                return R.drawable.rook_white
            }
            else if(piece.player==1){
                return R.drawable.rook_black
            }
        }
        return com.google.android.material.R.drawable.navigation_empty_icon
    }

}