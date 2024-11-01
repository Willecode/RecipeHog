package com.portfolio.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    extensionType: ExtensionType
) {
    commonExtension.run {
        buildFeatures {
            buildConfig = true
        }

        when(extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType()
                            isDebuggable = true
                        }
                        release {
                            configureReleaseBuildTypeApplication(commonExtension)
                            isDebuggable = false
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType()
                        }
                        release {
                            configureReleaseBuildType()
                        }
                    }
                }
            }
        }
    }
}

private fun BuildType.configureDebugBuildType() {
    buildConfigField("String", "EMU_HOST", "\"10.0.2.2\"")
    buildConfigField("int", "EMU_AUTH_PORT", "9099")
    buildConfigField("int", "EMU_FIRESTORE_PORT", "8080")
    buildConfigField("int", "EMU_STORAGE_PORT", "9199")
}

private fun BuildType.configureReleaseBuildType() {
    buildConfigField("String", "EMU_HOST", "\"UNSPECIFIED\"")
    buildConfigField("int", "EMU_AUTH_PORT", "0")
    buildConfigField("int", "EMU_FIRESTORE_PORT", "0")
    buildConfigField("int", "EMU_STORAGE_PORT", "0")
}

private fun BuildType.configureReleaseBuildTypeApplication(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    buildConfigField("String", "EMU_HOST", "\"UNSPECIFIED\"")
    buildConfigField("int", "EMU_AUTH_PORT", "0")
    buildConfigField("int", "EMU_FIRESTORE_PORT", "0")
    buildConfigField("int", "EMU_STORAGE_PORT", "0")

    isMinifyEnabled = true
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}