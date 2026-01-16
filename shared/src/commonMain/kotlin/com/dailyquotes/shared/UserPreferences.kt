package com.dailyquotes.shared

expect class UserPreferences {
    fun hasShownSplash(): Boolean
    fun setHasShownSplash(shown: Boolean)
    fun getNotificationHour(): Int
    fun getNotificationMinute(): Int
    fun setNotificationTime(hour: Int, minute: Int)
}
