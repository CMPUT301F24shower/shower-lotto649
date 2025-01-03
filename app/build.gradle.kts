import org.apache.tools.ant.util.JavaEnvUtils.VERSION_1_8

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
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.library)
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    testImplementation(libs.junit)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("org.robolectric:robolectric:4.9")
    testImplementation("com.google.firebase:firebase-firestore:24.x.x")
    implementation("com.google.firebase:firebase-dynamic-links-ktx:21.0.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1")
    androidTestImplementation("androidx.fragment:fragment-testing:1.5.5")
    implementation("com.google.zxing:core:3.5.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.2.1")
    implementation("androidx.work:work-runtime:2.9.1")
    androidTestImplementation("androidx.work:work-runtime:2.9.1")
    implementation("androidx.concurrent:concurrent-futures:1.1.0")
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation("androidx.camera:camera-core:1.4.0")
    implementation("androidx.camera:camera-lifecycle:1.4.0")
    implementation("androidx.camera:camera-view:1.4.0")
    implementation("androidx.camera:camera-camera2:1.4.0")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
}