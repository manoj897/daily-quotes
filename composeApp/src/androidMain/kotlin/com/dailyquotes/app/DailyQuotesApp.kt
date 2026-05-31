package com.dailyquotes.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.koin.androidContext

class DailyQuotesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@DailyQuotesApp)
        }

        FirebaseApp.initializeApp(this)
        // Disable uploads in debug to avoid noisy reports
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        // Optionally capture uncaught exceptions
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            FirebaseCrashlytics.getInstance().recordException(throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
