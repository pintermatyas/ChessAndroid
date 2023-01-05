package hu.bme.aut.android.chess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import hu.bme.aut.android.chess.Board.Board
import hu.bme.aut.android.chess.databinding.ActivityGameViewBinding
import hu.bme.aut.android.chess.databinding.ActivityMainBinding

class GameViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_view)
        binding = ActivityGameViewBinding.inflate(layoutInflater)

        var board = Board()

        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
    }
}