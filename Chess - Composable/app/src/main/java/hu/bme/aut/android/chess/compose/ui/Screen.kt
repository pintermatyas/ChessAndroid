package hu.bme.aut.android.chess.compose.ui

sealed class Screen(val route: String) {
    object MainMenuScreen: Screen("main_menu")
    object MultiplayerMainMenuScreen: Screen("multiplayer_main_menu")
    object MultiplayerSearchingScreen: Screen("multiplayer_searching")
    object QRCodeScreen: Screen("qr_code")
    object QRCodeReaderScreen: Screen("qr_code_reader")
}