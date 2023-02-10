package hu.bme.aut.android.chess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates

class MultiplayerScreenActivity : AppCompatActivity() {
    var running by Delegates.notNull<Boolean>()
    var username: String = ""
    var database: FirebaseDatabase? = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
    var message = database?.reference
    var onlinePlayers = ArrayList<String>()
    var logSize: Int = 0
    var logged = false
    var connected = false
    var opponent = ""
    private lateinit var log: ArrayList<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        running = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer_screen)
        username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "").toString()
        message!!.child("players").child(username).setValue("online")

        log = ArrayList<String>()

        message!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(running){

                    val map = dataSnapshot.value as Map<*, *>?
                    onlinePlayers.clear()
                    var state: String = ""
                    opponent = ""


                    log = ArrayList<String>()

                    log = map!!["log"] as ArrayList<*>
                    logSize = log.size
//                if(!joinLog){
//                    message!!.child("log").child(logSize.toString()).setValue("$username joined")
//                    joinLog = true
//                }


                    state = updateOnlinePlayers(map)





                    if(log.last().toString().contains(username) && !connected){
                        message!!.child("games").child(log.last().toString()).child(username).setValue("entered")
                    }

                    var existingGame = false
                    var game = ""

                    game = log.last().toString()


                    Log.d("GAME STATUS", game)

                    if(game == "entered"){
                        state = updateOnlinePlayers(map)
                        Log.d("LOGGING STATUS", "LAST MESSAGE IS ENTERED")
                        log = map!!["log"] as ArrayList<*>
                        logSize = log.size
                        createPairing(map)
//                    return
                    }

                    var games: HashMap<*, *> = map["games"] as HashMap<*,*>
                    var gameKeys = games.keys

                    for(k in gameKeys){
                        if(k.toString().contains(username)){
                            existingGame = true
                            game = k.toString()
                        }
                    }


                    game = log.last().toString()

                    log = map!!["log"] as ArrayList<*>
                    logSize = log.size

                    Log.d("Connection status", "EXISTING: ${existingGame.toString()}, CONNECTED: ${connected.toString()}, ONLINE PLAYERS: ${onlinePlayers.toString()}")

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
                        existingGame = false
                        running = false

                        connected = true
                        return
                    }

                    else if(!logged && !(game.contains(",$username") || (game.contains("$username,")))){
                        log = map!!["log"] as ArrayList<*>
                        logSize = log.size
                        Log.d("LOGGING STATUS", "NOT LOGGED, NO GAME")

                        state = updateOnlinePlayers(map)

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
        running = false
        logged = false
    }

    override fun onStop() {
        super.onStop()
        running = false
    }

    override fun onResume() {
        super.onResume()
        running = true
        message!!.child("players").child(username).setValue("online")
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
        Log.d("OPPONENT FOR PLAYER $username", "$opponent")
        return opponent
    }

    fun updateOnlinePlayers(map: Map<*,*>): String{
        var state: String = ""
        if (map.get("players") is HashMap<*, *>) {
            Log.d("PLAYER STATUSES", "${map.get("players")}")
            val entries: HashMap<*, *> = map["players"] as HashMap<*, *>
            val keys = entries.keys.toMutableList()
            val values = entries.values.toMutableList()
            for ((idx, e) in keys.withIndex()) {
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



}