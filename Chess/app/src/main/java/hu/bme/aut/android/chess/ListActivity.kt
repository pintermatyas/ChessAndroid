package hu.bme.aut.android.chess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.chess.adapter.GameAdapter
import hu.bme.aut.android.chess.data.BoardData
import hu.bme.aut.android.chess.data.GameDatabase
import hu.bme.aut.android.chess.databinding.ActivityListBinding
import kotlin.concurrent.thread

class ListActivity : AppCompatActivity(), GameAdapter.GameItemClickListener {

    private lateinit var localDatabase: GameDatabase
    private lateinit var binding: ActivityListBinding
    private lateinit var adapter: GameAdapter
//    private lateinit var listener: GameAdapter.GameItemClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListBinding.inflate(layoutInflater)
        adapter = GameAdapter(this)
        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)
        supportActionBar?.hide()


        localDatabase = GameDatabase.getDatabase(this)
        binding.rvMain.adapter = adapter

        initRecyclerView()

    }

    override fun onItemChanged(item: BoardData) {
        thread {
            localDatabase.BoardDataDAO().update(item)
            Log.d("ListActivity", "Update was successful")
        }
    }

    override fun onItemRemoved(item: BoardData) {
        thread{
            localDatabase.BoardDataDAO().deleteItem(item)
            Log.d("ListActivity", "Delete was successful")
            val items = localDatabase.BoardDataDAO().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemClicked(item: BoardData) {
        val intent = Intent(this@ListActivity, GameViewActivity::class.java).apply {  }
        intent.putExtra("replay", true)
        intent.putExtra("state", item.state)
        intent.putExtra("nextPlayer", item.nextPlayer)
        startActivity(intent)
    }

    private fun initRecyclerView() {
        adapter = GameAdapter(this)
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            val items = localDatabase.BoardDataDAO().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

}