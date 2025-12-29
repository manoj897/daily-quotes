# Daily Quotes Inspiration

A premium, minimalist cross-platform mobile app that delivers one motivational quote daily with a powerful reflection feature for turning inspiration into action.

## Project Overview

Daily Quotes Inspiration is more than just a quote app—it's a private companion for mindset integration and habit formation. The core USP is the **Reflection** feature, which transforms passive quote reading into active personal application through notes and smart tagging.

Users can:
- Receive one high-quality motivational quote daily
- Add personal reflections with context (incidents, intentions, reminders)
- Tag quotes for easy retrieval (Work, Family, Meetings, Workouts, etc.)
- Build a searchable journal of applied wisdom
- Share quotes via native platform sharing

## Tech Stack

### Core Technologies
- **Kotlin Multiplatform (KMP)**: Shared business logic across platforms
- **Compose Multiplatform**: Single UI codebase for Android and iOS
- **Target Platforms**: Android (API 24+), iOS (14.0+)

### Key Libraries
- **Voyager**: Type-safe navigation for Compose Multiplatform
- **Koin**: Dependency injection
- **SQLDelight**: Type-safe local database for reflections and tags
- **Ktor Client**: HTTP client for fetching daily quotes
- **kotlinx.serialization**: JSON serialization
- **kotlinx.datetime**: Cross-platform date/time handling

## Architecture

### Design Patterns
- **Clean Architecture**: Separation of concerns with data, domain, and presentation layers
- **Repository Pattern**: Data access abstraction
- **MVVM with ScreenModels**: Voyager's ScreenModel for state management
- **Expect/Actual Pattern**: Platform-specific implementations (sharing, notifications, database drivers)

### Module Structure
```
daily-quotes/
├── composeApp/          # Compose Multiplatform UI layer
│   ├── commonMain/      # Shared UI code (screens, theme, navigation)
│   ├── androidMain/     # Android-specific UI entry point
│   └── iosMain/         # iOS-specific UI entry point
├── shared/              # Shared business logic
│   ├── commonMain/      # Core logic (repositories, models, database schema)
│   ├── androidMain/     # Android platform implementations
│   └── iosMain/         # iOS platform implementations
└── iosApp/              # iOS app wrapper (Swift/SwiftUI)
```

## Key Features

### 1. Quote of the Day
- Fetches fresh motivational quotes from ZenQuotes API
- Premium monochrome dark-mode UI inspired by CRED
- Elegant typography with smooth animations

### 2. Reflection Editor
- **Personal Notes**: Full-screen editor for capturing how quotes apply to real life
- **Smart Tagging System**: 
  - Hybrid chip-based interface
  - Auto-suggests tags from user history
  - Pre-seeded with common tags (Work, Family, Meetings, Workouts, etc.)
  - Support for custom tags
- **Auto-save**: Reflections persist locally via SQLDelight

### 3. Reflections Journal
- Chronological list of all past reflections
- Tag-based filtering for quick retrieval
- Search quotes by context (e.g., all "Workout" quotes for gym motivation)
- Displays quote preview, personal notes, and associated tags

### 4. Social Sharing
- Native platform share sheet integration
- Share quotes as formatted text
- Platform-specific implementation (Android Intent, iOS UIActivityViewController)

### 5. Daily Reminders
- Local notifications scheduled at 9:00 AM (customizable)
- **Android**: AlarmManager + BroadcastReceiver
- **iOS**: UNUserNotificationCenter
- No server dependency—fully local

## Build Instructions

See [build_instructions.md](./build_instructions.md) for detailed build and deployment steps.

### Quick Start
```bash
# Android
./gradlew :composeApp:assembleDebug

# iOS (requires macOS + Xcode)
cd iosApp
xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -configuration Debug
```

## Current State

### ✅ Implemented Features
- [x] Daily quote fetching via Ktor
- [x] Premium monochrome theme with dark mode
- [x] Reflection editor with notes and smart tagging
- [x] SQLDelight database for local storage
- [x] Reflections journal with chronological view
- [x] Tag-based filtering
- [x] Native social sharing (Android + iOS)
- [x] Daily local notifications (Android + iOS)
- [x] Voyager navigation with smooth transitions
- [x] Koin dependency injection setup
- [x] Platform-native auto-backups (Google Drive/iCloud)

### 🚧 Known Issues
- None currently tracked

### 📋 Future Enhancements (Out of Scope for V1.1)
- Cloud sync across devices
- AI-powered tag suggestions
- Quote analytics and trends
- Export reflections to PDF/JSON
- Custom notification time picker in settings
- Image-based quote sharing (generate styled images)

## Development Notes

### Database Schema
The app uses SQLDelight with a relational schema:
- `Reflection`: Stores quote content, author, user notes, and timestamp
- `Tag`: Unique tag names
- `ReflectionTag`: Many-to-many relationship between reflections and tags

### Quote API
Currently using ZenQuotes API (`https://zenquotes.io/api/today`). The API returns:
```json
[{
  "q": "Quote text",
  "a": "Author name",
  "h": "HTML version (optional)"
}]
```

### Privacy & Data Storage
All user data (reflections, tags) is stored **locally only** on the device. Platform-native auto-backup is enabled for data recovery (Google Drive for Android, iCloud for iOS).

---

**Last Updated**: December 2025  
**Version**: 1.1  
**Maintainer**: Manoj Krishnan
