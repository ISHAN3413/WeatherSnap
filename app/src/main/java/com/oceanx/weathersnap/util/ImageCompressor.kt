package com.oceanx.weathersnap.util


import android.content.Context
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

data class CompressResult(
    val compressedPath: String,
    val originalKb: Long,
    val compressedKb: Long
)

object ImageCompressor {
    fun compress(context: Context, sourcePath: String): CompressResult {
        val originalFile = File(sourcePath)
        val originalKb = originalFile.length() / 1024

        val bitmap = BitmapFactory.decodeFile(sourcePath)
        val outFile = File(context.filesDir, "compressed_${System.currentTimeMillis()}.jpg")

        FileOutputStream(outFile).use { out ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, out)
        }
        bitmap.recycle()

        return CompressResult(
            compressedPath = outFile.absolutePath,
            originalKb = originalKb,
            compressedKb = outFile.length() / 1024
        )
    }
}