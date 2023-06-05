package hu.bme.aut.android.chess.compose.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import hu.bme.aut.android.chess.GameViewActivity
import hu.bme.aut.android.chess.R
import hu.bme.aut.android.chess.compose.ui.Screen
import hu.bme.aut.android.chess.isOnline
import hu.bme.aut.android.chess.preferences.SettingsActivity

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun MainMenuScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, SettingsActivity::class.java).apply {  }
                    context.startActivity(intent)
                },
                containerColor = Color.Green
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.Black
                )
            }
        }
    ) {
        Image(painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Button(
                onClick = {
                    val intent = Intent(context, GameViewActivity::class.java).apply {  }
                    intent.putExtra("multiplayer", false)
                    intent.putExtra("replay", false)
                    context.startActivity(intent)
                          },
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black)
            ) {
                Text(text = "Start game")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val networkAccess = context.isOnline()
                    if(PreferenceManager.getDefaultSharedPreferences(context).getString("username", "").toString() != "" && networkAccess){
                        navController.navigate(Screen.MultiplayerMainMenuScreen.route)
                    }
                    else if(networkAccess){
                        Toast.makeText(context, "You need to set a username first!", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(context, "You are not connected to the Internet!", Toast.LENGTH_LONG).show()
                    }
                          },
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black)
            ) {
                Text(text = "Play online")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.ListScreen.route)
                          },
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black)
            ) {
                Text(text = "View previous games")
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun MainMenuScreenPreview() {
    MaterialTheme {
        MainMenuScreen(rememberNavController())
    }
}
