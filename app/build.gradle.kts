plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.lotto649"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lotto649"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.material:material:1.2.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.zxing:core:3.5.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("org.robolectric:robolectric:4.9")
    testImplementation("com.google.firebase:firebase-firestore:24.x.x")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}