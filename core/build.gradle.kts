import org.lwjgl.Lwjgl
import org.lwjgl.Release
import org.lwjgl.lwjgl
import org.lwjgl.sonatype

plugins {
    id("org.lwjgl.plugin") version "0.0.35"
    id("java-library")
}

group = "com.github.ageofwar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    sonatype()
}

dependencies {
    lwjgl {
        version = Release.latest
        implementation(Lwjgl.Module.assimp)
    }

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
