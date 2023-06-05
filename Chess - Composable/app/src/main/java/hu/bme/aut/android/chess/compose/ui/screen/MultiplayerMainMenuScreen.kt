package hu.bme.aut.android.chess.compose.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.chess.R
import hu.bme.aut.android.chess.compose.ui.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun MultiplayerMainMenuScreen(
    username: String,
    navController: NavHostController
) {
    val context = LocalContext.current

    val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
    val message = database?.reference
    message!!.child("players").child(username).setValue("offline")

    Scaffold(
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
                          navController.navigate(Screen.MultiplayerSearchingScreen.route)
                },
                modifier = Modifier
                    .height(50.dp)
                    .width(250.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black)
            ) {
                Text(text = "Play with random opponent")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.QRCodeScreen.route)
                          },
                modifier = Modifier
                    .height(50.dp)
                    .width(250.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black)
            ) {
                Text(text = "Play with friends")
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun MultiplayerMainMenuScreenPreview() {
    MaterialTheme {
        MultiplayerMainMenuScreen("username", rememberNavController())
    }
}
