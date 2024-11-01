plugins {
    alias(libs.plugins.recipehog.android.feature.ui)
}

android {
    namespace = "com.portfolio.auth.presentation"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(projects.core.domain)
    implementation(projects.auth.domain)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}