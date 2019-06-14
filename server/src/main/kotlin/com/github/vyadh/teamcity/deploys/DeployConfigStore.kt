package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor

/**
 * Config storage functions that allows finding project hierarchy dependent
 * configuration, or storing configuration on projects (as project features).
 */
class DeployConfigStore {

  companion object {
    const val type = "deployment-dashboard-config"
  }

  /** The config found at this level or first one up the hierarchy. */
  fun findAvailable(project: SProject): DeployConfig {
    return find(project.getAvailableFeaturesOfType(type))
  }

  /** The config found at this level only. */
  fun findOwn(project: SProject): DeployConfig {
    return find(project.getOwnFeaturesOfType(type))
  }

  private fun find(features: Collection<SProjectFeatureDescriptor>): DeployConfig {
    return features.stream()
          .map { DeployConfig.fromMap(it.parameters) }
          .filter { it.isEnabled() }
          .findFirst()
          .orElse(DeployConfig.disabled)
  }

  fun store(project: SProject, config: DeployConfig) {
    val features = project.getOwnFeaturesOfType(type)

    val shouldAdd = features.isEmpty() && config.isEnabled()
    val shouldUpdate = !features.isEmpty() && config.isEnabled()
    val shouldRemove = !features.isEmpty() && !config.isEnabled()

    when {
      shouldAdd -> add(config, project)
      shouldUpdate -> update(config, project, features.first())
      shouldRemove -> remove(project, features.first())
    }
  }

  private fun add(config: DeployConfig, project: SProject) {
    project.addFeature(type, config.toMap())
    project.persist()
  }

  private fun update(config: DeployConfig, project: SProject, feature: SProjectFeatureDescriptor) {
    project.updateFeature(feature.id, type, config.toMap())
    project.persist()
  }

  private fun remove(project: SProject, feature: SProjectFeatureDescriptor) {
    project.removeFeature(feature.id)
    project.persist()
  }

}
