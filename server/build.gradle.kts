import com.github.rodm.teamcity.TeamCityPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
}

apply {
  plugin("com.github.rodm.teamcity-server")
}

repositories {
  mavenCentral()
  maven(url = "http://download.jetbrains.com/teamcity-repository")
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))

  compileOnly("org.jetbrains.teamcity.internal:server:${rootProject.extra["teamcityVersion"]}")

  testCompile("org.assertj:assertj-core:3.12.2")
  testCompile("org.junit.jupiter:junit-jupiter-api:5.4.2")
  testCompile("org.junit.jupiter:junit-jupiter-params:5.4.2")
  testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

teamcity {
  version = rootProject.extra["teamcityVersion"] as String

  server {
    descriptor {
      name = "Deployment Dashboard"
      displayName = "Deployment Dashboard"
      version = rootProject.version as String?
      vendorName = "Kieron Wilkinson"
      vendorUrl = "https://github.com/vyadh"
      description = "Deployment Dashboard for TeamCity"
      email = "kieron.wilkinson@gmail.com"
      useSeparateClassloader = true
      minimumBuild = "58245"
    }
  }
}

// Extension function to allow cleaner configuration
fun Project.teamcity(configuration: TeamCityPluginExtension.() -> Unit) {
  configure(configuration)
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
