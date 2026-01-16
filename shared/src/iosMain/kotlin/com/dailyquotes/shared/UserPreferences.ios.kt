package com.dailyquotes.shared

import platform.Foundation.NSUserDefaults

actual class UserPreferences {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun hasShownSplash(): Boolean {
        return userDefaults.boolForKey("has_shown_splash")
    }

    actual fun setHasShownSplash(shown: Boolean) {
        userDefaults.setBool(shown, forKey = "has_shown_splash")
    }

    actual fun getNotificationHour(): Int {
        val hour = userDefaults.integerForKey("notification_hour").toInt()
        // Return default of 9 if not set (NSUserDefaults returns 0 for missing keys)
        return if (hour == 0 && !userDefaults.boolForKey("has_shown_splash")) 9 else hour
    }

    actual fun getNotificationMinute(): Int {
        return userDefaults.integerForKey("notification_minute").toInt()
    }

    actual fun setNotificationTime(hour: Int, minute: Int) {
        userDefaults.setInteger(hour.toLong(), forKey = "notification_hour")
        userDefaults.setInteger(minute.toLong(), forKey = "notification_minute")
    }
}
