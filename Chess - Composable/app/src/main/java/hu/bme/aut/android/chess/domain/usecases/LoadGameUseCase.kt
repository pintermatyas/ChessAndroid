package hu.bme.aut.android.chess.domain.usecases

import hu.bme.aut.android.chess.data.GameRepository
import hu.bme.aut.android.chess.domain.model.GameData
import hu.bme.aut.android.chess.domain.model.asGameData
import kotlinx.coroutines.flow.first
import java.io.IOException

class LoadGameUseCase (private val repository: GameRepository) {

    suspend operator fun invoke(id: Int): Result<GameData> {
        return try {
            Result.success(repository.getGameById(id).first().asGameData())
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

}