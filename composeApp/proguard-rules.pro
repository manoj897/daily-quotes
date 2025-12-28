# Keep Compose runtime
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# Keep Koin DI
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Keep SQLDelight
-keep class app.cash.sqldelight.** { *; }
-keep class com.dailyquotes.db.** { *; }
-dontwarn app.cash.sqldelight.**

# Keep Ktor HTTP client
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# SLF4J - Ktor logging dependency
-dontwarn org.slf4j.**
-keep class org.slf4j.** { *; }

# Keep data classes and models
-keep class com.dailyquotes.shared.** { *; }
-keep class com.dailyquotes.app.** { *; }

# Keep Voyager navigation
-keep class cafe.adriel.voyager.** { *; }
-dontwarn cafe.adriel.voyager.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep generic signatures
-keepattributes Signature
-keepattributes Exceptions

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep datetime
-keep class kotlinx.datetime.** { *; }
-dontwarn kotlinx.datetime.**
