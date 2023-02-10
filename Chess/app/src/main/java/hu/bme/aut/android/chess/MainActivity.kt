package hu.bme.aut.android.chess

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import hu.bme.aut.android.chess.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    //firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var message: DatabaseReference
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startbtn.setOnClickListener {
            val intent = Intent(this@MainActivity, GameViewActivity::class.java).apply {  }
            intent.putExtra("multiplayer", false)
            startActivity(intent)
        }
        binding.settingsbtn.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java).apply {  }
            startActivity(intent)
        }


        binding.multiplayer.setOnClickListener {
            username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "").toString()
            if(username == ""){
                Toast.makeText(this, "Username is not set!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            message.child("players").child(username).setValue("online")

            val intent = Intent(this@MainActivity, MultiplayerScreenActivity::class.java).apply {  }
            intent.putExtra("multiplayer", true)
            startActivity(intent)
        }

        //Ha ki van kapcsolva a multiplayer a beállításokban, nem jelenik meg a gomb
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        binding.multiplayer.isVisible = prefs.getBoolean("multiplayer", false).toString()=="false"


        //firebase teszt
        database = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
        message = database.reference
//        message.child("users").child(username).setValue("")


        message.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as Map<*, *>?
                val values = listOf(map?.values?.last())
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    override fun onResume() {
        super.onResume()
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        binding.multiplayer.isVisible = prefs.getBoolean("multiplayer", false).toString()=="true"
    }

    fun toast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }


}