package com.dailyquotes.app

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}

fun initKoinIos() = initKoin {
    // Platform-specific setup if needed
}
