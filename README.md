# Simple QR Code Generator

## Technologies Used

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern Android UI toolkit
- **ZXing Library** - QR code generation and scanning
- **Android CameraX** - Camera integration for scanning
- **Google Play Services Location** - GPS location fetching
- **Coroutines** - Asynchronous operations
- **Android FileProvider** - Secure file sharing
- **MediaStore API** - Gallery integration for saving QR codes

## Features

- Generate QR codes for 7 different content types (Text, URL, SMS, Email, Contact, Location, Calendar)
- Real-time QR code scanning with camera
- Date and time pickers for calendar events
- Location services integration with GPS
- Save QR codes to device gallery
- Share QR codes with other apps
- Offline functionality - no internet required

## Architecture

- **MVVM pattern** with Compose state management
- **Single Activity** architecture
- **Modular code structure** with separate generator functions
- **Permission handling** for camera and location access
- **Error handling** with user-friendly feedback
