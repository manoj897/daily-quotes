package com.dailyquotes.shared

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val q: String, // quote
    val a: String, // author
    val h: String? = null // html (optional)
)

data class Reflection(
    val id: Long,
    val quoteContent: String,
    val author: String,
    val note: String,
    val tags: List<String>,
    val createdAt: Long
)
