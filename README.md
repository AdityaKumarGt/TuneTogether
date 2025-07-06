# 🎵 Tune Together

A real-time collaborative music playback Android app built with Kotlin and Jetpack Compose. Users can create or join rooms, upload songs, invite members, and control synchronized playback across all connected users — just like a virtual party room!

---

## 🚀 Features

- 🔊 **Real-Time Synchronized Music Playback** using ExoPlayer and WebSocket
- 📁 **Upload Audio Files to Cloudinary** during room creation
- 👥 **Invite Users to Join Rooms** for shared playback control
- 🔁 **Playback Control Sync**: Play, pause, seek, or change song — it reflects for everyone instantly
- 🔐 **Google Sign-In** and **Firebase Authentication**
- 📲 **Push Notifications** using Firebase Cloud Messaging (FCM)
- 🌐 **WebSocket Communication** for room and playback event handling
- 🧠 **Clean Architecture** with modular layers and dependency injection (Hilt)

---

## 🛠️ Tech Stack

### 📱 Android
- **Kotlin**, **Jetpack Compose**, **XML**
- **ExoPlayer** – for audio streaming
- **Hilt** – for Dependency Injection
- **OkHttp WebSocket Client** – for real-time communication
- **Coroutines** & **Flows** – for async and state handling
- **SharedPreferences** –  for local lightweight data persistence (e.g., auth/session/token)
- **Firebase** – Authentication, FCM

### ☁️ Backend
- **Node.js**, **Express**
- **Cloudinary** – for storing uploaded audio files
- **MongoDB** – for room and user data
- **WebSocket** – for real-time messaging and sync
