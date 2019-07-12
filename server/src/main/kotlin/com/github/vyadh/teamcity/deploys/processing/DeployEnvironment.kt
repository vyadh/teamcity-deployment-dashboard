package com.github.vyadh.teamcity.deploys.processing

import jetbrains.buildServer.serverSide.SBuild

/**
 * Encapsulates deployment environments so that the name can be normalised to the case
 * provided by the dashboard configuration.
 */
class DeployEnvironment(
      private val key: String,
      val list: List<String>,
      private val converter: BuildAttributeConverter) {

  fun name(build: SBuild): String {
    val name = converter.environmentName(build, key)
    val normalised = list.find { it.equals(name, ignoreCase = true) }
    return normalised ?: name
  }

  fun contains(build: SBuild): Boolean {
    val name = converter.environmentName(build, key)
    return list.any { it.equals(name, ignoreCase = true) }
  }

}
