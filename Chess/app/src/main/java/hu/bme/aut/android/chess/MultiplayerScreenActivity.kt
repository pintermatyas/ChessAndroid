package hu.bme.aut.android.chess

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
    var database = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
    var message = database.reference
    var onlinePlayers = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer_screen)
        username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "").toString()
        message.child("players").child(username).setValue("online")

        message.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                Toast.makeText(this@MultiplayerScreenActivity, "asdasdasd", Toast.LENGTH_SHORT).show()
                val map = dataSnapshot.value as Map<*, *>?
                onlinePlayers.clear()
                if (map!!.get("players") is HashMap<*, *>) {
                    val entries: HashMap<*, *> = map.get("players") as HashMap<*, *>
                    val keys = entries.keys.toMutableList()
                    val values = entries.values.toMutableList()
                    var state: String = ""
                    for ((idx, e) in keys.withIndex()) {
                        if(keys[idx].toString() == username){
                            state = values[idx].toString()
                        }
                        if (keys[idx].toString() != username && values[idx].toString() == "online") {
                            onlinePlayers.add(keys[idx].toString())
                        }
                    }
                    if(state != "unavailable"){
                        createMatch()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        createMatch()
    }

    override fun onPause() {
        super.onPause()
        message.child("players").child(username).setValue("offline")
    }

    override fun onResume() {
        super.onResume()
        message.child("players").child(username).setValue("online")
    }

    fun createMatch(){
        if(onlinePlayers.size==0){
            return
        }

        val size = onlinePlayers.size
        val rand = (0..size).random()
        Toast.makeText(this, rand.toString(), Toast.LENGTH_SHORT).show()
//        val opponent = onlinePlayers[rand]



    }
}