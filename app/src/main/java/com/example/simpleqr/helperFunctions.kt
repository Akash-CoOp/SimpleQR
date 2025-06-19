package com.example.simpleqr
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast

import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.core.net.toUri

suspend fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    filename: String = "QR_Code_${System.currentTimeMillis()}"
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 and above - use MediaStore
                saveToMediaStore(context, bitmap, filename)
            } else {
                // Android 9 and below - use external storage
                saveToExternalStorage(context, bitmap, filename)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to save QR code: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }
}

private suspend fun saveToMediaStore(
    context: Context,
    bitmap: Bitmap,
    filename: String
): Boolean {
    val contentResolver: ContentResolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QR Codes")
    }

    val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    return uri?.let { imageUri ->
        contentResolver.openOutputStream(imageUri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "QR code saved to gallery!", Toast.LENGTH_SHORT).show()
            }
            true
        } ?: false
    } ?: false
}

private suspend fun saveToExternalStorage(
    context: Context,
    bitmap: Bitmap,
    filename: String
): Boolean {
    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val qrDir = File(picturesDir, "QR Codes")

    if (!qrDir.exists()) {
        qrDir.mkdirs()
    }

    val file = File(qrDir, "$filename.png")

    return FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)


        val mediaScanIntent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = Uri.fromFile(file)
        context.sendBroadcast(mediaScanIntent)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "QR code saved to gallery!", Toast.LENGTH_SHORT).show()
        }
        true
    }
}

suspend fun shareBitmap(
    context: Context,
    bitmap: Bitmap,
    shareText: String = "Check out this QR code!"
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            // Save bitmap to cache directory
            val cachePath = File(context.externalCacheDir, "images")
            cachePath.mkdirs()

            val file = File(cachePath, "qr_code_${System.currentTimeMillis()}.png")
            val fileOutputStream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            // Get URI using FileProvider
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            // Create share intent
            withContext(Dispatchers.Main) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooserIntent = Intent.createChooser(shareIntent, "Share QR Code")
                context.startActivity(chooserIntent)
            }

            true
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to share QR code: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }
}


