package com.dailyquotes.shared

import platform.UserNotifications.*
import platform.Foundation.*

actual class NotificationManager {
    
    actual fun scheduleDailyReminder(hour: Int, minute: Int) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound
        ) { granted, error ->
            if (granted) {
                val content = UNMutableNotificationContent().apply {
                    setTitle("Daily Inspiration")
                    setBody("Time for your daily quote reflection.")
                    setSound(UNNotificationSound.defaultSound())
                }

                val dateComponents = NSDateComponents().apply {
                    setHour(hour.toLong())
                    setMinute(minute.toLong())
                }

                val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                    dateComponents, 
                    repeats = true
                )

                val request = UNNotificationRequest.requestWithIdentifier(
                    "daily_quote_reminder",
                    content,
                    trigger
                )

                center.addNotificationRequest(request) { error ->
                    // Handle error if needed
                }
            }
        }
    }

    actual fun cancelAllReminders() {
        UNUserNotificationCenter.currentNotificationCenter().removeAllPendingNotificationRequests()
    }
}
