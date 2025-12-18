package com.dailyquotes.shared

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class QuoteRepository(private val client: HttpClient) {
    suspend fun getDailyQuote(): Quote? {
        return try {
            val quotes: List<Quote> = client.get("https://zenquotes.io/api/today").body()
            quotes.firstOrNull()
        } catch (e: Exception) {
            null
        }
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
