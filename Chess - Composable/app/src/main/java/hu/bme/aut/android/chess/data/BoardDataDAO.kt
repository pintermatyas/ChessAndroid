package hu.bme.aut.android.chess.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDataDAO {
    @Query("SELECT * FROM boarddata")
    fun getAll(): Flow<List<BoardEntity>>

    @Insert
    fun insert(board: BoardEntity): Long

    @Query("SELECT * FROM boarddata WHERE id = :id")
    fun getGameById(id: Int): Flow<BoardEntity>

    @Update
    fun update(board: BoardEntity)

    @Delete
    fun deleteItem(board: BoardEntity)

    @Query("DELETE FROM boarddata")
    fun nukeTable()
}