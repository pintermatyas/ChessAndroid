package hu.bme.aut.android.chess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.firebase.database.*
import hu.bme.aut.android.chess.databinding.ActivityMainBinding

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



    }

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        binding.multiplayer.isVisible = prefs.getBoolean("multiplayer", false).toString()=="true"
    }

}