package com.example.simpleqr

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.text.format.DateUtils.formatDateTime
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Calendar

@Composable
fun QrInputForm(
    type: QrType,
    onGenerate: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enter ${type.name.lowercase().replaceFirstChar { it.uppercase() }} Details",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (type) {
                QrType.TEXT -> TextInputForm(onGenerate)
                QrType.URL -> UrlInputForm(onGenerate)
                QrType.SMS -> SmsInputForm(onGenerate)
                QrType.EMAIL -> EmailInputForm(onGenerate)
                QrType.CONTACT -> ContactInputForm(onGenerate)
                QrType.LOCATION -> LocationInputForm(onGenerate)
                QrType.CALENDAR -> CalendarInputForm(onGenerate)
            }
        }
    }
}

@Composable
fun TextInputForm(onGenerate: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter text") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onGenerate(text) },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }
    }
}

@Composable
fun UrlInputForm(onGenerate: (String) -> Unit) {
    var url by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Website URL") },
            placeholder = { Text("example.com") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onGenerate(UrlGenerator(url)) },
            enabled = url.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }
    }
}

@Composable
fun SmsInputForm(onGenerate: (String) -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            placeholder = { Text("+1234567890") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onGenerate(SmsGenerator(phoneNumber, message)) },
            enabled = phoneNumber.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }
    }
}

@Composable
fun EmailInputForm(onGenerate: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            placeholder = { Text("example@email.com") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Message (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onGenerate(EmailGenerator(email, subject, body)) },
            enabled = email.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }
    }
}

@Composable
fun ContactInputForm(onGenerate: (String) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onGenerate(ContactGenerator(phone, firstName, lastName, email)) },
            enabled = firstName.isNotBlank() || phone.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }
    }
}

@Composable
fun LocationInputForm(onGenerate: (String) -> Unit) {
    val context = LocalContext.current
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//permissionLauncher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation(fusedLocationClient, context) { lat, lng ->
                latitude = lat
                longitude = lng
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    Column {
        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitude") },
            placeholder = { Text("40.7128") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitude") },
            placeholder = { Text("-74.0060") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Button(onClick = {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                getLocation(fusedLocationClient,context){ lat,long->
                    latitude= lat
                    longitude= long
                }
            }else{
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
                 },
            modifier= Modifier.fillMaxWidth()
        ) { Text("Get MY Location")}
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val lat = latitude.toDoubleOrNull() ?: 0.0
                val lng = longitude.toDoubleOrNull() ?: 0.0
                onGenerate(GeoGenerator(lat, lng))
            },
            enabled = latitude.isNotBlank() && longitude.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }
    }

}
@SuppressLint("MissingPermission")
fun getLocation(fusedLocationClient: FusedLocationProviderClient,context:Context, onResult: (String, String) -> Unit) {
    try {

        fusedLocationClient.lastLocation.addOnSuccessListener {
                location->
            if(location !=null){
               onResult(location.latitude.toString(),location.longitude.toString())

            }else{
                // If lastLocation is null, request fresh location updates
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    10000 // 10 seconds interval
                ).apply {
                    setMinUpdateDistanceMeters(10f)
                    setMaxUpdateDelayMillis(15000)
                    setMaxUpdates(1) // Stop after getting 1 location
                }.build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult.lastLocation?.let { location ->
                            onResult(location.latitude.toString(), location.longitude.toString())
                            // Stop location updates after getting result
                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    }
                }
                try {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to request location updates", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }catch (e: Exception){
        Toast.makeText(context,"error getching location",Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun CalendarInputForm(onGenerate: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Column {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Event Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { },
                label = { Text("Start Date") },
                placeholder = { Text("Select date") },
                modifier = Modifier.weight(1f),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                       DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                startDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select date")
                    }
                }
            )

            OutlinedTextField(
                value = startTime,
                onValueChange = { },
                label = { Text("Start Time") },
                placeholder = { Text("Select time") },
                modifier = Modifier.weight(1f),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                startTime = String.format("%02d:%02d", hour, minute)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    }) {
                        Icon(Icons.Default.Schedule, contentDescription = "Select time")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = endDate,
                onValueChange = { },
                label = { Text("End Date") },
                placeholder = { Text("Select date") },
                modifier = Modifier.weight(1f),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                endDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select date")
                    }
                }
            )

            OutlinedTextField(
                value = endTime,
                onValueChange = { },
                label = { Text("End Time") },
                placeholder = { Text("Select time") },
                modifier = Modifier.weight(1f),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                endTime = String.format("%02d:%02d", hour, minute)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    }) {
                        Icon(Icons.Default.Schedule, contentDescription = "Select time")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val formattedStart = convertToCalendarFormat(startDate, startTime)
                val formattedEnd = convertToCalendarFormat(endDate, endTime)
                onGenerate(CalenderGenerator(title, formattedStart, formattedEnd, location, description))
            },
            enabled = title.isNotBlank() && startDate.isNotBlank() && startTime.isNotBlank() && endDate.isNotBlank() && endTime.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }
    }
}

fun convertToCalendarFormat(date: String, time: String): String {
    return try {
        val dateFormatted = date.replace("-", "")
        val timeFormatted = time.replace(":", "") + "00"
        "${dateFormatted}T${timeFormatted}"
    } catch (e: Exception) {
        "20250618T140000"
    }
}