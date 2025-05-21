plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.relojfichajeskairos24h"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.relojfichajeskairos24h"
        minSdk = 25 // Actualiza esto a 28 o superior
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true // Asegura que se pueda usar run-as en esta variante
        }
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //noinspection UseTomlInstead
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Para el Inspector de bases de datos
    //noinspection KtxExtensionAvailable,UseTomlInstead,GradleDependency
    implementation ("androidx.sqlite:sqlite:2.1.0")

    //noinspection UseTomlInstead,GradleDependency
    implementation ("com.google.code.gson:gson:2.8.8")

    // Implementación que se encarga de que el contenido sea responsive
    //noinspection UseTomlInstead
    implementation ("androidx.constraintlayout:constraintlayout:2.2.1")
}
