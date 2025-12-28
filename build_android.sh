#!/bin/bash

# Exit on error
set -e

echo "🚀 Starting Android Build Process..."

echo "🧹 Cleaning project..."
./gradlew clean

echo "📦 Compiling and assembling Debug APK..."
./gradlew :composeApp:assembleDebug

echo "📲 Installing APK on device/emulator..."
./gradlew :composeApp:installDebug

echo "✅ Android build and installation complete!"
