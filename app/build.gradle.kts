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
        versionCode = 2
        versionName = "0.3.0"
    }

    buildFeatures { buildConfig = true }

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

dependencies {
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")
}