package com.dailyquotes.shared

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class ShareManager {
    actual fun shareText(text: String) {
        val window = UIApplication.sharedApplication.keyWindow
        val rootViewController = window?.rootViewController
        
        val activityViewController = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )
        
        rootViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}
