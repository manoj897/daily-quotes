package com.dailyquotes.shared

actual object TelemetryLogger {
    actual fun logNonFatal(t: Throwable) { /* no-op for iOS currently */ }
    actual fun setUserId(id: String?) { /* no-op */ }
}
