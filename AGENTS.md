# AGENTS.md

## Scope
- Make product and code changes for the Android app only unless the user explicitly asks otherwise.
- Prefer Android-specific paths such as `composeApp/src/androidMain/` and `shared/src/androidMain/` for Android-only behavior.
- Do not modify `iosApp/` or iOS-specific source sets unless the user explicitly requests iOS work.
- If Android behavior is implemented in shared/common code, keep the change minimal and verify it is required for the Android app.

## Commands
- Install debug build on a connected Android device: `./gradlew :composeApp:installDebug`
- Build debug APK: `./gradlew :composeApp:assembleDebug`
- Build release AAB: `fastlane android build_release`
