package hu.bme.aut.android.chess.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val databaseSize = items.size
        val data = items[databaseSize - position-1]
        holder.binding.dateOfGame.text = data.date
        if(data.opponent != "null"){
            holder.binding.opponent.text = "Against ${data.opponent}"
        }
        holder.binding.root.setOnClickListener {
            listener.onItemClicked(data)
        }
        holder.binding.deletebtn.setOnClickListener {
            listener.onItemRemoved(data)
        }
        holder.binding.previewimg.background = BitmapDrawable(holder.binding.root.context.resources, getBoardBitmap(data.state))
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun update(shoppingItems: List<BoardData>) {
        items.clear()
        items.addAll(shoppingItems)
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBoardBitmap(boardString: String): Bitmap? {
        val multiplier = 32
        val size = 8*multiplier // 32*8

        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val realX = x/multiplier
                    val realY = y/multiplier
                    val char = boardString[realX+8*realY]
                    if(x%multiplier < 2 || y%multiplier < 2 || size-x < 2 || size-y < 2){
                        it.setPixel(x, size-y-1, Color.BLACK)
                        continue
                    }
                    if(char=='0'){
                        it.setPixel(x, size-y-1, if ((realX+realY) % 2 == 0) Color.rgb(191, 147, 52) else Color.rgb(245, 218, 159))
                    } else if(char.isUpperCase()){
                        it.setPixel(x, size-y-1, Color.BLACK)
                    }else if(char.isLowerCase()){
                        it.setPixel(x, size-y-1, Color.WHITE)
                    }
                }
            }
        }
    }

    inner class GameViewHolder(val binding: ItemGameBinding) : RecyclerView.ViewHolder(binding.root)


}
