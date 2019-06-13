package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor

class DeployConfigStore {

  companion object {
    const val type = "deployment-dashboard-config"
  }

  fun find(project: SProject): DeployConfig {
    return project.getAvailableFeaturesOfType(type).stream()
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
