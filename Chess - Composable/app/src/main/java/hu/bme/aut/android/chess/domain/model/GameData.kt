package hu.bme.aut.android.chess.domain.model

import hu.bme.aut.android.chess.data.BoardEntity

data class GameData(
    val id: Long?,
    val date: String,
    val opponent: String,
    val nextPlayer: Int,
    val multiplayer: Boolean,
    val state: String
)

fun BoardEntity.asGameData(): GameData = GameData(
    id = id,
    date = date,
    opponent = opponent,
    nextPlayer = nextPlayer,
    multiplayer = multiplayer,
    state = state
)

fun GameData.asBoardEntity(): BoardEntity = BoardEntity(
    id = id,
    date = date,
    opponent = opponent,
    nextPlayer = nextPlayer,
    multiplayer = multiplayer,
    state = state
)
