package hu.bme.aut.android.chess.compose.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.android.chess.GameViewActivity
import hu.bme.aut.android.chess.R
import hu.bme.aut.android.chess.data.BoardEntity
import hu.bme.aut.android.chess.domain.model.asBoardEntity
import hu.bme.aut.android.chess.feature.list.GamesViewModel


@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    viewModel: GamesViewModel = viewModel(factory = GamesViewModel.Factory)
){
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (!state.isLoading && !state.isError) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.background
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            } else if (state.isError) {
                Text(
                    text = "We run into some trouble."
                )
            } else {
                if (state.games.isEmpty()) {
                    Text(text = "You haven't played any games yet.")
                } else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(0.98f)
                            .padding(it)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        items(state.games.size) { i ->
                           GameListItem(
                               game = state.games[state.games.size-i-1].asBoardEntity(),
                               deleteItem = {
                                   viewModel.deleteGame(state.games[state.games.size-i-1].asBoardEntity())
                               },
                               openGame = {
                                   val game = state.games[state.games.size-i-1].asBoardEntity()
                                   val intent = Intent(context, GameViewActivity::class.java)
                                   intent.putExtra("replay", true)
                                   intent.putExtra("opponent", game.opponent)
                                   intent.putExtra("state", game.state)
                                   intent.putExtra("nextPlayer", game.nextPlayer)
                                   context.startActivity(intent)
                               }
                           )
                           if (i != state.games.lastIndex) {
                               Divider(
                                   thickness = 2.dp,
                                   color = MaterialTheme.colorScheme.secondaryContainer
                               )
                           }
                        }
                    }
                }
            }
        }

    }


}



@Composable
fun GameListItem(
    game: BoardEntity,
    deleteItem: () -> Unit,
    openGame: () -> Unit
) {

    val context = LocalContext.current
    val bitmap = getBoardBitmap(game.state, game.nextPlayer)?.asImageBitmap()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = openGame)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        // Date of game and opponent
        Column(
        ) {
            Text(
                text = game.date,
                fontSize = 25.sp
            )
            Text(
                text = game.opponent,
                fontSize = 25.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Preview image
        Image(
            bitmap = bitmap!!,
            contentDescription = stringResource(R.string.bitmap_of_the_saved_board),
            modifier = Modifier
                .size(66.dp)
        )

        // Delete button
        IconButton(
            onClick = deleteItem,
            modifier = Modifier
                .size(66.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete_button_for_item)
            )
        }

    }
    // Divider
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = Color.DarkGray)
    )
}


@Preview
@Composable
fun ItemRowPreview(){

    GameListItem(
        BoardEntity(0,"pppppppp000000000000000000000000000000000000000000000000PPPPPPPP",0, true, "opponent","20230515"),
        {},
        {}
    )
}