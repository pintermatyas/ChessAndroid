package hu.bme.aut.android.chess.domain.usecases

import hu.bme.aut.android.chess.data.GameRepository

class GameUseCases (repository: GameRepository) {
    val loadGames = LoadGamesUseCase(repository)
    val loadGame = LoadGameUseCase(repository)
    val saveGame = SaveGameUseCase(repository)
    val updateGame = UpdateGameUseCase(repository)
    val deleteGame = DeleteGameUseCase(repository)
}