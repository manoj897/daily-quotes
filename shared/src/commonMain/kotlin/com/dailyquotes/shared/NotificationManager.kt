package com.dailyquotes.shared

expect class NotificationManager {
    fun scheduleDailyReminder(hour: Int, minute: Int)
    fun cancelAllReminders()
}
