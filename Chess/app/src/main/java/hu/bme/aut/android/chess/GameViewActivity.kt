package hu.bme.aut.android.chess

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Pieces.*
import hu.bme.aut.android.chess.Board.Tile
import hu.bme.aut.android.chess.databinding.ActivityGameViewBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class GameViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameViewBinding
    private lateinit var board: Board

    private var buttons =  ArrayList<ImageButton>()
    private var buttonNames = ArrayList<String>()
    private var backup = ArrayList<Board>()
    private var steps = ArrayList<String>()


    private var previouslySelectedPiece: ChessPiece? = null
    private var previouslySelectedTile: Tile? = null
    private var currentPlayer: Int = 0
    private var previousBoard = Board()
    private var firstRound = true
    private var latestPromote: ChessPiece? = null //Latest promoted chess piece
    private var lastStep: Int = 0 //Player with the latest step
    private var aiLevel = 0 //0: disabled
    private lateinit var prefs: SharedPreferences

    private var multiplayer = true

    //firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var message: DatabaseReference
    private var username: String = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        multiplayer = intent.extras!!.getBoolean("multiplayer")
        super.onCreate(savedInstanceState)
        binding = ActivityGameViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        board = Board()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        multiplayer = prefs.getBoolean("multiplayer", true)


        database = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
        message = database.reference
        username = prefs.getString("username", "").toString()

        if(multiplayer) binding.fabBack.isVisible = false
        message.child("players").child(username).setValue("unavailable")
        message.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                runOnUiThread {
                    val map = dataSnapshot.value as Map<String, *>?
                    val sortedMap = map?.toSortedMap(compareBy<String?>{it})
                    val values = listOf(sortedMap?.values?.last())
                    if(values.last().toString().contains(username)){
                        interpretMessage(values.last().toString())
                    }
                    toast(values.last().toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


        setUpButtons()
        drawBoard()
        changeNextPlayer()

    }

    override fun onPause() {
        super.onPause()
        message.child("players").child(username).setValue("offline")
    }

    override fun onResume() {
        super.onResume()
        if(multiplayer){
            message.child("players").child(username).setValue("unavailable")
        }
    }

    @SuppressLint("PrivateResource")
    private fun onTileClick(view: View){
        if(checkForCheckMate(board)){
            drawBoard()
            highlightTile(view)
            checkForCheck(board, false)
            Snackbar.make(binding.root, "CHECKMATE", Snackbar.LENGTH_LONG).show()
            return
        }

        val tileId = view.contentDescription.toString()
        val currentTile = board.searchForTileById(tileId)
        val currentPiece = currentTile?.chessPiece
        var castling = false
        var promote = false
        var step = false
        var prevTile: Tile? = null

        if(previouslySelectedPiece == null && currentPiece?.player == currentPlayer){
            previouslySelectedPiece = currentPiece
        }
        if(previouslySelectedPiece!=null){
            if(currentPiece?.player != currentPlayer && currentPiece != null && previouslySelectedPiece!!.player != currentPlayer){
                previouslySelectedPiece = currentPiece
                previouslySelectedTile = currentTile
                checkForCheck(board, false)
                return
            }
            //Step
            if(previouslySelectedPiece?.player == currentPlayer){
                if(currentTile?.let { previouslySelectedPiece!!.checkIfValidMove(it,board) } == true){
                    val tempBoard = board.copy()
                    val currentTileCopy = tempBoard.tiles[currentTile.x_coord + currentTile.y_coord*8]
                    val prevSelectedPieceCopy = previouslySelectedPiece!!.copy()
                    val prevTileCopy = tempBoard.tiles[previouslySelectedTile!!.x_coord + previouslySelectedTile!!.y_coord*8]
                    prevTileCopy?.let {
                        tempBoard.step(it, currentTileCopy!!)
                        it.chessPiece = null
                        it.isEmpty = true
                        currentTileCopy.isEmpty = false
                        currentTileCopy.chessPiece = prevSelectedPieceCopy
                        currentTileCopy.chessPiece?.posX = currentTileCopy.x_coord
                        currentTileCopy.chessPiece?.posY = currentTileCopy.y_coord
                    }

                    val opponent = if(currentPlayer==0) 1 else 0
                    if(!mutableListOf(2, opponent).contains(checkForCheck(tempBoard, false))){
                        //Backup the Board
                        for(i in 0..63){
                            previousBoard.tiles[i] = board.tiles[i]?.copy()
                            if(board.tiles[i] == null){
                                previousBoard.tiles[i] = null
                            }
                        }
                        backup.add(previousBoard.copy())
                        steps.add(previouslySelectedPiece?.shortenedName +  previouslySelectedTile?.tileName + currentTile.tileName)
//                        Toast.makeText(this, steps.last(), Toast.LENGTH_SHORT).show()
                        previouslySelectedTile?.let {
                            board.step(it, currentTile)
                            it.chessPiece = null
                            currentTile.chessPiece = previouslySelectedPiece
                            currentTile.chessPiece?.posX = currentTile.x_coord
                            currentTile.chessPiece?.posY = currentTile.y_coord
                        }
                        previouslySelectedPiece!!.step(currentTile,board)
                        prevTile = previouslySelectedTile
                        step = true
                        if(previouslySelectedPiece is King && abs(prevTile!!.x_coord - currentTile.x_coord) > 1){
                            castling = true
                        }
                        if(previouslySelectedPiece is Pawn){
                            if((previouslySelectedPiece as Pawn).checkForTradeability()){
                                promote = true
                            }
                        }
                        previouslySelectedPiece = null
                    }
                }
            }
        }

        previouslySelectedTile = currentTile!!
        val prevPiece: ChessPiece? = previouslySelectedPiece?.copy()
        previouslySelectedPiece = currentPiece

        if(step) {
            if(castling){
                manageCastling(currentTile)
            } else if(promote){
                latestPromote = prevPiece
                lastStep = currentPlayer
                createPromotionPopupWindow()
            }

            drawBoard()
            highlightTile(view)
            if (prevTile != null) {
                findButtonFromTile(prevTile)?.let { highlightTile(it) }
            }
            board.tiles[prevTile!!.x_coord + prevTile.y_coord*8]?.isEmpty = true
            board.tiles[currentTile.x_coord + currentTile.y_coord*8]?.isEmpty = false
            var sent = false
            if(multiplayer){
                sent = sendBoard()
            }
            changeNextPlayer()
            previouslySelectedPiece = null
            checkForCheck(board, false)
            checkForCheckMate(board)
            if(!multiplayer || (multiplayer && sent)) return
        }

        drawBoard()
        highlightTile(view)
        checkForCheck(board, false)
        checkForCheckMate(board)

        //Highlight the valid moves
        if(currentPiece != null){
            if(currentPiece.player == currentPlayer){
                for(b in buttons){
                    val tempTileId = b.contentDescription.toString()
                    val tempTile = board.searchForTileById(tempTileId)
                    if(tempTile != null){
                        if(currentPiece.checkIfValidMove(tempTile,board)){
                            val opponentCode = if(currentPlayer==0) 1 else 0
                            if(currentPiece is King){
                                if(!mutableListOf(2, opponentCode).contains(board.checkForAttack(tempTile))){
                                    findButtonFromTile(tempTile)?.let { highlightTile(it) }
                                }
                            }
                            else{
                                findButtonFromTile(tempTile)?.let { highlightTile(it) }
                            }
                        }
                    }
                }
            }
        }

    }

    @SuppressLint("PrivateResource")
    private fun manageCastling(currentTile: Tile){
        board.manageCastling(currentTile)
        drawBoard()
    }

    private fun createPromotionPopupWindow(){
        val autoQueenPromotion = prefs.getBoolean("autoqueenpromotion", true)

        if(autoQueenPromotion){
            managePromotion(1)
        } else{
            val factory = LayoutInflater.from(this)
            val promotionDialogView: View = factory.inflate(R.layout.dialog_promote, null)
            val promoteDialog: AlertDialog? = AlertDialog.Builder(this).create()
            promoteDialog?.setView(promotionDialogView)
            promoteDialog?.setCancelable(false)
            promoteDialog?.setCanceledOnTouchOutside(false)
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
                    managePromotion(1)
                    promoteDialog?.dismiss()
                }
            promotionDialogView.findViewById<ImageButton>(R.id.promote_rook)
                .setOnClickListener {
                    managePromotion(2)
                    promoteDialog?.dismiss()
                }
            promotionDialogView.findViewById<ImageButton>(R.id.promote_bishop)
                .setOnClickListener {
                    managePromotion(3)
                    promoteDialog?.dismiss()
                }
            promotionDialogView.findViewById<ImageButton>(R.id.promote_knight)
                .setOnClickListener {
                    managePromotion(4)
                    promoteDialog?.dismiss()
                }
            promoteDialog!!.show()
        }
    }

    private fun managePromotion(id: Int){

        board.promotePawnTo(id, previouslySelectedTile, lastStep)

        drawBoard()
    }

    private fun checkForCheck(b: Board, findingCheckMate: Boolean): Int{
        var checkByWhite = false
        var checkByBlack = false
        for(t in b.tiles){
            val piece = t?.chessPiece
            if(piece is King && b.checkForKingAttack(t)){
                if(!findingCheckMate) highlightTileForCheck(findButtonFromTile(t)!!)
                if(piece.player == 0) checkByBlack = true
                if(piece.player == 1) checkByWhite = true
            }
        }
        return if(checkByBlack && checkByWhite) 2
        else if(!checkByWhite && checkByBlack) 1
        else if(checkByWhite && !checkByBlack) 0
        else -1
    }

    private fun checkForCheckMate(b: Board): Boolean{
        val check = checkForCheck(b, true)
        var checkmate = true
        if(check!=-1 || check != 2){
            val checkedPlayer = if(check==0) 1 else 0
            for(t in b.tiles){
                if(t?.chessPiece?.player != checkedPlayer) continue
                val temp = b.copy()
                for(tempTiles in temp.tiles){
                    val tempBoard = temp.copy()
                    tempBoard.step(tempBoard.tiles[t.x_coord + t.y_coord*8]!!, tempBoard.tiles[tempTiles!!.x_coord + (tempTiles.y_coord * 8)]!!)
                    if(checkForCheck(tempBoard, true) != check) checkmate = false

                }

            }
        }
        return checkmate
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

    //Sets up the buttons and its onClick events.
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

        for(b in buttons){
            b.setOnClickListener {
                onTileClick(it)
            }
            buttonNames.add(b.contentDescription.toString())
        }

        binding.settingsbtn.setOnClickListener {
            val intent = Intent(this@GameViewActivity, SettingsActivity::class.java).apply {  }
            startActivity(intent)
        }

        binding.fabBack.setOnClickListener {
            revert()
        }
        binding.resetbtn.setOnClickListener {
            resetBoard()
        }
    }

    @SuppressLint("PrivateResource")
    fun drawBoard(){
        for(b in buttons){
            val piece = board.searchForTileById(b.contentDescription.toString())?.chessPiece
            val tileId = b.contentDescription.toString()
            val tile = board.searchForTileById(tileId)

            if(piece?.posX == tile?.x_coord && piece?.posY == tile?.y_coord){
                piece?.let { getImageResourceFromChessPiece(it) }?.let { b.setImageResource(it) }
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
    fun getImageResourceFromChessPiece(piece: ChessPiece): Int {
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

    //Changes how the layout looks, depending on the current player
    private fun changeNextPlayer(){

        if(!firstRound){
            for(b in buttons){
                if(currentPlayer == 0){
                    b.rotation = 180F
                } else b.rotation = 0F
            }
            if(currentPlayer == 1){
                currentPlayer = 0
                binding.root.setBackgroundResource(R.color.white)
            }
            else if(currentPlayer == 0){
                if(aiLevel==0){
                    currentPlayer = 1
                    binding.root.setBackgroundResource(R.color.black)
                }
            }
//            buttonNames.reverse()
//            for((idx, b) in buttons.withIndex()){
//                b.contentDescription = buttonNames[idx]
//            }

        }
        if(firstRound){
            binding.root.setBackgroundResource(R.color.white)
        }
        firstRound = false
        drawBoard()
    }

    //Completely resets the board and everything related to it
    private fun resetBoard(){
        board = Board()
        if(currentPlayer == 1) {
//            buttonNames.reverse()
//            for((idx, b) in buttons.withIndex()){
//                b.contentDescription = buttonNames[idx]
//            }
            changeNextPlayer()
        }
        previouslySelectedPiece = null
        previouslySelectedTile = null
        currentPlayer = 0
        previousBoard = Board()
        firstRound = true
        latestPromote = null
        lastStep = 0
        backup.clear()
        steps.clear()
        changeNextPlayer()
        drawBoard()
    }

    //Restores the board one move before current state
    private fun revert(){
//        for(b in buttons){
//            b.rotation = 180F
//        }
//        return

        val size = backup.size
        if(size == 0){
            Snackbar.make(binding.root, "Not available!", Snackbar.ANIMATION_MODE_SLIDE).show()
            return
        }
        board = backup.removeAt(size-1).copy()
        steps.removeAt(size-1)
        changeNextPlayer()
        drawBoard()
    }

    fun sendBoard(): Boolean{
        username = prefs.getString("username", "").toString()
        val enemy = prefs.getString("opponent", "").toString()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(Calendar.getInstance().time )
        message.child(date).setValue(steps.last() + " " + enemy)
        return true
    }

    fun toast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    fun interpretMessage(message: String){

        return

        username = prefs.getString("username", "").toString()

        if(!message.contains(username) || message.contains("entered")) return

        var inCharacters = message.toCharArray()
        var piece: ChessPiece? = null
        var prevTileCol: Int = (inCharacters[1] - 'a')
        var prevTileRow: Int = inCharacters[2].digitToInt() - 1
        var currentTileCol: Int = (inCharacters[3] - 'a')
        var currentTileRow: Int = inCharacters[4].digitToInt() - 1

        board.tiles[prevTileCol + prevTileRow*8]?.chessPiece = null
        board.tiles[prevTileCol + prevTileRow*8]?.isEmpty = true

        if(inCharacters[0] == 'Q'){
            piece = Queen(currentTileCol, currentTileRow, currentPlayer)
        }
        else if(inCharacters[0] == 'R'){
            piece = Rook(currentTileCol, currentTileRow, currentPlayer)
        }
        else if(inCharacters[0] == 'H'){
            piece = Knight(currentTileCol, currentTileRow, currentPlayer)
        }
        else if(inCharacters[0] == 'K'){
            piece = King(currentTileCol, currentTileRow, currentPlayer)
        }
        else if(inCharacters[0] == 'P'){
            piece = Pawn(currentTileCol, currentTileRow, currentPlayer)
        }

        board.tiles[currentTileCol + currentTileRow*8]?.chessPiece = piece
        board.tiles[currentTileCol + currentTileRow*8]?.isEmpty = false

        changeNextPlayer()
//        drawBoard()
    }

    @Throws(Exception::class)
    private fun getTime(): Long {
        val url = "https://time.is/Unix_time_now"
        val doc: Document = Jsoup.parse(URL(url).openStream(), "UTF-8", url)
        val tags = arrayOf(
            "div[id=time_section]",
            "div[id=clock0_bg]"
        )
        var elements: Elements = doc.select(tags[0])
        for (i in tags.indices) {
            elements = elements.select(tags[i])
        }
        return elements.text().toLong()
    }

    private fun getDate(time: Long): String? {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = time * 1000
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.ENGLISH)
        return dateFormat.format(cal.time)
    }
}