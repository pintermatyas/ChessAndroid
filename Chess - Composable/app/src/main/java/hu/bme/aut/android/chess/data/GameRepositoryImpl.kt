package hu.bme.aut.android.chess.data

import kotlinx.coroutines.flow.Flow

class GameRepositoryImpl(private val dao: BoardDataDAO) : GameRepository {

    override fun getAllGames(): Flow<List<BoardEntity>> = dao.getAll()

    override fun getGameById(id: Int): Flow<BoardEntity> = dao.getGameById(id)

    override suspend fun insertGame(game: BoardEntity) { dao.insert(game) }

    override suspend fun updateGame(game: BoardEntity) { dao.update(game) }

    override suspend fun deleteGame(game: BoardEntity) { dao.deleteItem(game) }
}