package com.dailyquotes.shared

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
    single { ShareManager(get()) }
}
