plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// OneDrive can mark outputs as reparse points and block Gradle cleanup.
// Keep module build outputs in LOCALAPPDATA to avoid AccessDenied on package tasks.
val localBuildRoot = System.getenv("LOCALAPPDATA") ?: "C:/temp"
layout.buildDirectory.set(file("$localBuildRoot/ecohack-mobile-app-build"))

val legacyDebugApkDir = file("$projectDir/build/outputs/apk/debug")
val legacyRedirectDir = file(
    "$projectDir/build/intermediates/apk_ide_redirect_file/debug/createDebugApkListingFileRedirect"
)
val relocatedDebugApkDir = layout.buildDirectory.dir("outputs/apk/debug")

val writeLegacyRedirect by tasks.registering {
    mustRunAfter("packageDebug")
    doLast {
        copy {
            from(relocatedDebugApkDir)
            into(legacyDebugApkDir)
            include("*.apk", "output-metadata.json")
        }
        legacyRedirectDir.mkdirs()
        file("$legacyRedirectDir/redirect.txt").writeText(
            "#- File Locator -\nlistingFile=../../../../outputs/apk/debug/output-metadata.json\n"
        )
    }
}

tasks.matching { it.name == "packageDebug" }.configureEach {
    finalizedBy(writeLegacyRedirect)
}

android {
    namespace = "com.ecoquest.app"
    compileSdk = 36

    val configuredBaseUrl = (project.findProperty("BASE_URL") as String?)?.trim().orEmpty()
    val baseUrl = when {
        configuredBaseUrl.isBlank() -> "http://10.0.2.2:8080/"
        configuredBaseUrl.endsWith("/") -> configuredBaseUrl
        else -> "$configuredBaseUrl/"
    }

    defaultConfig {
        applicationId = "com.ecoquest.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "BASE_URL", "\"http://10.20.185.50:8080/\"")
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "USE_FAKE_USER_REPOSITORY", "false")
            buildConfigField("boolean", "USE_FAKE_TASK_REPOSITORY", "false")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("boolean", "USE_FAKE_USER_REPOSITORY", "false")
            buildConfigField("boolean", "USE_FAKE_TASK_REPOSITORY", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Serialization & Coroutines
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    // Image loading
    implementation(libs.coil.compose)
}
