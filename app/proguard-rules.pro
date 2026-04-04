# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# kotlinx-serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1>$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room - keep generated database and DAO implementations
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class **_Impl { *; }
-keep @androidx.room.Dao class *
-keepclassmembers @androidx.room.Dao class * { *; }
-keep @androidx.room.Entity class * { *; }

# WorkManager
-keep class * extends androidx.work.WorkerParameters
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# AndroidX Startup
-keep class * extends androidx.startup.Initializer { *; }
