val bds100MavenUsername: String by project
val bds100MavenPassword: String by project

plugins {
    kotlin("multiplatform") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("maven-publish")
    id("com.github.ben-manes.versions") version "0.51.0"
}

group = "com.github.D10NGYANG"
version = "0.9.3"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                val kotlinKtorVer = "2.3.8"
                api("io.ktor:ktor-client-core:$kotlinKtorVer")
                api("io.ktor:ktor-client-cio:$kotlinKtorVer")
                api("io.ktor:ktor-client-logging:$kotlinKtorVer")
                api("io.ktor:ktor-client-content-negotiation:$kotlinKtorVer")
                api("io.ktor:ktor-serialization-kotlinx-json:$kotlinKtorVer")
                val kotlinSerializationJsonVer = "1.6.3"
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationJsonVer")
                val napierVer = "2.6.1"
                api("io.github.aakira:napier:$napierVer")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                val kotlinCoroutinesVer = "1.8.0"
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVer")
            }
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("/Users/d10ng/project/kotlin/maven-repo/repository")
        }
        maven {
            credentials {
                username = bds100MavenUsername
                password = bds100MavenPassword
            }
            setUrl("https://nexus.bds100.com/repository/maven-releases/")
        }
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}