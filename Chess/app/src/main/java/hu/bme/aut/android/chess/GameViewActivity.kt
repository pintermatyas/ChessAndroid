package hu.bme.aut.android.chess

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
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
    private var currentPlayer: Int = 0
    private var previousBoard = Board()
    private var init = true
    private var reversible = false
    private var latestPromote: ChessPiece? = null
    private var lastStep: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

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
        changeNextPlayer()

    }

    private fun revert(){
        if(reversible){
            for(i in 0..63){
                board.tiles[i] = previousBoard.tiles[i]?.copy()
                if(previousBoard.tiles[i] == null){
                    board.tiles[i] = null
                }
            }
            changeNextPlayer()
            drawBoard()
        }
        else {
            Snackbar.make(binding.root, "Not available!", Snackbar.ANIMATION_MODE_SLIDE).show()
        }
        reversible = false
    }

    @SuppressLint("PrivateResource")
    private fun onTileClick(view: View){

        if(checkForCheck()){
            //TODO Only allow steps that prevents further checks
        }

        val tileId = view.contentDescription.toString()
        val currentTile = board.searchForTileById(tileId)
        val currentPiece = currentTile?.chessPiece
        var castle = false
        var promote = false
        if(previouslySelectedPiece == null && currentPiece?.player == currentPlayer){
            previouslySelectedPiece = currentPiece
        }
        var step = false
        var prevTile: Tile? = null

        if(previouslySelectedPiece!=null){

            if(currentPiece?.player != currentPlayer && currentPiece != null && previouslySelectedPiece!!.player != currentPlayer){
                previouslySelectedPiece = currentPiece
                previouslySelectedTile = currentTile
                checkForCheck()
                return
            }


            //Step
            if(previouslySelectedPiece?.player == currentPlayer){


                if(currentTile?.let { previouslySelectedPiece!!.checkIfValidMove(it,board) } == true){
                    //Backup the board
                    for(i in 0..63){
                        previousBoard.tiles[i] = board.tiles[i]?.copy()
                        if(board.tiles[i] == null){
                            previousBoard.tiles[i] = null
                        }
                    }
                    reversible = true
                    previouslySelectedTile?.let {
                        board.step(it, currentTile)
                        it.chessPiece = null
                        currentTile.chessPiece = previouslySelectedPiece
                        currentTile.chessPiece?.pos_x = currentTile.x_coord
                        currentTile.chessPiece?.pos_y = currentTile.y_coord
                    }
                    previouslySelectedPiece!!.step(currentTile,board)
                    prevTile = previouslySelectedTile
                    step = true
                    if(previouslySelectedPiece is King && abs(prevTile!!.x_coord - currentTile.x_coord) > 1){
                        castle = true
                    }
                    if(previouslySelectedPiece is Pawn){
                        if((previouslySelectedPiece as Pawn).checkForTradebility()){
                            promote = true
                        }
                    }
                    previouslySelectedPiece = null
                }
            }
        }

        previouslySelectedTile = currentTile!!
        val prevPiece: ChessPiece? = previouslySelectedPiece?.copy()
        previouslySelectedPiece = currentPiece

        if(step) {
            if(castle){
                Snackbar.make(binding.root, "CASTLE PLAYER $currentPlayer", Snackbar.ANIMATION_MODE_SLIDE).show()
                when (currentTile.tileName) {
                    "c1" -> {
                        board.tiles[0]?.chessPiece = null
                        board.tiles[0]?.isEmpty = true
                        findButtonFromTile(board.tiles[0]!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                        board.tiles[3] = Tile(3,0)
                        board.tiles[3]?.chessPiece = Rook(3,0,0)
                        board.tiles[3]?.isEmpty = false
                    }
                    "g1" -> {
                        board.tiles[7]?.chessPiece = null
                        board.tiles[7]?.isEmpty = true
                        findButtonFromTile(board.tiles[7]!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                        board.tiles[5] = Tile(5,0)
                        board.tiles[5]?.chessPiece = Rook(5,0,0)
                        board.tiles[5]?.isEmpty = false
                    }
                    "c8" -> {
                        board.tiles[56]?.chessPiece = null
                        board.tiles[56]?.isEmpty = true
                        findButtonFromTile(board.tiles[56]!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                        board.tiles[59] = Tile(3,7)
                        board.tiles[59]?.chessPiece = Rook(3,7,1)
                        board.tiles[59]?.isEmpty = false
                    }
                    "g8" -> {
                        board.tiles[63]?.chessPiece = null
                        board.tiles[63]?.isEmpty = true
                        findButtonFromTile(board.tiles[63]!!)?.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                        board.tiles[61] = Tile(5,7)
                        board.tiles[61]?.chessPiece = Rook(5,7,1)
                        board.tiles[61]?.isEmpty = false
                    }
                }
            }

            else if(promote){
                latestPromote = prevPiece
                lastStep = currentPlayer
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                if(prefs.getBoolean("autoqueenpromotion", true)){
                    promote(1)
                }

                else{
                    val factory = LayoutInflater.from(this)
                    val promotionDialogView: View = factory.inflate(R.layout.dialog_promote, null)
                    val promoteDialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(this).create()
                    promoteDialog?.setView(promotionDialogView)
                    if(currentPlayer == 0){
                        promotionDialogView.findViewById<ImageButton>(R.id.promote_knight).setImageResource(R.drawable.knight_white)
                        promotionDialogView.findViewById<ImageButton>(R.id.promote_rook).setImageResource(R.drawable.rook_white)
                        promotionDialogView.findViewById<ImageButton>(R.id.promote_queen).setImageResource(R.drawable.queen_white)
                        promotionDialogView.findViewById<ImageButton>(R.id.promote_bishop).setImageResource(R.drawable.bishop_white)
                    }
                    else if(currentPlayer == 1){
                        promotionDialogView.findViewById<ImageButton>(R.id.promote_knight).setImageResource(R.drawable.knight_black)
                        promotionDialogView.findViewById<ImageButton>(R.id.promote_rook).setImageResource(R.drawable.rook_black)
                        promotionDialogView.findViewById<ImageButton>(R.id.promote_queen).setImageResource(R.drawable.queen_black)
                        promotionDialogView.findViewById<ImageButton>(R.id.promote_bishop).setImageResource(R.drawable.bishop_black)
                    }

                    promotionDialogView.findViewById<ImageButton>(R.id.promote_queen)
                        .setOnClickListener {
                            promote(1)
                            promoteDialog?.dismiss()
                        }
                    promotionDialogView.findViewById<ImageButton>(R.id.promote_rook)
                        .setOnClickListener {
                            promote(2)
                            promoteDialog?.dismiss()
                        }
                    promotionDialogView.findViewById<ImageButton>(R.id.promote_bishop)
                        .setOnClickListener {
                            promote(3)
                            promoteDialog?.dismiss()
                        }
                    promotionDialogView.findViewById<ImageButton>(R.id.promote_knight)
                        .setOnClickListener {
                            promote(4)
                            promoteDialog?.dismiss()
                        }

                    promoteDialog!!.show()
                }

            }

            drawBoard()
            highlightTile(view)
            if (prevTile != null) {
                findButtonFromTile(prevTile)?.let { highlightTile(it) }
            }
            board.tiles[prevTile!!.x_coord + prevTile.y_coord*8]?.isEmpty = true
            board.tiles[currentTile.x_coord + currentTile.y_coord*8]?.isEmpty = false
            changeNextPlayer()
            previouslySelectedPiece = null
            checkForCheck()
            return
        }

        drawBoard()
        highlightTile(view)
        checkForCheck()

        //Highlight the valid moves
        if(currentPiece != null){
            if(currentPiece.player == currentPlayer){
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

    private fun promote(id: Int){
        var piece: ChessPiece? = null
        //Queen
        when (id) {
            1 -> {
                piece = Queen(previouslySelectedTile!!.x_coord, previouslySelectedTile!!.y_coord, lastStep)
            }
            //Rook
            2 -> {
                piece = Rook(previouslySelectedTile!!.x_coord, previouslySelectedTile!!.y_coord, lastStep)
            }
            //Bishop
            3 -> {
                piece = Bishop(previouslySelectedTile!!.x_coord, previouslySelectedTile!!.y_coord, lastStep)
            }
            //Knight
            4 -> {
                piece = Knight(previouslySelectedTile!!.x_coord, previouslySelectedTile!!.y_coord, lastStep)
            }
        }
        board.tiles[previouslySelectedTile!!.x_coord + previouslySelectedTile!!.y_coord*8]?.chessPiece = piece
        drawBoard()
    }

    private fun checkForKingAttack(tile: Tile): Boolean{
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

    private fun checkForCheck(): Boolean{
        for(t in board.tiles){
            val piece = t?.chessPiece
            if(piece is King && checkForKingAttack(t)){
                highlightTileForCheck(findButtonFromTile(t)!!)
                val playerString = if(piece.player == 1)  "white"  else "black"
                Snackbar.make(binding.root, "CHECK for $playerString", Snackbar.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }

    private fun highlightTile(view: View){
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

    private fun highlightTileForCheck(view: View){
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

    private fun findButtonFromTile(tile: Tile): ImageButton? {
        for(b in buttons){
            val tileId = b.contentDescription.toString()
            val tempTile = board.searchForTileById(tileId)
            if(tempTile?.x_coord == tile.x_coord && tempTile.y_coord == tile.y_coord){
                return b
            }
        }
        return null
    }

    private fun setUpButtons(){
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

        binding.settingsbtn.setOnClickListener {
            val intent = Intent(this@GameViewActivity, SettingsActivity::class.java).apply {  }
            startActivity(intent)
        }
    }

    @SuppressLint("PrivateResource")
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

    @SuppressLint("PrivateResource")
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

    private fun changeNextPlayer(){
        if(!init){
            if(currentPlayer == 1){
                binding.player1indicator.text = ""
//                binding.player2indicator.text = getString(R.string.your_round)
                currentPlayer = 0
                binding.root.setBackgroundResource(R.color.white)
            }
            else if(currentPlayer == 0){
                binding.player1indicator.text = getString(R.string.your_round)
//                binding.player2indicator.text = ""
                currentPlayer = 1
                binding.root.setBackgroundResource(R.color.black)
            }
        }
        if(currentPlayer == 1) {
            binding.player1indicator.text = ""
//            binding.player2indicator.text = getString(R.string.your_round)
        }
        else if(currentPlayer == 0) {
//            binding.player1indicator.text = getString(R.string.your_round)
            binding.player2indicator.text = ""
        }
        if(init){
            binding.root.setBackgroundResource(R.color.white)
        }
        init = false
    }
}