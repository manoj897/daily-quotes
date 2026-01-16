package com.dailyquotes.shared

expect class QuoteCache {
    fun getCachedQuote(): Quote?
    fun cacheQuote(quote: Quote)
    fun clearCache()
}
