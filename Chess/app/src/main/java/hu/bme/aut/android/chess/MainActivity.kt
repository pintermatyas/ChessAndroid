package hu.bme.aut.android.chess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import hu.bme.aut.android.chess.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
    }


}