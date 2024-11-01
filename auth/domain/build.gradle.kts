plugins {
    alias(libs.plugins.recipehog.jvm.library)
}

dependencies {
    implementation(projects.core.domain)
}