package hu.bme.aut.android.chess.domain.usecases

import hu.bme.aut.android.chess.data.GameRepository
import hu.bme.aut.android.chess.domain.model.GameData
import hu.bme.aut.android.chess.domain.model.asBoardEntity

class SaveGameUseCase(private val repository: GameRepository) {

    suspend operator fun invoke(game: GameData) {
        repository.insertGame(game.asBoardEntity())
    }

}