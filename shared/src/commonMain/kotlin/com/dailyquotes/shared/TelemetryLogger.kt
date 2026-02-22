package com.dailyquotes.shared

expect object TelemetryLogger {
    fun logNonFatal(t: Throwable)
    fun setUserId(id: String?)
}
