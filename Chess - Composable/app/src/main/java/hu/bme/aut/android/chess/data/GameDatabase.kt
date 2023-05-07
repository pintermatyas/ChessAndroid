package hu.bme.aut.android.chess.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BoardData::class], version = 1)
abstract class GameDatabase : RoomDatabase() {

    abstract fun BoardDataDAO(): BoardDataDAO

    companion object {
        fun getDatabase(applicationContext: Context): GameDatabase {

            return Room.databaseBuilder(
                applicationContext,
                GameDatabase::class.java,
                "board"
            ).build()
        }
    }
}