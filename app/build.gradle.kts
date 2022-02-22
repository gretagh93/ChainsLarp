buildscript {
    repositories {
        google()
    }
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "1.5.30-1.0.0"
    //idea
}

repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

android {
    compileSdkVersion(31)
    buildToolsVersion = "30.0.3"

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-XXLanguage:+InlineClasses",
            "-Xallow-result-return-type",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-P", "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"

        )
        useIR = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.5"
    }

    buildFeatures {
        compose = true
    }

    dexOptions {
        javaMaxHeapSize = "12g"
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.chains.larp"
        minSdkVersion(22)
        targetSdkVersion(31)
        versionCode = 20
        versionName = "0.6"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.8.1")
    //Mini
    val duo_version = "872a26365d63c81a8ec0f344c82d53094858000b"
    implementation("com.github.minikorp.duo:duo-common:$duo_version")
    //implementation("com.github.minikorp.duo:duo-compose:$duo_version") Update to 07
    ksp("com.github.minikorp.duo:duo-ksp:$duo_version")

    //Compose
    val composeVersion = "1.0.5"
    implementation("androidx.compose.ui:ui:$composeVersion")
    //Tooling support (Previews, etc.")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    //Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    //Material Desig")
    implementation("androidx.compose.material:material:$composeVersion")
    //Material design icon")
    implementation("androidx.compose.material:material-icons-core:$composeVersion")
    // implementation("androidx.compose.material:material-icons-extended:$compose_version")

    //integration with navigation
    //   implementation("androidx.navigation:navigation-compose:2.4.0-alpha08")

    //Accompanist
    val accompanistVersion = "0.17.0"
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanistVersion")

    //DI
    val kodeinVersion = "7.6.0"
    implementation("org.kodein.di:kodein-di-framework-android-x:$kodeinVersion")
    implementation("org.kodein.di:kodein-di-framework-compose:$kodeinVersion")

    //Coroutines / rx
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    //AndroidX
    implementation("androidx.appcompat:appcompat:1.4.0-rc01")
    implementation("com.google.android.material:material:1.4.0")

    //Network
    val retrofit_version = "2.9.0"
    val okhttp_version = "4.7.2"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    
    //Util
    implementation("com.github.minikorp:grove:1.0.3")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    testImplementation("junit:junit:4.13.2")
}