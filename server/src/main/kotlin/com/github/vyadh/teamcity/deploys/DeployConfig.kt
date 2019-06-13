package com.github.vyadh.teamcity.deploys

/**
 * Deployment configuration and associated conversion functions, stringly-typed for simplicity.
 *
 * @see DeployFinder for more information on project and environment keys.
 */
data class DeployConfig(
      val dashboardEnabled: String,
      val projectKey: String,
      val environmentKey: String,
      val environments: String
) {

  companion object {
    val disabled = DeployConfig(
          dashboardEnabled = "false",
          projectKey = "",
          environmentKey = "",
          environments = ""
    )

    fun fromMap(map: Map<String, String>): DeployConfig {
      return DeployConfig(
            dashboardEnabled = map.getOrDefault(DeployConfigKeys.dashboardEnabled,"false"),
            projectKey = map.getOrDefault(DeployConfigKeys.projectKey,""),
            environmentKey = map.getOrDefault(DeployConfigKeys.environmentKey,""),
            environments = map.getOrDefault(DeployConfigKeys.environments,"")
      )
    }
  }

  fun isEnabled(): Boolean {
    return dashboardEnabled.toBoolean()
  }

  fun toMap(): Map<String, String> {
    return mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, dashboardEnabled),
          Pair(DeployConfigKeys.projectKey, projectKey),
          Pair(DeployConfigKeys.environmentKey, environmentKey),
          Pair(DeployConfigKeys.environments, environments)
    )
  }

  fun environmentsAsList(): List<String> {
    val items = environments.split(',')
    return if (items.size == 1 && items[0].isEmpty()) emptyList()
           else items
  }

}
