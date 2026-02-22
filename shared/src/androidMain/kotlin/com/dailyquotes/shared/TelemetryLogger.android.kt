package com.dailyquotes.shared

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual object TelemetryLogger {
    private val crashlytics: FirebaseCrashlytics
        get() = FirebaseCrashlytics.getInstance()

    actual fun logNonFatal(t: Throwable) {
        crashlytics.recordException(t)
    }

    actual fun setUserId(id: String?) {
        crashlytics.setUserId(id ?: "")
    }
}
