plugins {
    alias(libs.plugins.google.gms.google.services) //Firebase dependency
    alias(libs.plugins.kotlin.serialization) // for type-safe navigation
    alias(libs.plugins.recipehog.android.application.compose)
}

android {
    namespace = "com.portfolio.recipehog"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.koin.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material3.adaptive.navigation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.bundles.compose.debug)

    implementation(projects.auth.presentation)
    implementation(projects.auth.domain)
    implementation(projects.auth.data)
    implementation(projects.home.presentation)
    implementation(projects.home.data)
    implementation(projects.recipe.presentation)
    implementation(projects.recipe.data)
    implementation(projects.discover.presentation)
    implementation(projects.discover.data)
    implementation(projects.review.presentation)
    implementation(projects.review.data)
    implementation(projects.bookmarks.presentation)
    implementation(projects.profile.presentation)
    implementation(projects.core.presentation.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.data)
}