plugins {
    alias(libs.plugins.recipehog.android.library.compose)
}

android {
    namespace = "com.portfolio.core.presentation.designsystem"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.compose)

    implementation(libs.bundles.coil)

    implementation(libs.bundles.camera)

    implementation(projects.core.domain)

    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}