package hu.bme.aut.android.chess.data

import androidx.room.*

@Dao
interface BoardDataDAO {
    @Query("SELECT * FROM boarddata")
    fun getAll(): List<BoardData>

    @Insert
    fun insert(board: BoardData): Long

    @Update
    fun update(board: BoardData)

    @Delete
    fun deleteItem(board: BoardData)

    @Query("DELETE FROM boarddata")
    fun nukeTable()
}