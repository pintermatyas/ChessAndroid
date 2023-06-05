package hu.bme.aut.android.chess.data

import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getAllGames(): Flow<List<BoardEntity>>

    fun getGameById(id: Int): Flow<BoardEntity>

    suspend fun insertGame(game: BoardEntity)

    suspend fun updateGame(game: BoardEntity)

    suspend fun deleteGame(game: BoardEntity)
}