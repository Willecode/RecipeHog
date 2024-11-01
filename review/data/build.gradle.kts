plugins {
    alias(libs.plugins.recipehog.android.library)
}

android {
    namespace = "com.portfolio.review.data"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.bundles.koin)

    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.review.domain)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}