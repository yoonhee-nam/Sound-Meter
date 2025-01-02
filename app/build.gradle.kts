plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.app.soundmeter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.soundmeter"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)    //lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx.v270)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // hilt
    //noinspection GradleDependency
    implementation(libs.hilt.android.v248)
    kapt(libs.hilt.android.compiler.v248)
    //noinspection GradleDependency
    implementation (libs.androidx.hilt.navigation.compose)
    //noinspection GradleDependency
    kapt (libs.androidx.hilt.compiler)

    // For Jetpack Compose.
    implementation(libs.compose)
    // For `compose`. Creates a `ChartStyle` based on an M2 Material Theme.
    implementation(libs.compose.m2)
    // For `compose`. Creates a `ChartStyle` based on an M3 Material Theme.
    implementation(libs.compose.m3)
    // Houses the core logic for charts and other elements. Included in all other modules.
    implementation(libs.core)
    // For the view system.
    implementation(libs.views)

    //ad
    implementation(libs.play.services.ads)
}

kapt {
    correctErrorTypes = true
}