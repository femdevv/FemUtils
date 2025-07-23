plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    implementation(project(":java"))
    implementation(project(":compatibility-layer:core"))
    implementation(project(":compatibility-layer:version-1_21_1"))
    implementation(project(":compatibility-layer:version-1_21_4"))
}

tasks.shadowJar {
    archiveBaseName.set("FemUtils-Paper")
    archiveVersion.set("")
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "FemUtils-Paper"
            artifact(tasks.named("shadowJar").get()) {
                classifier = null
            }
        }
    }
    repositories {
        mavenLocal()
    }
}
