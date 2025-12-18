package com.dailyquotes.shared

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.dailyquotes.db.DailyQuotesDb

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(DailyQuotesDb.Schema, context, "DailyQuotesDb")
    }
}
