pluginManagement {
    includeBuild("build-logic")
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

rootProject.name = "RecipeHog"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
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
include(":recipe:presentation")
include(":recipe:domain")
include(":recipe:data")
include(":core:data")
include(":discover:presentation")
include(":discover:domain")
include(":discover:data")
include(":bookmarks:presentation")
include(":profile:presentation")
include(":review:presentation")
include(":review:domain")
include(":review:data")
