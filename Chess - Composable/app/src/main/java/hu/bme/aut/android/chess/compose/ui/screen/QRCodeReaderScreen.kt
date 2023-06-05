package hu.bme.aut.android.chess.compose.ui.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.chess.GameViewActivity

@ExperimentalMaterial3Api
@Composable
fun QRCodeReaderScreen(
    username: String,
    navController: NavHostController
) {
    val database = FirebaseDatabase.getInstance("https://chessapp-ea53e-default-rtdb.europe-west1.firebasedatabase.app/")
    val context = LocalContext.current
    val message = database.reference

    AndroidView(
        factory = {
            val codeScannerView = CodeScannerView(context)
            val codeScanner = CodeScanner(context, codeScannerView)
            codeScanner.camera = CodeScanner.CAMERA_BACK
            codeScanner.formats = CodeScanner.ALL_FORMATS
            codeScanner.autoFocusMode = AutoFocusMode.SAFE
            codeScanner.scanMode = ScanMode.SINGLE
            codeScanner.isAutoFocusEnabled = true
            codeScanner.isFlashEnabled = false
            codeScanner.decodeCallback = DecodeCallback {
                val opponent = it.text
                val game = "$opponent,$username"
                message.child("games").child(game).child(username).setValue("entered")
                val intent = Intent(context, GameViewActivity::class.java).apply {  }
                intent.putExtra("multiplayer", true)
                intent.putExtra("opponent", opponent)
                intent.putExtra("match", game)
                message.child("players").child(username).setValue("unavailable")
                context.startActivity(intent)
                codeScanner.releaseResources()
            }
            codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                Toast.makeText(context, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
            codeScanner.startPreview()
            return@AndroidView codeScannerView
                  },

    )
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun QRCodeReaderScreenPreview() {
    QRCodeReaderScreen(
        username = "test",
        rememberNavController()
    )
}

