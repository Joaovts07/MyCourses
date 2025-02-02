plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    //id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
    kotlin("plugin.serialization") version "2.0.0"
}

android {
    namespace = "com.example.mycourses"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mycourses"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.io.coil.kt.coil.compose)
    implementation(libs.gson)

    implementation(libs.firebase.firestore.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))

    /*//hilt
    implementation(libs.hilt.android)
    implementation(libs.firebase.storage.ktx)
    kapt(libs.hilt.android.compiler )
    implementation(libs.androidx.hilt.navigation.compose)*/

    implementation(libs.kotlinx.serialization.json)

    implementation(project(":login"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}