package com.example.simpleqr

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap

fun QrgeneraatorFunction(text: String): Bitmap? {
    return try {

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 400, 400)

        val w = bitMatrix.width
        val h = bitMatrix.height
        val pixels = IntArray(w * h)

        for (y in 0 until h) {
            for (x in 0 until w) {
                pixels[y * w + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        val bitmap = createBitmap(w, h)
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return bitmap
    } catch (e: Exception) {
        null
    }
}
fun UrlGenerator(url: String): String{
    return if (url.startsWith("http://") || url.startsWith("https://")){
        url
    }else{
        "https://$url"
    }
}
fun SmsGenerator(phoneNumber: String, message: String): String{
    val number = phoneNumber.replace(Regex("[^+\\d]"), "")
    return if (message.isNotEmpty()){
        "sms:$number:$message"
    }else{
        "sms:$number"
    }
}
fun EmailGenerator(email: String, subject: String, body: String): String {
    var mailtoString = "mailto:$email"
    val params = mutableListOf<String>()

    if (subject.isNotEmpty()) {
        params.add("subject=${Uri.encode(subject)}")
    }
    if (body.isNotEmpty()) {
        params.add("body=${Uri.encode(body)}")
    }

    if (params.isNotEmpty()) {
        mailtoString += "?" + params.joinToString("&")
    }

    return mailtoString
}

fun ContactGenerator(phone:String, firstName:String="", lastName:String="",email:String=""):String{
    return buildString {
        appendLine("BEGIN:VCARD")
        appendLine("VERSION:3.0")
        appendLine("FN:$firstName $lastName")
        appendLine("N:$lastName;$firstName;;;")
        if (phone.isNotEmpty()) {
        appendLine("TEL:$phone")
    }
        if (email.isNotEmpty()) {
            appendLine("EMAIL:$email")
        }
        appendLine("END:VCARD")
    }
}
fun GeoGenerator(latitude: Double, longitude:Double):String {
    return "geo:$latitude,$longitude"
}
fun CalenderGenerator(title:String, startDate:String, endDate:String, location:String="", description:String=""):String{
    return buildString {
        appendLine("BEGIN:VCALENDAR")
        appendLine("VERSION:2.0")
        appendLine("BEGIN:VEVENT")
        appendLine("SUMMARY:$title")
        appendLine("DTSTART:$startDate")
        appendLine("DTEND:$endDate")
        if (location.isNotEmpty()) {
            appendLine("LOCATION:$location")
        }
        if (description.isNotEmpty()) {
            appendLine("DESCRIPTION:$description")
        }
        appendLine("END:VEVENT")
        appendLine("END:VCALENDAR")

    }
}

