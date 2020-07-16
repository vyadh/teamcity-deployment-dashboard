import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

buildscript {
  repositories {
    mavenLocal()
    mavenCentral()
  }

  dependencies {
    classpath("com.github.rodm:gradle-teamcity-plugin:1.2")
  }
}

plugins {
  kotlin("jvm") version "1.3.40"
}

repositories {
  mavenLocal()
  mavenCentral()
}

group = "com.github.vyadh.teamcity"
version = "1.9.0${buildNumber()}"
project.logger.lifecycle("Building version: $version")

extra["teamcityVersion"] = findProperty("teamcity.version") ?: "2018.2"

subprojects {
  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
  }

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
    }
  }
}

fun buildNumber(): String {
  val release = (System.getenv("RELEASE") ?: "false").toBoolean()
  return if (release) "" else "-SNAPSHOT-${timestamp()}"
}

fun timestamp(): String {
  val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
  return LocalDateTime.now().format(formatter)
}
