package hu.bme.aut.android.chess.compose.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import hu.bme.aut.android.chess.compose.ui.common.getQrCodeBitmap
import hu.bme.aut.android.chess.compose.ui.screen.ListScreen
import hu.bme.aut.android.chess.compose.ui.screen.MainMenuScreen
import hu.bme.aut.android.chess.compose.ui.screen.MultiplayerMainMenuScreen
import hu.bme.aut.android.chess.compose.ui.screen.MultiplayerSearchingScreen
import hu.bme.aut.android.chess.compose.ui.screen.QRCodeReaderScreen
import hu.bme.aut.android.chess.compose.ui.screen.QRCodeScreen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation(
    username: String
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainMenuScreen.route) {
            composable(Screen.MainMenuScreen.route) {
                MainMenuScreen(navController)
            }
            composable(Screen.MultiplayerMainMenuScreen.route) {
                MultiplayerMainMenuScreen(username,navController)
            }
            composable(Screen.MultiplayerSearchingScreen.route) {
                MultiplayerSearchingScreen(
                    username,
                    navController
                )
            }
            composable(Screen.QRCodeScreen.route) {
                QRCodeScreen(
                    username,
                    getQrCodeBitmap(username),
                    navController
                )
            }
            composable(Screen.QRCodeReaderScreen.route) {
                QRCodeReaderScreen(
                    username,
                    navController
                )
            }
            composable(Screen.ListScreen.route) {
                ListScreen()
            }
    }
}