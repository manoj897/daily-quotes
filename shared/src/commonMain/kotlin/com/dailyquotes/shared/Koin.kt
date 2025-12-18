package com.dailyquotes.shared

import com.dailyquotes.db.DailyQuotesDb
import org.koin.core.module.Module
import org.koin.dsl.module

fun commonModule() = module {
    single { createHttpClient() }
    single { QuoteRepository(get()) }
    single { 
        val driverFactory: DatabaseDriverFactory = get()
        DailyQuotesDb(driverFactory.createDriver())
    }
    single { ReflectionRepository(get()) }
}

expect fun platformModule(): Module
