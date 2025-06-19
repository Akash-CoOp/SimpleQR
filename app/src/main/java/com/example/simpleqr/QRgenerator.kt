package com.example.simpleqr

import android.R.attr.bitmap
import android.R.attr.type
import android.app.Activity
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.asImageBitmap  // ✅ Correct import

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Language

import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.launch

//@Composable
//fun QrgeneratormScreen(){
//    var inputText by remember{  mutableStateOf("") }
//    var qrBitmap by remember{ mutableStateOf<Bitmap?>(null)}
//
//    Column(modifier= Modifier.fillMaxSize().padding(16.dp),horizontalAlignment = Alignment.CenterHorizontally){
//        OutlinedTextField(value = inputText,
//            onValueChange = {inputText= it},
//            label = {Text("Enter text to generate QR")},
//            modifier = Modifier.fillMaxWidth()
//
//
//
//        )
//        Spacer(modifier= Modifier.height(16.dp))
//        Button(onClick = {
//            qrBitmap = QrgeneraatorFunction(inputText)
//        },
//          enabled = inputText.isNotBlank()
//        ) {Text("Generate QR Code") }
//
//        Spacer(modifier = Modifier.height(16.dp))
//        qrBitmap?.let{
//            Image(
//                bitmap = it.asImageBitmap(),
//                contentDescription = null,
//                modifier = Modifier.size(300.dp)
//
//            )
//
//        }
//
//    }
//
//}

@Composable
fun QrgeneratorScreen(){
    var selectedType by remember { mutableStateOf<QrType?>(null)}
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    BackHandler(
        enabled = selectedType != null || qrBitmap != null
    ) {
        when {
            // If QR code is displayed, go back to form
            qrBitmap != null -> {
                qrBitmap = null
            }
            // If form is displayed, go back to main screen
            selectedType != null -> {
                selectedType = null
                qrBitmap = null
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center){
        Text("QR code for anything", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom= 24.dp)
        )

        Button(
            onClick = {
                val integrator = IntentIntegrator(context as Activity)
                integrator.setPrompt("Scan a QR Code")
                integrator.initiateScan()
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Scan QR Code")
        }

        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier= Modifier.padding(bottom= 24.dp)) {
            item {
                QrTypeButton(
                    title = "Text",
                    icon = Icons.Default.TextFields,
                    onClick = { selectedType = QrType.TEXT }
                )
            }
            item {
                QrTypeButton(
                    title = "Website",
                    icon = Icons.Default.Language,
                    onClick = { selectedType = QrType.URL }
                )
            }
            item {
                QrTypeButton(
                    title = "SMS",
                    icon = Icons.Default.Message,
                    onClick = { selectedType = QrType.SMS }
                )
            }
            item {
                QrTypeButton(
                    title = "Email",
                    icon = Icons.Default.Email,
                    onClick = { selectedType = QrType.EMAIL }
                )
            }
            item {
                QrTypeButton(
                    title = "Contact",
                    icon = Icons.Default.Person,
                    onClick = { selectedType = QrType.CONTACT }
                )
            }
            item {
                QrTypeButton(
                    title = "Location",
                    icon = Icons.Default.LocationOn,
                    onClick = { selectedType = QrType.LOCATION }
                )
            }
            item(span = { GridItemSpan(2) }) {
                QrTypeButton(
                    title = "Calendar Event",
                    icon = Icons.Default.Event,
                    onClick = { selectedType = QrType.CALENDAR },
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }

    }
    selectedType?.let {
        QrInputForm(
            type = it,
            onGenerate = { content ->
                qrBitmap = QrgeneraatorFunction(content)
            },
            onBack = { selectedType = null
            qrBitmap= null}
        )
    }
    qrBitmap?.let{bitmap->

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { qrBitmap = null }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to form"
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card( modifier = Modifier.wrapContentSize().padding(horizontal = 16.dp),elevation= CardDefaults.cardElevation(defaultElevation = 8.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
                ,verticalArrangement = Arrangement.Center
            ){

                Image(
                    bitmap= bitmap.asImageBitmap(),
                    contentDescription = "generated qr code",

                    modifier= Modifier.size(250.dp)

                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = { qrBitmap = null }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to form"
                        )
                    }
                    Button(
                        onClick = {
                            scope.launch {

                                saveBitmapToGallery(context, bitmap)

                            }
                        },

                        modifier = Modifier.weight(1f)
                    ) {

                        Text("Save")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            scope.launch {

                                shareBitmap(context, bitmap)

                            }
                        },

                        modifier = Modifier.weight(1f)
                    ) {


                        Text( "Share")
                    }

                }
            }

        }
    }

}
@Composable
fun QrTypeButton(title: String,icon:ImageVector,onClick:()-> Unit,
                 modifier:Modifier= Modifier){
    Card(modifier= Modifier.height(100.dp)
        .clickable{ onClick()}) {  Column(modifier=Modifier.fillMaxSize().padding(16.dp),horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

}






