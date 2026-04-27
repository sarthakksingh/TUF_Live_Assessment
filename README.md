# Uchat

Uchat is a Jetpack Compose Android app that recreates a WhatsApp-style experience with chats, status updates, and call history. The app uses a shared `ChatViewModel` and a local JSON-backed repository to keep conversations and contacts available between launches.

## Features

- Chat list with search
- Create new contacts from inside the app
- Open individual conversations
- Send text messages
- Attach images or files from local storage
- Simulated message delivery and auto-replies
- Archive chats
- Status screen with story-style updates
- Calls screen with recent voice and video call history
- Dark-themed Material 3 UI

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Lifecycle ViewModel
- Hilt
- KSP

## Project Structure

- `app/src/main/java/com/sarthak/tufassessment/MainActivity.kt` - app entry point
- `app/src/main/java/com/sarthak/tufassessment/ui/navigation/ChatApp.kt` - navigation and app scaffold
- `app/src/main/java/com/sarthak/tufassessment/viewmodel/ChatViewModel.kt` - shared UI state and message logic
- `app/src/main/java/com/sarthak/tufassessment/data/repo/ChatRepository.kt` - local JSON persistence
- `app/src/main/java/com/sarthak/tufassessment/ui/screens/` - chats, status, calls, and conversation screens

## Requirements

- Android Studio Hedgehog or newer
- JDK 11
- Android SDK with compile/target SDK 36
- Android device or emulator running API 26 or later

## Getting Started

1. Clone or open the project in Android Studio.
2. Let Gradle sync finish.
3. Run the app on a device or emulator.

### From the command line

```bash
./gradlew assembleDebug
```

On Windows:

```powershell
gradlew.bat assembleDebug
```

## Data Storage

The app stores its local state in the app's internal storage as `chat_store.json`. This file is created automatically the first time the app runs and is used to preserve contacts, conversations, and archived chats.

## Notes

- The app is designed as a local demo and does not connect to a backend service.
- Message delivery and replies are simulated for a smoother prototype experience.
