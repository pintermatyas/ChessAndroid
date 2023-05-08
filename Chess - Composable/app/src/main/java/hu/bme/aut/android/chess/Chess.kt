package hu.bme.aut.android.chess

import android.app.Application
import androidx.room.Room
import hu.bme.aut.android.chess.data.GameDatabase
import hu.bme.aut.android.chess.data.GameRepositoryImpl

class Chess : Application() {

    companion object {
        private lateinit var db: GameDatabase

        lateinit var repository: GameRepositoryImpl
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            applicationContext,
            GameDatabase::class.java,
            "board"
        ).fallbackToDestructiveMigration().build()


        repository = GameRepositoryImpl(db.dao)
    }
}