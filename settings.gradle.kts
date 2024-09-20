pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "RecipeHog"
include(":app")
include(":auth:presentation")
include(":core:presentation:designsystem")
include(":core:presentation:ui")
include(":auth:domain")
include(":auth:data")
include(":core:domain")
include(":home:presentation")
include(":home:data")
include(":home:domain")
