val bds100MavenUsername: String by project
val bds100MavenPassword: String by project

plugins {
    kotlin("multiplatform") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("maven-publish")
}

group = "com.github.D10NGYANG"
version = "0.7"

repositories {
    mavenCentral()
    maven("https://jitpack.io" )
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                val kotlinKtorVer = "2.2.1"
                api("io.ktor:ktor-client-core:$kotlinKtorVer")
                api("io.ktor:ktor-client-cio:$kotlinKtorVer")
                api("io.ktor:ktor-client-logging:$kotlinKtorVer")
                api("io.ktor:ktor-client-content-negotiation:$kotlinKtorVer")
                api("io.ktor:ktor-serialization-kotlinx-json:$kotlinKtorVer")
                val kotlinSerializationJsonVer = "1.4.1"
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationJsonVer")
                val napierVer = "2.6.1"
                api("io.github.aakira:napier:$napierVer")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                val kotlinCoroutinesVer = "1.6.4"
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