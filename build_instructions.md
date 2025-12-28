# Daily Quotes: Build and Installation Guide

This guide provides the necessary Gradle and command-line tasks to compile and install the application on Android and iOS devices/simulators.

## Prerequisites

- **Java Development Kit (JDK)**: Ensure JDK 17+ is installed and `JAVA_HOME` is set.
- **Android SDK**: Required for Android builds.
- **Xcode**: Required for iOS builds (macOS only).
- **CocoaPods**: Ensure `pod` is installed if the project uses it (standard KMP often requires it).

---

## Android Build & Installation

### 1. Clean the Project
Remove any cached build artifacts to ensure a fresh start.
```bash
./gradlew clean
```

### 2. Compile and Assemble
Generate the debug APK.
```bash
./gradlew :composeApp:assembleDebug
```
*Note: The generated APK will be located at `composeApp/build/outputs/apk/debug/composeApp-debug.apk`.*

### 3. Install on Device/Simulator
Install the compiled APK on a connected Android device or running emulator.
```bash
./gradlew :composeApp:installDebug
```

---

## iOS Build & Installation

### 1. Build the Shared Framework
Compile the Kotlin Multiplatform shared code into an Apple Framework for the simulator (ARM64).
```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```
*Note: For physical devices, use `linkDebugFrameworkIosArm64`.*

### 2. Build the iOS Application
Use `xcodebuild` to compile the Swift wrapper and bundle it with the framework.
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
           -scheme iosApp \
           -configuration Debug \
           -sdk iphonesimulator \
           -destination 'platform=iOS Simulator,name=iPhone 16' \
           build
```
*Note: Replace `'name=iPhone 16'` with your preferred simulator name found via `xcrun simctl list devices`.*

### 3. Install and Run on Simulator
First, find the ID of your booted simulator:
```bash
xcrun simctl list devices | grep Booted
```

Then, install the `.app` bundle produced by the previous step:
```bash
# Replace [SIMULATOR_ID] with the actual ID and verify the path to .app
xcrun simctl install [SIMULATOR_ID] build/Build/Products/Debug-iphonesimulator/iosApp.app
```

Launch the app:
```bash
xcrun simctl launch [SIMULATOR_ID] com.dailyquotes.app
```

---

## Useful Shortcuts

| Action | Command |
| :--- | :--- |
| **Clean & Build Android** | `./gradlew clean :composeApp:assembleDebug` |
| **Check Targets** | `./gradlew tasks` |
| **Run Android Directly** | `./gradlew :composeApp:installDebug` |
