plugins {
    `kotlin-dsl`
}

description = "cool 18n lib"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jreleaser:org.jreleaser.gradle.plugin:1.17.0")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}