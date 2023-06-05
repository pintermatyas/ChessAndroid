package hu.bme.aut.android.chess

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.chess.compose.ui.Navigation

class MainActivity : AppCompatActivity() {
    //firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var message: DatabaseReference
    private var username: String = ""


    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
        message = database.reference

        setContent {
            username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "").toString()
            Navigation(
                username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "").toString()
            )
        }

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                ),
                0
            )
            return
        }


    }

}