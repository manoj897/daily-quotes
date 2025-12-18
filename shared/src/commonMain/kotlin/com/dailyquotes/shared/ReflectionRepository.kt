package com.dailyquotes.shared

import com.dailyquotes.db.DailyQuotesDb
import kotlinx.datetime.Clock

class ReflectionRepository(db: DailyQuotesDb) {
    private val queries = db.DailyQuotesDbQueries

    fun saveReflection(quote: Quote, note: String, tags: List<String>) {
        queries.transaction {
            queries.insertReflection(
                quoteId = quote.a + quote.q.hashCode(), 
                quoteContent = quote.q,
                author = quote.a,
                note = note,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
            val reflectionId = queries.lastInsertedId().executeAsOne()
            
            tags.forEach { tagName ->
                queries.insertTag(tagName)
                val tag = queries.getTagByName(tagName).executeAsOne()
                queries.linkTagToReflection(reflectionId, tag.id)
            }
        }
    }

    fun getAllReflections(): List<Reflection> {
        return queries.getAllReflections().executeAsList().map { r ->
            val tags = queries.getTagsForReflection(r.id).executeAsList().map { it.name }
            Reflection(r.id, r.quoteContent, r.author, r.note, tags, r.createdAt)
        }
    }

    fun getAllTags(): List<String> {
        return queries.getAllTags().executeAsList().map { it.name }
    }
}
