package hu.bme.aut.android.chess

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.chess.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    //firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var message: DatabaseReference
    private var username: String = ""

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

//
//        FirebaseMessaging.getInstance().getToken()
//
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
//            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//        })

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.root.setBackgroundResource(R.drawable.background)
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
        binding.gameLogBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, ListActivity::class.java).apply {  }
            startActivity(intent)
        }


        binding.multiplayer.setOnClickListener {
            username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "").toString()
            if(username == ""){
                Toast.makeText(this, "Username is not set!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
//            message.child("players").child(username).setValue("online")

            val intent = Intent(this@MainActivity, MultiplayerScreenActivity::class.java).apply {  }
            intent.putExtra("multiplayer", true)
            startActivity(intent)
        }

        //Ha ki van kapcsolva a multiplayer a be??ll??t??sokban, nem jelenik meg a gomb
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        binding.multiplayer.isVisible = prefs.getBoolean("multiplayer", false).toString()=="false"


        //firebase teszt
        database = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
        message = database.reference

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                ),
                0
            )
            return
        }


    }

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        binding.multiplayer.isVisible = prefs.getBoolean("multiplayer", false).toString()=="true"
    }

}