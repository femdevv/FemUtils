plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14" apply false
}

allprojects {
    group = "xyz.femdev"
    version = "1.0"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc-repo" }
        maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype" }
        maven("https://jitpack.io") { name = "jitpack" }
    }
}
