package hu.bme.aut.android.chess

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
            startActivity(intent)
        }
        binding.settingsbtn.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java).apply {  }
            startActivity(intent)
        }



        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        //firebase teszt
        database = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
        message = database.reference
//        message = Firebase.database.reference
//        message.child("users").child(username).setValue("")

//        message.setValue("teszt")

        message.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as Map<*, *>?
                val values = listOf(map?.values?.last())
//                toast(values.last().toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.tesztbtn.setOnClickListener {
            username = prefs.getString("username", "").toString()
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().time )
            message.child(date).setValue(binding.messengerteszt.text.toString())


        }


    }

    fun toast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }


}