plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.kanha.cli"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kanha.cli"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.2.0"
    }

    buildFeatures {
        buildConfig = true
    }

    val movieBoxBackendUrl = providers.gradleProperty("MOVIEBOX_BACKEND_URL").orElse("").get()
    defaultConfig {
        buildConfigField("String", "MOVIEBOX_BACKEND_URL", "\"$movieBoxBackendUrl\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
