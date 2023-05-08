package hu.bme.aut.android.chess.domain.usecases

import hu.bme.aut.android.chess.data.BoardEntity
import hu.bme.aut.android.chess.data.GameRepository

class DeleteGameUseCase(private val repository: GameRepository) {

    suspend operator fun invoke(game: BoardEntity) {
        repository.deleteGame(game)
    }

}