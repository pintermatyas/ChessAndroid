package hu.bme.aut.android.chess.compose.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import hu.bme.aut.android.chess.GameViewActivity
import hu.bme.aut.android.chess.R
import hu.bme.aut.android.chess.compose.ui.Screen
import hu.bme.aut.android.chess.compose.ui.common.getQrCodeBitmap

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun QRCodeScreen(
    username: String,
    QRCodeBitmap: Bitmap,
    navController: NavHostController
) {
    val context = LocalContext.current
    val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
    val message = database.reference
    val onlinePlayers = ArrayList<String>()
    var opponent = ""
    var log = ArrayList<String>()
    var logSize = 0
    var logged = false
    var connected = false
    var searching = false
    var friendMatch = true

    Scaffold(
    ) {
        Image(painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                bitmap = QRCodeBitmap.asImageBitmap(),
                contentDescription = "QR code"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                          navController.navigate(Screen.QRCodeReaderScreen.route)
                          },
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black)
            ) {
                Text(text = "Scan QR Code")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                          },
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black)
            ) {
                Text(text = "BACK")
            }
        }
    }



    message.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if(friendMatch){
                val map = dataSnapshot.value as Map<*, *>?
                onlinePlayers.clear()
                opponent = ""
                val oldLog = log
                log = java.util.ArrayList<String>()
                log = map!!["log"] as ArrayList<String>
                logSize = log.size
                if(oldLog == log && log.last().toString() == "entered" && !friendMatch) return
                updateOnlinePlayers(map)
                if(log.last().toString().contains(username) && !connected && oldLog != log){
                    message.child("games").child(log.last().toString()).child(username).setValue("entered")
                }
                var existingGame = false
                var game: String = log.last().toString()
                Log.d("GAME STATUS", game)
                if(game == "entered" && !friendMatch){
                    updateOnlinePlayers(map)
                    log = map["log"] as ArrayList<String>
                    logSize = log.size
                    createPairing(map)
                }
                val games: java.util.HashMap<*, *> = map["games"] as java.util.HashMap<*, *>
                val gameKeys = games.keys
                var friendlyGame = ""
                for(k in gameKeys){
                    if(k.toString().contains(username)){
                        existingGame = true
                        friendlyGame = k.toString()
                    }
                }
                if(friendMatch && existingGame){
                    game = friendlyGame
                    message.child("games").child(game).child(username).setValue("entered")
                    val intent = Intent(context, GameViewActivity::class.java).apply {  }
                    intent.putExtra("multiplayer", true)
                    val players = friendlyGame.split(",")
                    Log.d("TAG","$username,     $game")
                    if(games[game] == null) {
                        Log.d("GAME IS NULL", "$username; $game")
                        return
                    }
                    if(games[game] !is java.util.HashMap<*, *>) return
                    val match = games[game] as java.util.HashMap<*, *>
                    opponent = if(players[0]==username) players[1] else players[0]
                    if(match["white"].toString()!=username && match["black"].toString() != username){
                        message.child("games").child(game).child("white").setValue("")
                        message.child("games").child(game).child("black").setValue("")
                    }
                    intent.putExtra("opponent", opponent)
                    intent.putExtra("match", game)
                    message.child("players").child(username).setValue("unavailable")
                    context.startActivity(intent)
                    (context as Activity).finish()
                    searching = false
                    friendMatch = false
                    return
                } else {
                    game = log.last().toString()
                }
                log = map["log"] as ArrayList<String>
                logSize = log.size
                Log.d("Connection status", "EXISTING: $existingGame, CONNECTED: ${connected}, ONLINE PLAYERS: $onlinePlayers")
                if((existingGame && !connected && game != "entered") || (existingGame && friendMatch) ){
                    Log.d("entered", "$username entered")
                    message.child("games").child(game).child(username).setValue("entered")
                    val intent = Intent(context, GameViewActivity::class.java).apply {  }
                    intent.putExtra("multiplayer", true)
                    var players = log.last().toString().split(",")
                    if(friendMatch) players = friendlyGame.split(",")
                    Log.d("TAG","$username,     $game")
                    if(games[game] == null) {
                        Log.d("GAME IS NULL", "$username; $game")
                        return
                    }
                    if(games[game] !is java.util.HashMap<*, *>) return
                    val match = games[game] as java.util.HashMap<*, *>
                    opponent = if(players[0]==username) players[1] else players[0]
                    if(match["white"].toString()!=username && match["black"].toString() != username){
                        message.child("games").child(game).child("white").setValue("")
                        message.child("games").child(game).child("black").setValue("")
                    }
                    intent.putExtra("opponent", opponent)
                    intent.putExtra("match", game)
                    message.child("players").child(username).setValue("unavailable")
                    context.startActivity(intent)
                    (context as Activity).finish()
                    searching = false
                    connected = true
                    return
                }
                else if(!logged && !(game.contains(",$username") || (game.contains("$username,")))){
                    log = map["log"] as ArrayList<String>
                    logSize = log.size
                    Log.d("LOGGING STATUS", "NOT LOGGED, NO GAME")
                    updateOnlinePlayers(map)
                    createPairing(map)
                }
            }


        }

        fun createPairing(map: Map<*,*>): String{
            Log.d("PAIRING", "CREATING PAIRING")
            opponent = ""
            updateOnlinePlayers(map)
            if(onlinePlayers.size==0){
                Log.d("NO ONLINE PLAYERS", "0 ONLINE PLAYERS")
                return ""
            }
            val size = onlinePlayers.size
            val rand = (0 until size).random()
            opponent = onlinePlayers[rand]
            Log.d("ONLINE PLAYERS AT $logSize", "$onlinePlayers")
            if(opponent == username) return ""
            Log.d("MATCH LOGGED AT $logSize", "$opponent,$username")
            message.child("log").child(logSize.toString()).setValue("$opponent,$username")
            logged = true
            Log.d("OPPONENT FOR PLAYER $username", opponent)
            return opponent
        }

        fun updateOnlinePlayers(map: Map<*,*>): String{
            var state = ""
            if (map["players"] is HashMap<*, *>) {
                Log.d("PLAYER STATUSES", "${map["players"]}")
                val entries: HashMap<*, *> = map["players"] as HashMap<*, *>
                val keys = entries.keys.toMutableList()
                val values = entries.values.toMutableList()
                for ((idx, _) in keys.withIndex()) {
                    if(keys[idx].toString() == username){
                        state = values[idx].toString()
                    }
                    if (keys[idx].toString() != username && values[idx].toString() == "online") {
                        onlinePlayers.add(keys[idx].toString())
                    }
                }
                Log.d("ONLINE PLAYERS UPDATED", "$onlinePlayers")
            }
            return state
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

}

@ExperimentalMaterial3Api
@Preview
@Composable
fun QRCodeScreenPreview() {
    MaterialTheme {
        QRCodeScreen(
            "username",
            getQrCodeBitmap("username"),
            rememberNavController()
            )
    }
}

