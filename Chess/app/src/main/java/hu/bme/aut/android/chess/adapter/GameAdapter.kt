package hu.bme.aut.android.chess.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.chess.GameViewActivity
import hu.bme.aut.android.chess.ListActivity
import hu.bme.aut.android.chess.data.BoardData
import hu.bme.aut.android.chess.databinding.ItemGameBinding

class GameAdapter(private val listener: GameItemClickListener) :RecyclerView.Adapter<GameAdapter.GameViewHolder>() {


    private val items = mutableListOf<BoardData>()

    interface GameItemClickListener{
        fun onItemChanged(item: BoardData)
        fun onItemRemoved(item: BoardData)
        fun onItemClicked(item: BoardData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GameViewHolder(
        ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val databaseSize = items.size
        val data = items[databaseSize - position-1]
        holder.binding.dateOfGame.text = data.date
        holder.binding.root.setOnClickListener {
            listener.onItemClicked(data)
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun update(shoppingItems: List<BoardData>) {
        items.clear()
        items.addAll(shoppingItems)
        notifyDataSetChanged()
    }

    inner class GameViewHolder(val binding: ItemGameBinding) : RecyclerView.ViewHolder(binding.root)


}
