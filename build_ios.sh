#!/bin/bash

# Exit on error
set -e

echo "🚀 Starting iOS Build Process..."

echo "🔗 Building Shared Framework..."
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

echo "🏗️ Building iOS Application..."
xcodebuild -project iosApp/iosApp.xcodeproj \
           -scheme iosApp \
           -configuration Debug \
           -sdk iphonesimulator \
           -destination 'platform=iOS Simulator,name=iPhone 16' \
           build \
           -derivedDataPath build

echo "🔍 Finding booted simulator..."
SIMULATOR_ID=$(xcrun simctl list devices | grep Booted | head -1 | sed -E 's/.*\(([0-9A-F-]+)\).*/\1/')

if [ -z "$SIMULATOR_ID" ]; then
    echo "❌ Error: No booted simulator found. Please start a simulator first."
    exit 1
fi

echo "📍 Simulator ID: $SIMULATOR_ID"

echo "📲 Installing app on simulator..."
# Note: The path depends on -derivedDataPath used in xcodebuild
APP_PATH="build/Build/Products/Debug-iphonesimulator/iosApp.app"

if [ ! -d "$APP_PATH" ]; then
    echo "❌ Error: App bundle not found at $APP_PATH"
    exit 1
fi

xcrun simctl install "$SIMULATOR_ID" "$APP_PATH"

echo "🚀 Launching app..."
xcrun simctl launch "$SIMULATOR_ID" com.dailyquotes.app

echo "✅ iOS build and installation complete!"
