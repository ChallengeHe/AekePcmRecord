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
        maven {
            isAllowInsecureProtocol = true
//            url = uri("/repo")
            url = uri("http://192.168.190.31:8081/repository/maven-releases/")
            credentials {
                username = "admin"
                password = "aeke@2023"
            }
        }
    }
}

rootProject.name = "AekePcmRecord"
include(":app")
include(":audio-record")
