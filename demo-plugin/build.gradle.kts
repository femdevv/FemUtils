plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation(project(":java"))
    implementation(project(":paper"))
    implementation(project(":compatibility-layer:core"))
}

tasks {
    jar {
        enabled = false
    }
    shadowJar {
        configurations = listOf(project.configurations.runtimeClasspath.get())
        archiveBaseName.set("FemUtils-Demo")
        archiveVersion.set("")
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
}
