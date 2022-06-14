plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("maven-publish")
}

group = "com.github.D10NGYANG"
version = "0.5"

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
                val kotlin_ktor_ver = "2.0.2"
                api("io.ktor:ktor-client-core:$kotlin_ktor_ver")
                api("io.ktor:ktor-client-cio:$kotlin_ktor_ver")
                api("io.ktor:ktor-client-logging:$kotlin_ktor_ver")
                api("io.ktor:ktor-client-content-negotiation:$kotlin_ktor_ver")
                api("io.ktor:ktor-serialization-kotlinx-json:$kotlin_ktor_ver")
                val kotlin_serialization_json = "1.3.3"
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_serialization_json")
                val napier_version = "2.6.1"
                api("io.github.aakira:napier:$napier_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                val kotlin_coroutines_ver = "1.6.2"
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_ver")
            }
        }
    }
}