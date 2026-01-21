plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.english.tutor"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.english.tutor"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

// ⭐ AÑADE ESTAS 2 LÍNEAS:
        buildConfigField("String", "BASE_URL", "\"${project.properties["ollamaUrl"] ?: "http://localhost:11434/"}\"")
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
        buildConfig = true  // ⭐ AÑADE ESTA LÍNEA
    }
}

dependencies {
    // Android Core & Lifecycle
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    // COMPOSE UI & MATERIAL
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.ui:ui-graphics:1.6.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-core:1.6.0")

    // Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // Lifecycle Compose (Resuelve 'LocalLifecycleOwner')
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.2")

    // ✅ CORRECCIÓN CRÍTICA: Proporciona la función 'viewModel()' para Composable.
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    // RETROFIT & COROUTINES (Para la API de Ollama)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ⭐⭐⭐ CORRECCIÓN: Añadido OkHttp Logging Interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // TESTING
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0")
}