import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// Load local.properties for production ad IDs (not committed to version control)
val localProperties = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { load(it) }
    }
}

android {
    namespace = "dev.krgm4d.shiroguessr"
    compileSdk {
        version = release(36)
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: ""
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }

    defaultConfig {
        applicationId = "dev.krgm4d.shiroguessr"
        minSdk = 29
        targetSdk = 36
        versionCode = (System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1)
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // AdMob App ID for manifest placeholder
        manifestPlaceholders["admobAppId"] = localProperties.getProperty(
            "ADMOB_APP_ID",
            "ca-app-pub-3940256099942544~3347511713"  // Google test app ID
        )
    }

    buildTypes {
        debug {
            // Use Google-provided test ad unit IDs in debug builds
            buildConfigField(
                "String",
                "ADMOB_INTERSTITIAL_AD_UNIT_ID",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            // Use production ad unit ID from local.properties, fallback to test ID
            buildConfigField(
                "String",
                "ADMOB_INTERSTITIAL_AD_UNIT_ID",
                "\"${localProperties.getProperty(
                    "ADMOB_INTERSTITIAL_AD_UNIT_ID",
                    "ca-app-pub-3940256099942544/1033173712"
                )}\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.google.fonts)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.play.services.ads)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}