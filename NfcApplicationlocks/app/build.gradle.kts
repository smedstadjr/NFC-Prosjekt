plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.nfcapplicationlocks"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.nfcapplicationlocks"
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.mockito.core)
    implementation(libs.mockito.inline)
    implementation(libs.junit)
    implementation(libs.robolectric)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.robolectric)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)



}