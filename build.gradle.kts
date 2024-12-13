/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("io.ktor.plugin") version "3.0.1"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.4.0"
}

group = "kotlinx.rpc.sample"
version = "0.0.1"

application {
    mainClass.set("ServerKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    // Client API
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:0.4.0")
    // Server API
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:0.4.0")
    // Serialization module. Also, protobuf and cbor are provided
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-protobuf:0.4.0")

    // Transport implementation for Ktor
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server:0.4.0")

    // Ktor API
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:1.5.12")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.0.10")
}
