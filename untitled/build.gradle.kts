plugins {
    kotlin("jvm") version "1.9.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation('androidx.core:core-ktx:1.6.0')
    implementation('androidx.appcompat:appcompat:1.3.1')
    implementation('com.google.android.material:material:1.4.0')
    implementation('androidx.constraintlayout:constraintlayout:2.1.0')
    implementation('com.squareup.retrofit2:retrofit:2.9.0')
    implementation('com.squareup.retrofit2:converter-gson:2.9.0')
    implementation('androidx.room:room-runtime:2.3.0')
    kapt 'androidx.room:room-compiler:2.3.0'
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}