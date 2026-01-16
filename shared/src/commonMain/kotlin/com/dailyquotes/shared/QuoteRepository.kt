package com.dailyquotes.shared

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.datetime.*

class QuoteRepository(
    private val client: HttpClient,
    private val quoteCache: QuoteCache
) {
    suspend fun getDailyQuote(): Quote? {
        // Check if we have a cached quote for today
        val cachedQuote = quoteCache.getCachedQuote()
        if (cachedQuote != null) {
            return cachedQuote
        }

        // No cache or cache expired, fetch from API
        return try {
            val quotes: List<Quote> = client.get("https://zenquotes.io/api/today").body()
            val quote = quotes.firstOrNull()

            // Cache the newly fetched quote
            quote?.let { quoteCache.cacheQuote(it) }

            quote
        } catch (e: Exception) {
            null
        }
    }

    fun clearCache() {
        quoteCache.clearCache()
    }
}

fun createHttpClient() = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }
}
