package hu.bme.aut.android.chess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MultiplayerScreenActivity : AppCompatActivity() {
    var username: String = ""
    var database: FirebaseDatabase? = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
    var message = database?.reference
    var onlinePlayers = ArrayList<String>()
    var logSize: Int = 0
    var logged = false
    var connected = false
    private lateinit var log: ArrayList<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer_screen)
        username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "").toString()
        message!!.child("players").child(username).setValue("online")

        message!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as Map<*, *>?
                onlinePlayers.clear()
                var opponent = ""
                var state: String = ""

                log = map!!["log"] as ArrayList<*>
                logSize = log.size
//                if(!joinLog){
//                    message!!.child("log").child(logSize.toString()).setValue("$username joined")
//                    joinLog = true
//                }




                if (map.get("players") is HashMap<*, *>) {
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
                }

                if(log.last().toString().contains(username) && !connected){
                    message!!.child("games").child(log.last().toString()).child(username).setValue("entered")
                }

                val games = map["games"] as HashMap<*,*>
                val gameKeys = games.keys
                var existingGame = false
                var game = ""

                for(k in gameKeys){
                    if(k.toString().contains(username)){
                        existingGame = true
                        game = k.toString()
                    }
                }

                game = log.last().toString()

                if(existingGame && !connected){

                    Toast.makeText(this@MultiplayerScreenActivity, "connecting to $game", Toast.LENGTH_SHORT).show()

                    message!!.child("games").child(game).child(username).setValue("entered")
                    val intent = Intent(this@MultiplayerScreenActivity, GameViewActivity::class.java).apply {  }
                    intent.putExtra("multiplayer", true)

                    val players = log.last().toString().split(",")
                    val match = games[game] as HashMap<*,*>
                    opponent = if(players[0]==username) players[1] else players[0]
                    if(match["white"].toString()=="null" || match["white"].toString()==""){
                        message!!.child("games").child(game).child("white").setValue(username)
                        message!!.child("games").child(game).child("black").setValue(opponent)
                    }
                    intent.putExtra("opponent", opponent)
                    intent.putExtra("match", game)
                    message!!.child("players").child(username).setValue("unavailable")
                    finish()
                    startActivity(intent)

                    connected = true
                    return
                }

                if(!existingGame && !logged){
                    createPairing()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        message!!.child("players").child(username).setValue("offline")
        logged = false
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        message!!.child("players").child(username).setValue("online")
    }

    fun createPairing(): String{
        if(onlinePlayers.size==0){
            return ""
        }

        val size = onlinePlayers.size
        val rand = (0 until size).random()
        val opponent = onlinePlayers[rand]

        message!!.child("log").child(logSize.toString()).setValue("$opponent,$username")
        logged = true
        if(opponent == username) return ""
        return opponent
    }



}