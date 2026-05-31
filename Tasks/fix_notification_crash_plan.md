# Implementation Plan - Fix Notification Crash

The app crashes when clicking a notification because `initKoin` is called in `MainActivity.onCreate`. If the activity is recreated or launched while the application process is already running (e.g., after the activity was destroyed), `initKoin` attempts to start Koin again, throwing a `KoinAppAlreadyStartedException`.

The standard fix is to move Koin initialization to a custom `Application` class, which is only created once per process lifetime.

## Proposed Changes

### [Component] Android Entry Point

#### [NEW] [DailyQuotesApp.kt](file:///Users/manoj.krishnan/StudioProjects/Personal/daily-quotes/composeApp/src/androidMain/kotlin/com/dailyquotes/app/DailyQuotesApp.kt)
Create a new `Application` class to handle global initialization.
- Initialize Koin here using `initKoin`.
- Use `androidContext(this)` to provide the application context.

#### [MODIFY] [MainActivity.kt](file:///Users/manoj.krishnan/StudioProjects/Personal/daily-quotes/composeApp/src/androidMain/kotlin/com/dailyquotes/app/MainActivity.kt)
- Remove the `initKoin` block from `onCreate`.

#### [MODIFY] [AndroidManifest.xml](file:///Users/manoj.krishnan/StudioProjects/Personal/daily-quotes/composeApp/src/androidMain/AndroidManifest.xml)
- Set `android:name=".DailyQuotesApp"` in the `<application>` tag.

### [Component] Shared Logic (Android)

#### [MODIFY] [ReminderReceiver.kt](file:///Users/manoj.krishnan/StudioProjects/Personal/daily-quotes/shared/src/androidMain/kotlin/com/dailyquotes/shared/ReminderReceiver.kt)
- Ensure the `PendingIntent` for the notification is robust. Adding `Intent.FLAG_ACTIVITY_NEW_TASK` explicitly if needed, though `getLaunchIntentForPackage` usually includes it.

## Verification Plan

### Automated Tests
- N/A (Unit tests won't easily catch this lifecycle-related session crash without significant mocking of the Android framework).

### Manual Verification
1.  **Launch the app**: Ensure it starts normally.
2.  **Simulate Notification**: Trigger a notification (can be done by setting the reminder time to 1 minute from now).
3.  **App in Background**: Put the app in the background, then click the notification. It should open without crashing.
4.  **App Process Alive, Activity Dead**: Open the app, press back to exit the activity (process remains alive usually), then click the notification. This is the scenario that likely caused the crash.
5.  **Orientation Change**: Change orientation; ensure no crash occurs (this also calls `onCreate` again).
6.  **Multiple Notification Clicks**: Trigger multiple notifications and click them sequentially.
