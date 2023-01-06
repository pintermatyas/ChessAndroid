package hu.bme.aut.android.chess

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.chess.Board.*
import hu.bme.aut.android.chess.Board.Pieces.*
import hu.bme.aut.android.chess.databinding.ActivityGameViewBinding
import kotlin.math.abs


class GameViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameViewBinding
    var board = Board()
    private var buttons =  ArrayList<ImageButton>()
    private var previouslySelectedPiece: ChessPiece? = null
    private var previouslySelectedTile: Tile? = null
    private var nextPlayer: Int = 0
    private var previousBoard = Board()
    private var init = true
    private var revertable = false

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
        binding.fabBack.setOnClickListener {
            revert()
        }


        drawBoard()
        alterPlayer()

    }

    private fun revert(){
        if(revertable){
            for(i in 0..63){
                board.tiles[i] = previousBoard.tiles[i]?.copy()
                if(previousBoard.tiles[i] == null){
                    board.tiles[i] = null
                }
            }
            alterPlayer()
            drawBoard()
        }
        else {
            Snackbar.make(binding.root, "Not available!", Snackbar.ANIMATION_MODE_SLIDE).show()
        }
        revertable = false
    }

    private fun onTileClick(view: View){

        if(checkForCheck()){
            //TODO Only allow steps that prevents further checks
        }

        val tileId = view.contentDescription.toString()
        val currentTile = board.searchForTileById(tileId)
        val currentPiece = currentTile?.chessPiece
        var castle = false
        if(previouslySelectedPiece == null && currentPiece?.player == nextPlayer){
            previouslySelectedPiece = currentPiece
        }
        var step = false
        var prevTile: Tile? = null

        if(previouslySelectedPiece!=null){
//            Toast.makeText(this, "selected piece is not null", Toast.LENGTH_SHORT).show()
            if(currentPiece?.player != nextPlayer && currentPiece != null && previouslySelectedPiece!!.player != nextPlayer){
//                Toast.makeText(this, "selected piece is not next player", Toast.LENGTH_SHORT).show()
                previouslySelectedPiece = currentPiece
                previouslySelectedTile = currentTile
                checkForCheck()
                return
            }

            //Step
            if(previouslySelectedPiece?.player == nextPlayer){
                if(currentTile?.let { previouslySelectedPiece!!.checkIfValidMove(it,board) } == true){
                    for(i in 0..63){
                        previousBoard.tiles[i] = board.tiles[i]?.copy()
                        if(board.tiles[i] == null){
                            previousBoard.tiles[i] = null
                        }
                    }
                    revertable = true
//                    previousBoard.tiles = board.tiles.clone()
                    previouslySelectedTile?.let {
                        board.step(it, currentTile)
                        it.chessPiece = null
                        currentTile.chessPiece = previouslySelectedPiece
                    }
                    previouslySelectedPiece!!.step(currentTile,board)
                    findButtonFromTile(currentTile)?.setImageResource(getImageFromChessPiece(previouslySelectedPiece!!))
                    findButtonFromTile(previouslySelectedTile!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                    prevTile = previouslySelectedTile
                    step = true
                    if(previouslySelectedPiece is King && abs(prevTile!!.x_coord - currentTile.x_coord) > 1){
                        castle = true
                    }
                    previouslySelectedPiece = null
//                    Toast.makeText(this, "step valid", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //DEBUG
        else{
//            Toast.makeText(this, "selected piece is null", Toast.LENGTH_SHORT).show()
        }
        previouslySelectedTile = currentTile
        previouslySelectedPiece?.pos_x = currentTile?.x_coord!!
        previouslySelectedPiece?.pos_y = currentTile.y_coord
        previouslySelectedPiece = currentPiece

        if(step) {
            if(castle){
                Snackbar.make(binding.root, "CASTLE PLAYER ${nextPlayer}", Snackbar.ANIMATION_MODE_SLIDE).show()
                if(currentTile.tileName == "c1"){
                    board.tiles[0]?.chessPiece = null
                    board.tiles[0]?.isEmpty = true
                    findButtonFromTile(board.tiles[0]!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                    board.tiles[3] = Tile(3,0)
                    board.tiles[3]?.chessPiece = Rook(3,0,0)
                    board.tiles[3]?.isEmpty = false
                }
                else if(currentTile.tileName == "g1"){
                    board.tiles[7]?.chessPiece = null
                    board.tiles[7]?.isEmpty = true
                    findButtonFromTile(board.tiles[7]!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                    board.tiles[5] = Tile(5,0)
                    board.tiles[5]?.chessPiece = Rook(5,0,0)
                    board.tiles[5]?.isEmpty = false
                }
                else if(currentTile.tileName == "c8"){
                    board.tiles[56]?.chessPiece = null
                    board.tiles[56]?.isEmpty = true
                    findButtonFromTile(board.tiles[56]!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                    board.tiles[59] = Tile(3,7)
                    board.tiles[59]?.chessPiece = Rook(3,7,1)
                    board.tiles[59]?.isEmpty = false
                }
                else if(currentTile.tileName == "g8"){
                    board.tiles[63]?.chessPiece = null
                    board.tiles[63]?.isEmpty = true
                    findButtonFromTile(board.tiles[63]!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                    board.tiles[61] = Tile(5,7)
                    board.tiles[61]?.chessPiece = Rook(5,7,1)
                    board.tiles[61]?.isEmpty = false
                }
            }

            drawBoard()
            highlightTile(view)
            if (prevTile != null) {
                findButtonFromTile(prevTile)?.let { highlightTile(it) }
            }
            board.tiles[prevTile!!.x_coord + prevTile.y_coord*8]?.isEmpty = true
            board.tiles[currentTile.x_coord + currentTile.y_coord*8]?.isEmpty = false

            alterPlayer()

            step = false
            previouslySelectedPiece = null
            checkForCheck()
            return
        }

        drawBoard()
        highlightTile(view)
        checkForCheck()

        if(currentPiece != null){
            if(currentPiece.player == nextPlayer){
                for(b in buttons){
                    val tempTileId = b.contentDescription.toString()
                    val tempTile = board.searchForTileById(tempTileId)
                    if(tempTile != null){
                        if(currentPiece.checkIfValidMove(tempTile,board)){
                            findButtonFromTile(tempTile)?.let { highlightTile(it) }
                        }
                    }
                }
            }
        }

    }

    fun checkForKingAttack(tile: Tile): Boolean{
        var attacked = false

        if(tile.chessPiece !is King) return false

        for(t in board.tiles){
            val piece = t?.chessPiece
            if(piece != null){
                //If the selected piece is attacking the enemy King
                if(piece.isAttackingKingOn(tile,board)){
                    attacked = true
                }
            }
        }
        return attacked
    }

    fun checkForCheck(): Boolean{
        for(t in board.tiles){
            val piece = t?.chessPiece
            if(piece is King && checkForKingAttack(t)){
                highlightTileForCheck(findButtonFromTile(t)!!)
                val playerstring = if(piece.player == 1)  "white"  else "black"
                Snackbar.make(binding.root, "CHECK for $playerstring", Snackbar.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }

    fun highlightTile(view: View){
        val tileId = view.contentDescription.toString()
        val tile = board.searchForTileById(tileId)
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

    fun highlightTileForCheck(view: View){
        val tileId = view.contentDescription.toString()
        val tile = board.searchForTileById(tileId)
        if(tile!=null){
            if(tile.x_coord % 2 == 0){
                if(tile.y_coord % 2 == 0){
                    view.setBackgroundResource(R.drawable.tile_gray_dark)
                }
                else{
                    view.setBackgroundResource(R.drawable.tile_grey_light)
                }
            }
            else{
                if(tile.y_coord % 2 == 0){
                    view.setBackgroundResource(R.drawable.tile_grey_light)
                }
                else{
                    view.setBackgroundResource(R.drawable.tile_gray_dark)
                }
            }
        }
    }

    fun findButtonFromTile(tile: Tile): ImageButton? {
        for(b in buttons){
            val tileId = b.contentDescription.toString()
            val tempTile = board.searchForTileById(tileId)
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
            val tile = board.searchForTileById(tileId)

            if(piece?.pos_x == tile?.x_coord && piece?.pos_y == tile?.y_coord){
                piece?.let { getImageFromChessPiece(it) }?.let { b.setImageResource(it) }
            }
            if(piece == null){
                b.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
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

    fun alterPlayer(){
        if(!init){
            if(nextPlayer == 1){
                binding.player1indicator.text = ""
                binding.player2indicator.text = "YOUR ROUND!"
                nextPlayer = 0
            }
            else if(nextPlayer == 0){
                binding.player1indicator.text = "YOUR ROUND!"
                binding.player2indicator.text = ""
                nextPlayer = 1
            }
        }
        if(nextPlayer == 1) {
            binding.player1indicator.text = ""
            binding.player2indicator.text = "YOUR ROUND!"
        }
        else if(nextPlayer == 0) {
            binding.player1indicator.text = "YOUR ROUND!"
            binding.player2indicator.text = ""
        }
        init = false
    }
}