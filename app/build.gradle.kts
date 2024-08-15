plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.trengginas"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.trengginas"
        minSdk = 31
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

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

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:deprecation")
    }
}


dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0-alpha01")
    implementation("com.google.android.material:material:1.11.0-alpha03")
    implementation("com.android.volley:volley:1.2.1")

// Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

// Converter Gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// OkHttp
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")

    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha10")

    implementation("androidx.core:core-splashscreen:1.0.1")

}