# Unlim Cloud (Android)

Native Android application for **Unlim Cloud** built with Kotlin and Jetpack Compose.

Unlim Cloud offers unlimited cloud storage powered by Telegram ID storage, file explorer, gallery management, automatic update tracking from GitHub, and community donation channels.

## Features

- **Telegram Storage Integration:** Connect via Telegram ID for unlimited cloud file and media storage.
- **Files Explorer:** Browse, filter (Documents, Media, Archives, Other), search, upload, share, and manage cloud files.
- **Media Gallery:** Grid gallery dedicated to photos and video playback/previews.
- **Update Manager:** Live GitHub version check against official repositories (`https://raw.githubusercontent.com/inulute/unlim-cloud/main/package.json`) with direct release downloads.
- **Support & Donations:** Quick access to donate channels (Official Vercel portal, Ko-fi, PayPal, and UPI).
- **Web Portal View:** Integrated browser client for the Unlim Cloud web application.

## Tech Stack

- **Kotlin & Jetpack Compose (Material Design 3)**
- **Coroutines & StateFlow**
- **OkHttp & Kotlinx Serialization** for GitHub update checks
- **Coil** for image loading
