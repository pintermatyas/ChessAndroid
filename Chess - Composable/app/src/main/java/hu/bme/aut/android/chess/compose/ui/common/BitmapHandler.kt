package hu.bme.aut.android.chess.compose.ui.common

import android.content.ContentValues.TAG
import android.graphics.Color
import android.util.Log
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun getQrCodeBitmap(encodedString: String): Bitmap {
    val size = 768 //pixels
    val bits = QRCodeWriter().encode(encodedString, BarcodeFormat.QR_CODE, size, size)
    return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
        for (x in 0 until size) {
            for (y in 0 until size) {
                it.setPixel(x, y, if (bits[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
    }
}

fun getBoardBitmap(boardString: String, player: Int): Bitmap? {
    val multiplier = 12
    val size = 8*multiplier // 32*8

    Log.d(TAG, "Created bitmap for board $boardString")
    val startTime = System.currentTimeMillis()

    return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
        for (y in 0 until size) {
            for (x in 0 until size) {
                var realX = x/multiplier
                var realY = y/multiplier
                val char = boardString[realX+8*realY]

                var boardXPos: Int = x
                var boardYPos: Int = size-y-1

                if(player == 1){
                    boardXPos = size-x-1
                    boardYPos = y
                    realX = size - x/multiplier - 1
                    realY = size - y/multiplier - 1
                }

                if(x%multiplier < 2 || y%multiplier < 2 || size-x < 2 || size-y < 2){
                    it.setPixel(boardXPos, boardYPos, Color.BLACK)
                    continue
                }
                if(char=='0'){
                    it.setPixel(boardXPos, boardYPos, if ((realX+realY) % 2 == 0) Color.rgb(191, 147, 52) else Color.rgb(245, 218, 159))
                } else if(char.isUpperCase()){
                    it.setPixel(boardXPos, boardYPos, Color.BLACK)
                }else if(char.isLowerCase()){
                    it.setPixel(boardXPos, boardYPos, Color.WHITE)
                }
            }
        }
        val endTime = System.currentTimeMillis()

        // 28ms with multiplier=32
        // 17ms with multiplier=24
        // 8ms with multiplier=16
        // 4ms with multiplier=12
        Log.d(TAG, "Created in: ${endTime - startTime} ms")
    }
}