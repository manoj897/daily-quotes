package com.dailyquotes.shared

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.dailyquotes.db.DailyQuotesDb

class IOSDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(DailyQuotesDb.Schema, "DailyQuotesDb")
    }
}

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory = IOSDatabaseDriverFactory()
