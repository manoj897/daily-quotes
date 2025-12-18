package com.dailyquotes.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.dailyquotes.app.screens.QuoteScreen

@Composable
fun App() {
    DailyQuotesTheme {
        Navigator(QuoteScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
