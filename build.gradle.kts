plugins {
    kotlin("multiplatform") version "1.9.20-RC"
    id("com.squareup.wire") version "4.9.1"
}

group = "moe.lv5"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("com.squareup.wire:wire-gradle-plugin:4.9.1")
        classpath("com.squareup.wire:wire-gradle-plugin:4.9.1")
    }
}

wire {
    sourcePath {
        srcDir("src/commonMain/proto")
    }
    kotlin {
        rpcRole = "client"
        rpcCallStyle = "suspending"
    }
}


kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "moe.lv5.main"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.squareup.wire:wire-grpc-client:4.9.1")
                implementation("io.ktor:ktor-client-core:2.3.5")
            }
        }
        val nativeMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-curl:2.3.5")
                implementation("com.github.ajalt.clikt:clikt:4.2.1")
            }
        }
        val nativeTest by getting
    }
}
