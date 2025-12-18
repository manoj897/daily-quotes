package com.dailyquotes.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.dailyquotes.app.screens.QuoteScreen
import com.dailyquotes.shared.NotificationManager
import org.koin.compose.KoinContext
import org.koin.compose.getKoin

@Composable
fun App() {
    KoinContext {
        val notificationManager: NotificationManager = getKoin().get()
        
        LaunchedEffect(Unit) {
            notificationManager.scheduleDailyReminder(9, 0) // Default to 9:00 AM
        }

        DailyQuotesTheme {
            Navigator(QuoteScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}
