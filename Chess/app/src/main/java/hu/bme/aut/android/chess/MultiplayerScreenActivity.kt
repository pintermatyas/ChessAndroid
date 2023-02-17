package hu.bme.aut.android.chess

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import hu.bme.aut.android.chess.databinding.ActivityMultiplayerScreenBinding
import kotlin.properties.Delegates

class MultiplayerScreenActivity : AppCompatActivity() {
    var searching = false
    var username: String = ""
    private var database: FirebaseDatabase? = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
    var message = database?.reference
    var onlinePlayers = ArrayList<String>()
    var logSize: Int = 0
    var logged = false
    var connected = false
    var opponent = ""
    private lateinit var log: ArrayList<*>
    private lateinit var binding: ActivityMultiplayerScreenBinding

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMultiplayerScreenBinding.inflate(layoutInflater)
        binding.root.setBackgroundResource(R.drawable.background)

        setContentView(binding.root)
        username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "").toString()

        log = ArrayList<String>()
        binding.searchingText.isVisible = false
        binding.backbutton.isVisible = false
        binding.scanbutton.isVisible = false


        binding.playrandom.setOnClickListener {
            searching = true
            binding.playrandom.isVisible = false
            binding.playfriend.isVisible = false
            binding.searchingText.isVisible = true
            message!!.child("players").child(username).setValue("online")
        }

        binding.playfriend.setOnClickListener {
            binding.playrandom.isVisible = false
            binding.playfriend.isVisible = false
            binding.idIVQrcode.isVisible = true
            binding.backbutton.isVisible = true
            binding.scanbutton.isVisible = true
            val bitmap: Bitmap = getQrCodeBitmap(username)
            binding.idIVQrcode.setImageBitmap(bitmap)

        }

        binding.backbutton.setOnClickListener {
            binding.playrandom.isVisible = true
            binding.playfriend.isVisible = true
            binding.idIVQrcode.isVisible = false
            binding.backbutton.isVisible = false
            binding.scanbutton.isVisible = false
        }




        message!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(searching){

                    val map = dataSnapshot.value as Map<*, *>?
                    onlinePlayers.clear()
                    opponent = ""

                    val oldLog = log




                    log = ArrayList<String>()

                    log = map!!["log"] as ArrayList<*>
                    logSize = log.size

                    if(oldLog == log && log.last().toString() == "entered") return

//                if(!joinLog){
//                    message!!.child("log").child(logSize.toString()).setValue("$username joined")
//                    joinLog = true
//                }


                    updateOnlinePlayers(map)





                    if(log.last().toString().contains(username) && !connected && oldLog != log){
                        message!!.child("games").child(log.last().toString()).child(username).setValue("entered")
                    }

                    var existingGame = false

                    var game: String = log.last().toString()


                    Log.d("GAME STATUS", game)

                    if(game == "entered"){
                        updateOnlinePlayers(map)
//                        Log.d("LOGGING STATUS", "LAST MESSAGE IS ENTERED")
                        log = map["log"] as ArrayList<*>
                        logSize = log.size
                        createPairing(map)
//                    return
                    }

                    val games: HashMap<*, *> = map["games"] as HashMap<*,*>
                    val gameKeys = games.keys

                    for(k in gameKeys){
                        if(k.toString().contains(username)){
                            existingGame = true
                            k.toString()
                        }
                    }


                    game = log.last().toString()

                    log = map["log"] as ArrayList<*>
                    logSize = log.size

                    Log.d("Connection status", "EXISTING: $existingGame, CONNECTED: ${connected}, ONLINE PLAYERS: $onlinePlayers")

                    if(existingGame && !connected && game != "entered"){

//                    if(!onlinePlayers.contains(opponent)){
//                        Log.d("TAG", "Opponent $opponent is not online")
//                        return
//                    }

//                    Toast.makeText(this@MultiplayerScreenActivity, "connecting to $game", Toast.LENGTH_SHORT).show()

                        Log.d("entered", "$username entered")
                        message!!.child("games").child(game).child(username).setValue("entered")
                        val intent = Intent(this@MultiplayerScreenActivity, GameViewActivity::class.java).apply {  }
                        intent.putExtra("multiplayer", true)

                        val players = log.last().toString().split(",")
                        Log.d("TAG","$username,     $game")
                        if(games[game] == null) {
                            Log.d("GAME IS NULL", "$username; $game")
                            return
                        }

                        if(games[game] !is HashMap<*,*>) return

                        val match = games[game] as HashMap<*,*>

                        opponent = if(players[0]==username) players[1] else players[0]

                        if(match["white"].toString()!=username && match["black"].toString() != username){
                            message!!.child("games").child(game).child("white").setValue("")
                            message!!.child("games").child(game).child("black").setValue("")
                        }
//                    if(match["white"].toString()=="null" || match["white"].toString()==""){
//                        message!!.child("games").child(game).child("white").setValue(username)
//                        message!!.child("games").child(game).child("black").setValue(opponent)
//                    }
                        intent.putExtra("opponent", opponent)
                        intent.putExtra("match", game)
                        message!!.child("players").child(username).setValue("unavailable")
                        this@MultiplayerScreenActivity.finish()
                        startActivity(intent)
                        finish()
                        searching = false

                        connected = true
                        return
                    }

                    else if(!logged && !(game.contains(",$username") || (game.contains("$username,")))){
                        log = map["log"] as ArrayList<*>
                        logSize = log.size
                        Log.d("LOGGING STATUS", "NOT LOGGED, NO GAME")

                        updateOnlinePlayers(map)

                        createPairing(map)
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        message!!.child("players").child(username).setValue("offline")
        searching = false
        logged = false
    }

    override fun onStop() {
        super.onStop()
        searching = false
    }

    override fun onResume() {
        super.onResume()
//        searching = true
//        message!!.child("players").child(username).setValue("online")
    }

    fun createPairing(map: Map<*,*>): String{
        Log.d("PAIRING", "CREATING PAIRING")
        opponent = ""
        updateOnlinePlayers(map)

        if(onlinePlayers.size==0){
            Log.d("NO ONLINE PLAYERS", "0 ONLINE PLAYERS")
            return ""
        }

        val size = onlinePlayers.size
        val rand = (0 until size).random()
        opponent = onlinePlayers[rand]

        Log.d("ONLINE PLAYERS AT $logSize", "$onlinePlayers")
        if(opponent == username) return ""


        Log.d("MATCH LOGGED AT $logSize", "$opponent,$username")
        message!!.child("log").child(logSize.toString()).setValue("$opponent,$username")
        logged = true
        Log.d("OPPONENT FOR PLAYER $username", opponent)
        return opponent
    }

    fun updateOnlinePlayers(map: Map<*,*>): String{
        var state = ""
        if (map["players"] is HashMap<*, *>) {
            Log.d("PLAYER STATUSES", "${map["players"]}")
            val entries: HashMap<*, *> = map["players"] as HashMap<*, *>
            val keys = entries.keys.toMutableList()
            val values = entries.values.toMutableList()
            for ((idx, _) in keys.withIndex()) {
                if(keys[idx].toString() == username){
                    state = values[idx].toString()
                }
                if (keys[idx].toString() != username && values[idx].toString() == "online") {
                    onlinePlayers.add(keys[idx].toString())
                }
            }
            Log.d("ONLINE PLAYERS UPDATED", "$onlinePlayers")
        }
        return state
    }

    fun getQrCodeBitmap(encodedString: String): Bitmap {
        val size = 512 //pixels
        val qrCodeContent = encodedString
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }



}