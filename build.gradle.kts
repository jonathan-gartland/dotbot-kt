import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.71"
    id("org.jmailen.kotlinter") version "1.20.1"
    application
}

application { mainClassName = "dotbotKt.MainKt" }

group = "stevenknight"
version = "0.0.1"

repositories {
    mavenCentral()
}


dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(group = "com.github.ajalt", name = "clikt", version = "1.5.0")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

