package hu.bme.aut.android.chess.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BoardEntity::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract val dao: BoardDataDAO
}