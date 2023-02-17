package hu.bme.aut.android.chess

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.Board.Pieces.*
import hu.bme.aut.android.chess.Board.Tile
import hu.bme.aut.android.chess.data.BoardData
import hu.bme.aut.android.chess.data.GameDatabase
import hu.bme.aut.android.chess.databinding.ActivityGameViewBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread
import kotlin.math.abs


class GameViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameViewBinding
    private lateinit var board: Board

    private var buttons =  ArrayList<ImageButton>()
    private var buttonNames = ArrayList<String>()

    private var backup = ArrayList<Board>()
    private var steps = ArrayList<String>() //List of steps


    private var previouslySelectedPiece: ChessPiece? = null
    private var previouslySelectedTile: Tile? = null
    private var currentPlayer: Int = 0
    private var previousBoard = Board()
    private var firstRound = true
    private var latestPromote: ChessPiece? = null //Latest promoted chess piece
    private var lastStep: Int = 0 //Player with the latest step
    private var aiLevel = 0 //0: disabled
    private lateinit var prefs: SharedPreferences //Settings

    //Required for multiplayer
    private var multiplayer = false
    private var opponent = "" // Username of opponent
    private var match = "" // Name of match
    var whitePlayer = "" // Username of white player
    var blackPlayer = "" // Username of black player
    var opponentMove = ""
    var enterLogged = false
    var flippedBoard = false

    //firebase
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var username: String = ""

    private lateinit var localDatabase: GameDatabase
    private var saved = false

    var replay = false


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MATCH ENTERED")
        board = Board()

        if(intent.extras!!.getBoolean("replay")){
            replay = true
            board.constructBoardFromString(intent.extras!!.getString("state")!!)
            currentPlayer = intent.extras!!.getInt("nextPlayer")
        }

        multiplayer = intent.extras!!.getBoolean("multiplayer")
        opponent = intent.extras!!.getString("opponent").toString()
        match = intent.extras!!.getString("match").toString()

