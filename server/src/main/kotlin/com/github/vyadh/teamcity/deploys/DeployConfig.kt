package com.github.vyadh.teamcity.deploys

/**
 * Deployment configuration and associated conversion functions, stringly-typed for simplicity
 * since most of it is used as string or sent to the frontend as strings anyway.
 *
 * @param projectKey configuration property name to lookup the project name or blank
 * if the project name itself should be used.
 * @param versionKey configuration property name to lookup the version number or blank
 * if the TeamCity build number should be used.
 * @param environmentKey configuration property name to lookup the environment name
 * or blank if the build type name should be used.
 * @param environments comma-delimited string of environments to show in the dashboard.
 * @param customKey extra information taken from properties, e.g. branch name.
 * @param refreshSecs interval between refreshes, or blank for no referesh.
 * @param multiEnvConfig false to use [com.github.vyadh.teamcity.deploys.buildfinder.LastBuildFinder]
 *   when only one environment per configuration, or [com.github.vyadh.teamcity.deploys.buildfinder.MultiBuildFinder]
 *   when more than one environment can be deployed to from one build configuration.
 */
data class DeployConfig(
      val dashboardEnabled: String = "false",
      val projectKey: String = "",
      val versionKey: String = "",
      val environmentKey: String = "",
      val environments: String = "",
      val customKey: String = "",
      val refreshSecs: String = "",
      val multiEnvConfig: String = "false"
) {

  companion object {
    val disabled = DeployConfig(
          dashboardEnabled = "false",
          projectKey = "",
          versionKey = "",
          environmentKey = "",
          environments = "",
          customKey = "",
          refreshSecs = "",
          multiEnvConfig = "false"
    )

    fun fromMap(map: Map<String, String>): DeployConfig {
      return DeployConfig(
            dashboardEnabled = map.getOrDefault(DeployConfigKeys.dashboardEnabled,"false"),
            projectKey = map.getOrDefault(DeployConfigKeys.projectKey,""),
            versionKey = map.getOrDefault(DeployConfigKeys.versionKey,""),
            environmentKey = map.getOrDefault(DeployConfigKeys.environmentKey,""),
            environments = map.getOrDefault(DeployConfigKeys.environments,""),
            customKey = map.getOrDefault(DeployConfigKeys.customKey, ""),
            refreshSecs = map.getOrDefault(DeployConfigKeys.refreshSecs, ""),
            multiEnvConfig = map.getOrDefault(DeployConfigKeys.multiEnvConfig, "false")
      )
    }
  }

  fun isEnabled(): Boolean {
    return dashboardEnabled.toBoolean()
  }

  fun isMultiEnvConfig(): Boolean {
    return multiEnvConfig.toBoolean()
  }

  fun toMap(): Map<String, String> {
    return mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, dashboardEnabled),
          Pair(DeployConfigKeys.projectKey, projectKey),
          Pair(DeployConfigKeys.versionKey, versionKey),
          Pair(DeployConfigKeys.environmentKey, environmentKey),
          Pair(DeployConfigKeys.environments, environments),
          Pair(DeployConfigKeys.customKey, customKey),
          Pair(DeployConfigKeys.refreshSecs, refreshSecs),
          Pair(DeployConfigKeys.multiEnvConfig, multiEnvConfig)
    )
  }

  fun environmentsAsList(): List<String> {
    val items = environments
          .split(',')
          .map { it.trim() }

    return if (items.size == 1 && items[0].isEmpty()) emptyList()
           else items
  }

}
