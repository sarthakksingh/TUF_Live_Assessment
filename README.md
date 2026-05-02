# Uchat

Uchat is a Jetpack Compose Android app that recreates a WhatsApp-style experience with chats, status updates, and call history. The app uses a shared ChatViewModel and a local JSON-backed repository to persist conversations and contacts across app launches.



🔗 References
Codex Chat (Full Conversation Dump)
https://docs.google.com/document/d/1a9W5nKV8GyS5cUsRhPS0BJSSn7yyuXmfPaT0QCQb9bQ/edit?usp=sharing
https://www.notion.so/Chat-History-34f82fe8ba418058aae0e2da8d8be7ea?source=copy_link

(I have created and pasted the logs for the whole chat in a doc and a notion file because codex does not yet support chat share feature in desktop version it howeer has a deeplink sharing feature below is the deeplink i have not deleted the chat if needed i would provide the screenshots )

Codex Deeplink

codex://threads/019dcec8-cd30-7d82-bc42-ef71fdfa9b57


Claude Chat
https://claude.ai/share/271d0e63-1265-4f3b-94ff-4e5a3651da02





📱 Screenshots
💬 Chats Experience
<p align="center"> <img src="https://github.com/user-attachments/assets/9d4cf926-7173-4878-a400-d07c71953311" width="30%" /> <img src="https://github.com/user-attachments/assets/b1de646d-dc0a-4520-8b79-e82f98e9e3a7" width="30%" /> <img src="https://github.com/user-attachments/assets/820a94a0-9de8-46cd-bfd9-598262acb59e" width="30%" /> </p>
🟢 Status Screen
<p align="center"> <img src="https://github.com/user-attachments/assets/67a155d2-b756-46b4-bbe9-7ecb0d26ef29" width="30%" /> </p>
📞 Call History
<p align="center"> <img src="https://github.com/user-attachments/assets/0b53846c-2b5c-46eb-a4e0-e259cb84a6b5" width="30%" /> </p>
💬 Ongoing Chat UI
<p align="center"> <img src="https://github.com/user-attachments/assets/d0908b27-59f1-4a39-b4f6-dc6a83abb48c" width="30%" /> <img src="https://github.com/user-attachments/assets/c98baaf2-7f52-4f25-89d1-7d70bba68251" width="30%" /> <img src="https://github.com/user-attachments/assets/7b96ba7d-00a5-4eca-84ad-9fb6356f9076" width="30%" /> </p>






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
