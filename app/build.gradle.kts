
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val localProperties =  Properties()
localProperties.load( FileInputStream(rootProject.file("local.properties")))


android {
    namespace = "com.example.weatherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            resValue("string", "api_key", localProperties.getProperty("API_KEY"))
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable=true
    }
    buildFeatures {
        compose = true
        dataBinding = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Retrofit
    implementation(libs.logging.interceptor)

    implementation(libs.places)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.common.java8)

    implementation (libs.gson)
    implementation (libs.converter.gson)
//    androidTestImplementation ('androidx.test:rules:1.4.0')
//    androidTestImplementationstImplementation ('androidx.test.espresso:espresso-contrib:3.4.0')


//    def androidx_test = "1.2.0"
//    androidTestImplementation "androidx.test:runner:$androidx_test"
//    androidTestImplementation "androidx.test:core:$androidx_test"
//    androidTestImplementation "androidx.test.ext:junit-ktx:1.1.3"
//    androidTestImplementation "androidx.arch.core:core-testing:2.1.0"



    // Retrofit
    implementation(libs.converter.jackson)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor.v3110)

    implementation (libs.places.v270)





    implementation (libs.gson.v289)
    implementation (libs.converter.gson.v230)



//    implementation("androidx.compose.ui:ui:1.0.0-rc02")
    // Tooling support (Previews, etc.)
//    implementation("androidx.compose.ui:ui-tooling:1.0.0-rc02")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
//    implementation("androidx.compose.foundation:foundation:1.0.0-rc02")
    // Material Design
//    implementation("androidx.compose.material:material:1.0.0-rc02")
    // Material design icons
//    implementation("androidx.compose.material:material-icons-core:1.0.0-rc02")
//    implementation("androidx.compose.material:material-icons-extended:1.0.0-rc02")
    // Integration with observables
//    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-rc02")
//    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.0-rc02")



    // UI Tests
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.0-rc02")

    implementation (libs.androidx.activity.compose.v170alpha02)



}