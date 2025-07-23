pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "FemUtils"
include("java")
include("paper")
include("compatibility-layer:core")
include("compatibility-layer:version-1_21_1")
include("compatibility-layer:version-1_21_4")
include("demo-plugin")