//        toast(match)

        //Toast.makeText(this, "opponent: $opponent", Toast.LENGTH_SHORT).show()
        super.onCreate(savedInstanceState)
        binding = ActivityGameViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        var init = true


        prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        multiplayer = prefs.getBoolean("multiplayer", true)

        localDatabase = GameDatabase.getDatabase(this)


        firebaseDatabase = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
        databaseReference = firebaseDatabase.reference
        username = prefs.getString("username", "").toString()

        if(multiplayer || replay) {
            binding.fabBack.isVisible = false
            binding.resetbtn.isVisible = false
            if(!replay){
                binding.player2indicator.isVisible = true
                binding.player2indicator.text = opponent
                databaseReference.child("players").child(username).setValue("unavailable")
            } else {
                binding.player2indicator.isVisible = false
            }
            binding.settingsbtn.isVisible = false
            binding.loadlatestbtn.isVisible = false
        } else{
            binding.fabBack.isVisible = true
            binding.resetbtn.isVisible = true
            binding.player2indicator.isVisible = false
        }


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (multiplayer) {
                    val map = dataSnapshot.value as Map<*, *>?

//                    toast(match)

                    val games = map!!["games"] as HashMap<*, *>

                    if(games[match].toString() == "ended"){
                        finish()
                        return
                    }

                    if(games[match] == null){
                        return
                    }
                    val game = games[match]!! as HashMap<*, *>

                    if(game[opponent].toString() == "entered" && game[username].toString() == "entered" && !enterLogged){
                        val log = map["log"] as ArrayList<*>
                        databaseReference.child("log").child((log.size+1).toString()).setValue("entered")
                        enterLogged = true

                    }


                    if(game[opponent].toString() == "left"){
//                        toast("Opponent left the game")
                        databaseReference.child("games").child(match).child(opponent).setValue("idle")
                        databaseReference.child("games").child(match).child(username).setValue("idle")
                        databaseReference.child("games").child(match).setValue("ended")
                        databaseReference.child("games").child(match).removeValue()
                        Log.d("LOGGING OFF", "$opponent left")
                        finish()
                        return
                    }

                    else if(game[opponent].toString() == "idle"){
//                        toast("Waiting for opponent...")
                        return
                    }

                    whitePlayer = game["white"].toString()
                    blackPlayer = game["black"].toString()
                    if(whitePlayer == "" && blackPlayer == ""){
                        val rand = Calendar.getInstance().timeInMillis % 2
                        if(rand == 0L){
                            databaseReference.child("games").child(match).child("white").setValue(opponent)
                            databaseReference.child("games").child(match).child("black").setValue(username)
                            whitePlayer = game["white"].toString()
                            blackPlayer = game["black"].toString()
                            databaseReference.child("games").child(match).child("next").setValue(whitePlayer)
                        }
                    }

                    if((whitePlayer == username && flippedBoard) || (blackPlayer == username && !flippedBoard)){
//                        buttonNames.reverse()
//                        for((idx, b) in buttons.withIndex()) {
//                            b.contentDescription = buttonNames[idx]
//                        }
//                        drawBoard()
//                        flippedBoard = false
                        flipBoard()
                    }

                    if(init){
                        databaseReference.child("games").child(match).child("next").setValue(whitePlayer)
                        if(blackPlayer == username && !flippedBoard){
//                            buttonNames.reverse()
//                            for((idx, b) in buttons.withIndex()) {
//                                b.contentDescription = buttonNames[idx]
//                            }
                            flipBoard()
//                            drawBoard()
                            init = false
//                            flippedBoard = true
                        }
                        else if(whitePlayer == username){
                            init = false
                        }
                    }
                    val recentMove = game[opponent].toString()
                    if(recentMove == opponentMove){
                        return
                    } else opponentMove = recentMove
                    val tempBoard = interpretMessage(opponentMove)
//                    changeNextPlayer()
                    board = tempBoard.copy()
                    drawBoard()
                    return
//                }
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
        if(multiplayer){
            databaseReference.child("games").child(match).child(username).setValue("left")
            databaseReference.child("games").child(match).setValue("ended")
            databaseReference.child("games").child(match).removeValue()
        }
        databaseReference.child("players").child(username).setValue("offline")

    }

    @SuppressLint("NewApi")
    override fun onStop() {
        super.onStop()
        if(multiplayer){
            databaseReference.child("games").child(match).setValue("ended")
            databaseReference.child("games").child(match).child(username).setValue("left")
            databaseReference.child("games").child(match).removeValue()
        }

        if(!multiplayer && !replay && !saved){
//            val boardState = board.toString()
//
//            var save = BoardData(state=boardState,
//                nextPlayer = currentPlayer,
//                multiplayer = multiplayer,
//                date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy MM dd HH:mm"))
//            )
//
//            thread{
//                val insertId = localDatabase.BoardDataDAO().insert(save)
//                save.id = insertId
////            lastId = insertId.toInt()
//            }

            saveBoard()
        }
    }

    override fun onResume() {
        super.onResume()
        if(multiplayer){
            databaseReference.child("games").child(match).child(username).setValue("entered")
            databaseReference.child("players").child(username).setValue("unavailable")
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

        if( previouslySelectedTile?.tileName == currentTile?.tileName && steps.size == 0 || previouslySelectedTile?.tileName == currentTile?.tileName && steps.last().substring(steps.last().length-2) != currentTile!!.tileName){
            drawBoard()
            previouslySelectedTile = null
            previouslySelectedPiece = null
            return
        }

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
                    var playerNumber = -1
                    if(whitePlayer == username){
                        playerNumber = 0
                    } else if(whitePlayer == opponent){
                        playerNumber = 1
                    }
                    if(!multiplayer || (multiplayer && currentPlayer == playerNumber)){
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
                createPromotionPopupWindow(prevTile)
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
                var playerNumber = -1
                if(whitePlayer == username){
                    playerNumber = 0
                } else if(whitePlayer == opponent){
                    playerNumber = 1
                }
                if(currentPlayer == playerNumber && !promote){
                    sent = sendBoard()
                }
            }
            if(!multiplayer){
                changeNextPlayer()
            }
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


    private fun manageCastling(castlingTile: Tile){
        board.manageCastling(castlingTile)

        when (castlingTile.tileName) {
            "g1" -> {
                steps.add("O-O ${castlingTile.tileName}")
            }
            "g8" -> {
                steps.add("O-O ${castlingTile.tileName}")
            }
            "c8" -> {
                steps.add("O-O-O ${castlingTile.tileName}")
            }
            "c1" -> {
                steps.add("O-O-O ${castlingTile.tileName}")
            }
        }

        drawBoard()
    }


    //In case auto-queen promotion is turned off, it creates a pop-up dialog window, on which you can choose the promoted piece.
    private fun createPromotionPopupWindow(previousTile: Tile?){
        val autoQueenPromotion = prefs.getBoolean("autoqueenpromotion", true)

        if(autoQueenPromotion){
            managePromotion(1, previousTile)
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
                    managePromotion(1, previousTile)
                    promoteDialog?.dismiss()
                }
            promotionDialogView.findViewById<ImageButton>(R.id.promote_rook)
                .setOnClickListener {
                    managePromotion(2, previousTile)
                    promoteDialog?.dismiss()
                }
            promotionDialogView.findViewById<ImageButton>(R.id.promote_bishop)
                .setOnClickListener {
                    managePromotion(3, previousTile)
                    promoteDialog?.dismiss()
                }
            promotionDialogView.findViewById<ImageButton>(R.id.promote_knight)
                .setOnClickListener {
                    managePromotion(4, previousTile)
                    promoteDialog?.dismiss()
                }
            promoteDialog!!.show()
        }
    }

    private fun managePromotion(id: Int, oldPosition: Tile?){

        board.promotePawnTo(id, previouslySelectedTile, lastStep)

        //We need to log promotion as well, due to a multiplayer issue
        when (id) {
            //Queen
            1 -> {
                steps.add("Q" + oldPosition?.tileName + previouslySelectedTile?.tileName)
            }
            //Rook
            2 -> {
                steps.add("R" + oldPosition?.tileName + previouslySelectedTile?.tileName)
            }
            //Bishop
            3 -> {
                steps.add("B" + oldPosition?.tileName + previouslySelectedTile?.tileName)
            }
            //Knight
            4 -> {
                steps.add("H" + oldPosition?.tileName + previouslySelectedTile?.tileName)
            }
        }
        sendBoard()

        changeNextPlayer()

        drawBoard()
    }

    fun checkForCheck(b: Board, findingCheckMate: Boolean): Int{
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

    fun checkForCheckMate(b: Board): Boolean{
        val checkByPlayer = checkForCheck(b, true)
        var checkmate = true
        if(checkByPlayer != -1 || checkByPlayer != 2){
            val checkedPlayer = if(checkByPlayer==0) 1 else 0
            for(t in b.tiles){
                if(t?.chessPiece?.player != checkedPlayer) continue
                val temp = b.copy()
                for(tempTiles in temp.tiles){
                    val tempBoard = temp.copy()

                    val tempBoardTileOne = tempBoard.tiles[t!!.x_coord + t.y_coord*8]!!.copy()
                    val tempBoardPieceOne = tempBoard.tiles[t!!.x_coord + t.y_coord*8]!!.chessPiece?.copy()
                    val tempBoardTileTwo = tempBoard.tiles[tempTiles!!.x_coord + (tempTiles.y_coord * 8)]?.copy()
                    val tempBoardPieceTwo = tempBoard.tiles[tempTiles!!.x_coord + (tempTiles.y_coord * 8)]!!.chessPiece?.copy()

                    val validMove = tempBoardPieceOne?.checkIfValidMove(tempBoard.tiles[tempTiles!!.x_coord + (tempTiles.y_coord * 8)]!!, tempBoard) == true
//                    if(tempBoardPieceOne?.checkIfValidMove(tempBoardTileTwo!!, tempBoard) == true){
//                        continue
//                    }
//                    if(tempBoardPieceOne?.checkIfValidMove(tempBoard.tiles[tempTiles!!.x_coord + (tempTiles.y_coord * 8)]!!, tempBoard) == true){

//                        tempBoard.step(tempBoard.tiles[t.x_coord + t.y_coord*8]!!, tempBoard.tiles[tempTiles!!.x_coord + (tempTiles.y_coord * 8)]!!)
                        tempBoardPieceOne?.posX = tempTiles!!.x_coord
                        tempBoardPieceOne?.posY = tempTiles!!.y_coord
                        tempBoard.tiles[tempTiles!!.x_coord + (tempTiles.y_coord * 8)]!!.chessPiece = tempBoardPieceOne
                        tempBoard.tiles[tempTiles!!.x_coord + (tempTiles.y_coord * 8)]!!.isEmpty = false
                        tempBoard.tiles[t.x_coord + t.y_coord*8]!!.isEmpty = true
                        if(listOf(checkedPlayer, -1).contains(checkForCheck(tempBoard, true)) && validMove) {
//                            Log.d("Prevent checkmate:", "${tempBoardPieceOne?.player} with ${tempBoardPieceOne?.shortenedName} from ${tempBoardTileOne.tileName} to ${tempBoardTileTwo?.tileName}")
                            checkmate = false
                        }
//                    }


                }

            }
        } else if(checkByPlayer == -1 || checkByPlayer == 2){
            return false
        }

        if(checkmate) saveBoard()

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
            buttonNames.add(b.contentDescription.toString())
        }

        if(!replay){
            for(b in buttons){
                b.setOnClickListener {
                    onTileClick(it)
                }
            }
        }

        binding.settingsbtn.setOnClickListener {
            val intent = Intent(this@GameViewActivity, SettingsActivity::class.java).apply {  }
            startActivity(intent)
        }

        binding.loadlatestbtn.setOnClickListener {
            thread {
                val items = localDatabase.BoardDataDAO().getAll()
                if(items.isEmpty()) {
                    return@thread
                }
                board.constructBoardFromString(items.last().state)
                currentPlayer = items.last().nextPlayer
                runOnUiThread {
                    drawBoard()
                }
            }
        }

        binding.fabBack.setOnClickListener {
            revert()
        }
        binding.resetbtn.setOnClickListener {
//            board.constructBoardFromString("000000000000000000000000000000000000000000000000000q00000PPPPP00")
//            drawBoard()
            resetBoard()
        }
    }

    @SuppressLint("PrivateResource")
    fun drawBoard(){
        if(currentPlayer == 0){
            binding.root.setBackgroundResource(R.color.white)
        } else binding.root.setBackgroundResource(R.color.black)
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

        checkForCheck(board, false)
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
            if(!multiplayer){
                for(b in buttons){
                    if(currentPlayer == 0){
                        b.rotation = 180F
                    } else b.rotation = 0F
                }

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

        }
        if(firstRound){
            binding.root.setBackgroundResource(R.color.white)
        }
        firstRound = false
        drawBoard()
    }

    //Completely resets the board and everything related to it
    fun resetBoard(){
        board = Board()
        if(currentPlayer == 1) {
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
    fun revert(){
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

    private fun sendBoard(): Boolean{
        username = prefs.getString("username", "").toString()

        databaseReference.child("games").child(match).child(username).setValue(steps.last())
        databaseReference.child("games").child(match).child("next").setValue(opponent)
        changeNextPlayer()

        return true
    }

    fun interpretMessage(message: String): Board{

        val oldBoard = board.copy()

        username = prefs.getString("username", "").toString()

        if(message.contains("entered") || message.contains("left") || message.contains("idle") || message == "null" || message == "") return oldBoard

        //Short castling
        if(message.contains("O-O ")){
            val lastStepString = message.substring(message.length-2)
//            var tile = Tile(0,0)

            for(t in board.tiles){
                if(t?.tileName == lastStepString){
//                    tile = t.copy()
                    Log.d(TAG, "Castling on $lastStepString")
                    manageCastling(t)
                }
            }

            changeNextPlayer()
            drawBoard()

            return board
        }
        //Long castling
        else if(message.contains("O-O-O ")){
            val lastStepString = steps.last().substring(steps.last().length-2)
//            var tile = Tile(0,0)

            for(t in board.tiles){
                if(t?.tileName == lastStepString){
//                    tile = t.copy()
                    Log.d(TAG, "Castling on $lastStepString")
                    manageCastling(t)
                }
            }



            changeNextPlayer()
            drawBoard()

            return board
        }


        val inCharacters = message.toCharArray()
        var piece: ChessPiece? = null
        val prevTileCol: Int = (inCharacters[1] - 'a')

        Log.d("MESSAGE", message)

        val prevTileRow: Int = inCharacters[2].digitToInt() - 1
        val currentTileCol: Int = (inCharacters[3] - 'a')
        val currentTileRow: Int = inCharacters[4].digitToInt() - 1

//        var opponentNumber = -1
//        if(whitePlayer == username){
//            opponentNumber = 1
//        } else if(whitePlayer == opponent){
//            opponentNumber == 0
//        }
//
//
//        if(oldBoard.tiles[prevTileCol + prevTileRow*8]?.chessPiece?.player != opponentNumber){
//            return oldBoard
//        }
        val oldFirstX = oldBoard.tiles[prevTileCol + prevTileRow*8]?.chessPiece?.firstPosX
        val oldFirstY = oldBoard.tiles[prevTileCol + prevTileRow*8]?.chessPiece?.firstPosY
        val oldPiece = oldBoard.tiles[prevTileCol + prevTileRow*8]?.chessPiece?.copy()
        var player = oldPiece?.player

        if(oldPiece == null) return oldBoard

        oldBoard.tiles[prevTileCol + prevTileRow*8]?.chessPiece = null
        oldBoard.tiles[prevTileCol + prevTileRow*8]?.isEmpty = true

        if(player==null) player = 0




        if(inCharacters[0] == 'Q'){
            piece = Queen(currentTileCol, currentTileRow, player)
        }
        else if(inCharacters[0] == 'R'){
            piece = Rook(currentTileCol, currentTileRow, player)
        }
        else if(inCharacters[0] == 'H'){
            piece = Knight(currentTileCol, currentTileRow, player)
        }
        else if(inCharacters[0] == 'K'){
            piece = King(currentTileCol, currentTileRow, player)
        }
        else if(inCharacters[0] == 'P'){
            piece = Pawn(currentTileCol, currentTileRow, player)
        }
        else if(inCharacters[0] == 'B'){
            piece = Bishop(currentTileCol, currentTileRow, player)
        }


//        piece!!.firstPosX = oldPiece!!.firstPosX
//        piece.firstPosY = oldPiece.firstPosY



//        piece!!.firstPosX = oldFirstX!!
//        piece.firstPosY = oldFirstY!!


        if (oldFirstX != null) {
            piece?.firstPosX = oldFirstX
        }
        if (oldFirstY != null) {
            piece?.firstPosY = oldFirstY
        }

        piece?.shortenedName = oldPiece!!.shortenedName
        piece?.imagePath = oldPiece.imagePath
        piece?.stepCount = oldPiece.stepCount
        piece?.canPathBeBlocked = oldPiece.canPathBeBlocked
        piece?.isAlive = oldPiece.isAlive

        oldBoard.tiles[currentTileCol + currentTileRow*8]?.chessPiece = piece
        oldBoard.tiles[currentTileCol + currentTileRow*8]?.isEmpty = false

        changeNextPlayer()
        drawBoard()

        return oldBoard

    }

    fun flipBoard(){
        buttonNames.reverse()
        for((idx, b) in buttons.withIndex()) {
            b.contentDescription = buttonNames[idx]
        }
        drawBoard()
        flippedBoard = !flippedBoard
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveBoard(){
        if(saved) return
        val boardState = board.toString()

        var save = BoardData(state=boardState,
            nextPlayer = currentPlayer,
            multiplayer = multiplayer,
            date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy MM dd HH:mm"))
        )

        thread{
            val insertId = localDatabase.BoardDataDAO().insert(save)
            save.id = insertId
//            lastId = insertId.toInt()
            saved = true
        }
    }
}