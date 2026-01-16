package com.dailyquotes.shared

import android.content.Context

actual class UserPreferences(private val context: Context) {
    private val prefs = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    actual fun hasShownSplash(): Boolean {
        return prefs.getBoolean("has_shown_splash", false)
    }

    actual fun setHasShownSplash(shown: Boolean) {
        prefs.edit().putBoolean("has_shown_splash", shown).apply()
    }

    actual fun getNotificationHour(): Int {
        return prefs.getInt("notification_hour", 9)
    }

    actual fun getNotificationMinute(): Int {
        return prefs.getInt("notification_minute", 0)
    }

    actual fun setNotificationTime(hour: Int, minute: Int) {
        prefs.edit().apply {
            putInt("notification_hour", hour)
            putInt("notification_minute", minute)
            apply()
        }
    }
}
