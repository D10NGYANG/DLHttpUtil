val bds100MavenUsername: String by project
val bds100MavenPassword: String by project

plugins {
    kotlin("multiplatform") version "2.0.10"
    kotlin("plugin.serialization") version "2.0.10"
    id("maven-publish")
    id("com.github.ben-manes.versions") version "0.51.0"
}

group = "com.github.D10NGYANG"
version = "1.0.2"

repositories {
    mavenCentral()
    maven("https://raw.githubusercontent.com/D10NGYANG/maven-repo/main/repository")
}

kotlin {
    jvmToolchain(8)
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }

    sourceSets {
        val kotlinKtorVer = "2.3.12"
        val kotlinSerializationJsonVer = "1.7.1"
        val kotlinCoroutinesVer = "1.8.1"
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                // ktor核心库
                implementation("io.ktor:ktor-client-core:$kotlinKtorVer")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-cio:$kotlinKtorVer")
                implementation("io.ktor:ktor-client-logging:$kotlinKtorVer")
                implementation("io.ktor:ktor-client-content-negotiation:$kotlinKtorVer")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$kotlinKtorVer")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationJsonVer")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVer")
                // 通用工具
                implementation("com.github.D10NGYANG:DLCommonUtil:0.2.0")
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