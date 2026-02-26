plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.finance"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.finance"
        minSdk = 27
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    
    // Material Design Components (sollte schon durch libs.material abgedeckt sein)
    // Falls libs.material eine ältere Version ist, können Sie diese Zeile hinzufügen:
    // implementation("com.google.android.material:material:1.11.0")
    
    // RecyclerView für die Transaktionsliste
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // CardView (meist schon in material enthalten, aber sicher ist sicher)
    implementation("androidx.cardview:cardview:1.0.0")
}
