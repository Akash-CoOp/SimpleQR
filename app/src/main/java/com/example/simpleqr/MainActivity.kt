package com.example.simpleqr

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpleqr.ui.theme.SimpleQRTheme
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : ComponentActivity() {

        // Your existing onCreate code
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                SimpleQRTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(   ),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        QrgeneratorScreen()
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            handleScannedContent(result.contents)
        }
    }

    private fun handleScannedContent(content: String) {
        when {
            // Email
            content.startsWith("mailto:") -> {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse(content)
                }
                startActivity(intent)
            }

            // Phone number
            content.startsWith("tel:") -> {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse(content)
                }
                startActivity(intent)
            }

            // Website URL
            content.startsWith("http") -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(content)
                }
                startActivity(intent)
            }

            // SMS
            content.startsWith("sms:") -> {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse(content)
                }
                startActivity(intent)
            }

            // Location
            content.startsWith("geo:") -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(content)
                }
                startActivity(intent)
            }

            // Plain text or unknown - show in a dialog
            else -> {
                showTextDialog(content)
            }
        }
    }

    private fun showTextDialog(content: String) {
        AlertDialog.Builder(this)
            .setTitle("Scanned QR Code")
            .setMessage(content)
            .setPositiveButton("Copy") { _, _ ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("QR Content", content)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }
}
