package com.dailyquotes.shared

import platform.Foundation.NSUserDefaults
import kotlinx.datetime.*

actual class QuoteCache {

    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun getCachedQuote(): Quote? {
        val cachedDate = userDefaults.stringForKey("cached_date") ?: return null
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

        // Check if the cached quote is from today
        if (cachedDate != today) {
            return null // Cache expired
        }

        // Retrieve cached quote
        val quoteText = userDefaults.stringForKey("quote_text")
        val author = userDefaults.stringForKey("quote_author")

        return if (quoteText != null && author != null) {
            Quote(q = quoteText, a = author)
        } else {
            null
        }
    }

    actual fun cacheQuote(quote: Quote) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

        userDefaults.setObject(today, forKey = "cached_date")
        userDefaults.setObject(quote.q, forKey = "quote_text")
        userDefaults.setObject(quote.a, forKey = "quote_author")
    }

    actual fun clearCache() {
        userDefaults.removeObjectForKey("cached_date")
        userDefaults.removeObjectForKey("quote_text")
        userDefaults.removeObjectForKey("quote_author")
    }
}
