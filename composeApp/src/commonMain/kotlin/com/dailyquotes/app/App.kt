package com.dailyquotes.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.dailyquotes.app.screens.OnboardingScreen
import com.dailyquotes.app.screens.QuoteScreen
import com.dailyquotes.shared.UserPreferences
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun App() {
    val userPreferences: UserPreferences = getKoin().get()

    // Determine initial screen based on whether onboarding has been shown
    val initialScreen = remember {
        if (userPreferences.hasShownSplash()) {
            QuoteScreen()
        } else {
            OnboardingScreen()
        }
    }

    // Note: Notification scheduling is handled by:
    // - OnboardingScreenModel (first time setup)
    // - SettingsScreenModel (when user changes time)
    // - ReminderReceiver (auto-reschedules after firing)
    // - BootReceiver (reschedules after device reboot)
    // No need to schedule here as it would interfere with the existing schedule

    DailyQuotesTheme {
        Navigator(initialScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}
