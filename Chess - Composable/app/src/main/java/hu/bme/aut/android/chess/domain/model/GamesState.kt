package hu.bme.aut.android.chess.domain.model

data class GamesState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isError: Boolean = error != null,
    val games: List<GameData> = emptyList()
)