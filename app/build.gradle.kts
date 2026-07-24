import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use(::load)
}

android {
    namespace = "nl.rhaydus.nestbox"
    compileSdk = 37

    defaultConfig {
        applicationId = "nl.rhaydus.nestbox"
        // The nl.rhaydus foundation libraries declare minSdk 26, so the app cannot sit below it.
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Not a secret (device flow has none), but kept out of VCS via local.properties.
        buildConfigField(
            "String",
            "GITHUB_CLIENT_ID",
            "\"${localProperties.getProperty("GITHUB_CLIENT_ID", "")}\"",
        )

        // The private repository the publications are read from, as "owner/name".
        buildConfigField(
            "String",
            "DOVELETTER_REPOSITORY",
            "\"${localProperties.getProperty("DOVELETTER_REPOSITORY", "")}\"",
        )

        buildConfigField(
            "String",
            "DOVELETTER_BRANCH",
            "\"${localProperties.getProperty("DOVELETTER_BRANCH", "main")}\"",
        )
    }

    buildTypes {
        debug {
            // A subscriber's personal access token, read from local.properties so the content layer
            // can reach the private repository before the token-entry UI exists. Declared empty in
            // release, so no build ever ships a baked-in credential.
            buildConfigField(
                "String",
                "DOVELETTER_TOKEN",
                "\"${localProperties.getProperty("DOVELETTER_TOKEN", "")}\"",
            )
        }

        release {
            buildConfigField(
                "String",
                "DOVELETTER_TOKEN",
                "\"\"",
            )

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    // nl.rhaydus foundation
    implementation(libs.rhaydus.toad)
    implementation(libs.rhaydus.core.common)
    implementation(libs.rhaydus.core.platform)
    implementation(libs.rhaydus.designsystem.core)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    // The design system's empty states call for expressive glyphs, which icons-core does not carry.
    // Note this artifact ships whole: `isMinifyEnabled` is false on release, so R8 never runs and
    // never strips the icons the app does not reference. Turning minification on would reclaim it.
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.vintage.engine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // DI
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Navigation
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.koin)
    implementation(libs.voyager.tabNavigator)

    // Networking
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)

    // Markdown parsing
    implementation(libs.jetbrains.markdown)
}