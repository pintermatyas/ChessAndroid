package hu.bme.aut.android.chess.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import hu.bme.aut.android.chess.Chess
import hu.bme.aut.android.chess.data.BoardEntity
import hu.bme.aut.android.chess.domain.model.GamesState
import hu.bme.aut.android.chess.domain.usecases.GameUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GamesViewModel(
    private val gameOperations: GameUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(GamesState())
    val state = _state.asStateFlow()

    init {
        loadGames()
    }
    private fun loadGames() {

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
                    val games = gameOperations.loadGames().getOrThrow()
                    _state.update { it.copy(
                        isLoading = false,
                        games = games
                    ) }
                }
            } catch (e: Exception) {
                _state.update {  it.copy(
                    isLoading = false,
                    error = e
                ) }
            }
        }
    }

    public fun deleteGame(boardEntity: BoardEntity){
        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
            try {
                CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
                    gameOperations.deleteGame(boardEntity)
                    val games = gameOperations.loadGames().getOrThrow()
                    _state.update { it.copy(
                        isLoading = false,
                        games = games
                    ) }
                }
            } catch (e: Exception) {
                _state.update {  it.copy(
                    isLoading = false,
                    error = e
                ) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val gameOperations = GameUseCases(Chess.repository)
                GamesViewModel(
                    gameOperations = gameOperations
                )
            }
        }
    }
}
