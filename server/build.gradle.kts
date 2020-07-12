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
  testImplementation("org.jetbrains.teamcity.internal:server:${rootProject.extra["teamcityVersion"]}")

  testImplementation("org.assertj:assertj-core:3.16.1")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
  testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
}

teamcity {
  version = rootProject.extra["teamcityVersion"] as String

  server {
    descriptor {
      name = "deployment-dashboard"
      displayName = "Deployment Dashboard"
      version = rootProject.version as String?
      vendorName = "Kieron Wilkinson"
      vendorUrl = "https://github.com/vyadh"
      description = "Dashboard visualising deployments into different environments"
      email = "kieron.wilkinson@gmail.com"
      minimumBuild = "58245"
      useSeparateClassloader = true
      allowRuntimeReload = true
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
