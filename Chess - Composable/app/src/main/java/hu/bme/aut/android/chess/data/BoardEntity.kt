package hu.bme.aut.android.chess.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "boarddata")
data class BoardEntity(
    @ColumnInfo(name="id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name="state") var state: String,
    @ColumnInfo(name="nextPlayer") var nextPlayer: Int,
    @ColumnInfo(name="multiplayer") var multiplayer: Boolean,
    @ColumnInfo(name="opponent") var opponent: String,
    @ColumnInfo(name="date") var date: String
){}