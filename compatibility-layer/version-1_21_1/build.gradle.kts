plugins {
    id("java")
    id("io.papermc.paperweight.userdev")
}

dependencies {
    implementation(project(":compatibility-layer:core"))
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
}