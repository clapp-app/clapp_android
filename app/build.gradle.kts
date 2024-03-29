/*
 *    Copyright 2023 Maarten de Goede
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.firebase-perf")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "eu.insertcode.clapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "eu.insertcode.clapp"

        minSdk = 19
        targetSdk = 34

        versionCode = 25
        versionName = "onePointSeven"

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        resourceConfigurations.addAll(
                listOf("EN")
        )
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            applicationIdSuffix = ".dev"
            isDebuggable = true
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions { jvmTarget = "1.8" }


    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.multidex)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.browser)

    implementation(Google.android.material)

    // Firebase
    implementation("com.google.firebase:firebase-core:_")
    implementation("com.google.firebase:firebase-perf:_")
    implementation("com.google.firebase:firebase-appindexing:_")
    implementation("com.google.firebase:firebase-crashlytics:_")
    implementation("com.google.firebase:firebase-analytics:_")
}

