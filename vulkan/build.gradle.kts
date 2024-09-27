import org.lwjgl.*

plugins {
    id("java-library")
    id("application")
    id("org.lwjgl.plugin") version "0.0.35"
}

application {
    mainClass.set("com.github.ageofwar.ragna.Main")
}

group = "com.github.ageofwar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    sonatype()
}

dependencies {
    api(project(":core"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    lwjgl {
        version = Release.latest
        implementation(Lwjgl.Preset.minimalVulkan)
    }
}

tasks.test {
    useJUnitPlatform()
}
