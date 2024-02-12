plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}
apply("../shared_dependencies.gradle")

android {
    namespace = "com.juanarton.chargingcurrentcontroller"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.juanarton.chargingcurrentcontroller"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
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
}

dependencies {
    implementation(project(":core"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.airbnb.android:lottie:6.0.0")

    implementation("com.github.lzyzsd:circleprogress:1.2.1")

    implementation("nl.joery.animatedbottombar:library:1.1.0")

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.github.fondesa:kpermissions-coroutines:3.4.0")

    //noinspection GradleDependency
    implementation("com.scwang.wave:MultiWaveHeader:1.0.0-andx")
}