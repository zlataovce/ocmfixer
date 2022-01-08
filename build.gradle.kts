import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.URL

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
}

group = "me.kcra"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    // compileOnly(urlFile("https://github.com/kernitus/BukkitOldCombatMechanics/releases/download/1.10.0/OldCombatMechanics.jar", "ocm"))
    compileOnly("io.netty:netty-all:4.1.72.Final")
    implementation("net.bytebuddy:byte-buddy:1.12.6")
    implementation("net.bytebuddy:byte-buddy-agent:1.12.6")
}

tasks.getByName<ShadowJar>("shadowJar") {
    relocate("net.bytebuddy", "me.kcra.ocmfixer.bytebuddy")
}

fun urlFile(url: String, name: String): FileCollection {
    val file = File("$buildDir/ext/$name.jar")
    file.parentFile.mkdirs()
    if (!file.exists()) {
        URL(url).openStream().use { it.copyTo(file.outputStream()) }
    }
    return files(file.absolutePath)
}