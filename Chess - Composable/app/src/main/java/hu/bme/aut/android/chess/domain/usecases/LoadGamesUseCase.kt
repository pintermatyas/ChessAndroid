package hu.bme.aut.android.chess.domain.usecases

import hu.bme.aut.android.chess.data.GameRepository
import hu.bme.aut.android.chess.domain.model.GameData
import hu.bme.aut.android.chess.domain.model.asGameData
import kotlinx.coroutines.flow.first
import java.io.IOException

class LoadGamesUseCase(private val repository: GameRepository) {

    suspend operator fun invoke(): Result<List<GameData>> {
        return try {
            val games = repository.getAllGames().first()
            Result.success(games.map { it.asGameData() })
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}