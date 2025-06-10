plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.moodify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.moodify"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders.put("redirectSchemeName", "moodify")
        manifestPlaceholders.put("redirectHostName", "callback")
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
}

dependencies {

    // Untuk permintaan HTTP
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Untuk parsing JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Dependensi Spotify SDK jika Anda sudah menggunakannya untuk otentikasi lain
    // implementation 'com.spotify.android:auth:1.2.6' // Sesuaikan dengan versi yang Anda gunakan

    // Circle ImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")
//    implementation("com.google.code.gson:gson:2.6.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.gson) // You have gson declared twice. You can remove the older one.
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
//    runtimeOnly("com.fasterxml.jackson.core:jackson-annotations:2.15.2")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation ("androidx.browser:browser:1.7.0")
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
    implementation(files("libs/spotify-auth-release-2.1.0.aar"))
//    implementation(files("libs/spotify-auth-store-release-2.1.0.aar"))
}