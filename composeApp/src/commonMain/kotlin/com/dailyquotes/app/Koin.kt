package com.dailyquotes.app

import com.dailyquotes.app.screens.QuoteScreenModel
import com.dailyquotes.shared.commonModule
import com.dailyquotes.shared.platformModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    factory { QuoteScreenModel(get(), get()) }
}

fun initKoin(appDeclaration: org.koin.dsl.KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule(), platformModule(), appModule)
}
