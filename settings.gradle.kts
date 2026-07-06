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
        // Dev: versões da Hotfy ainda não publicadas no Central saem daqui.
        // Remover quando estiverem no Maven Central.
        mavenLocal {
            content { includeGroup("io.github.hotfyllc") }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "Pulse"
include(":app")
