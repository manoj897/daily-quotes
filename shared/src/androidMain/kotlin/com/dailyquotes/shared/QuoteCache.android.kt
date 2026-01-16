package com.dailyquotes.shared

import android.content.Context
import kotlinx.datetime.*

actual class QuoteCache(private val context: Context) {

    private val prefs = context.getSharedPreferences("quote_cache", Context.MODE_PRIVATE)

    actual fun getCachedQuote(): Quote? {
        val cachedDate = prefs.getString("cached_date", null) ?: return null
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

        // Check if the cached quote is from today
        if (cachedDate != today) {
            return null // Cache expired
        }

        // Retrieve cached quote
        val quoteText = prefs.getString("quote_text", null)
        val author = prefs.getString("quote_author", null)

        return if (quoteText != null && author != null) {
            Quote(q = quoteText, a = author)
        } else {
            null
        }
    }

    actual fun cacheQuote(quote: Quote) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

        prefs.edit().apply {
            putString("cached_date", today)
            putString("quote_text", quote.q)
            putString("quote_author", quote.a)
            apply()
        }
    }

    actual fun clearCache() {
        prefs.edit().clear().apply()
    }
}
