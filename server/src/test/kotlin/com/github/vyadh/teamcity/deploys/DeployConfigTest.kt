package com.github.vyadh.teamcity.deploys

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DeployConfigTest {

  @Test
  fun toMap() {
    val config = DeployConfig(
          dashboardEnabled = "true",
          projectKey = "project",
          environmentKey = "environment",
          environments = "development,production",
          refreshSecs = "10"
    )

    val map = config.toMap()

    assertThat(map).isEqualTo(mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, "true"),
          Pair(DeployConfigKeys.projectKey, "project"),
          Pair(DeployConfigKeys.environmentKey, "environment"),
          Pair(DeployConfigKeys.environments, "development,production"),
          Pair(DeployConfigKeys.refreshSecs, "10")
    ))
  }

  @Test
  fun fromMapWhenPopulated() {
    val map = mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, "true"),
          Pair(DeployConfigKeys.projectKey, "project"),
          Pair(DeployConfigKeys.environmentKey, "environment"),
          Pair(DeployConfigKeys.environments, "development,production"),
          Pair(DeployConfigKeys.refreshSecs, "30")
    )

    val config = DeployConfig.fromMap(map)

    assertThat(config).isEqualTo(DeployConfig(
          dashboardEnabled = "true",
          projectKey = "project",
          environmentKey = "environment",
          environments = "development,production",
          refreshSecs = "30"
    ))
  }

  @Test
  fun fromMapWhenNotPopulated() {
    val map = emptyMap<String, String>()

    val config = DeployConfig.fromMap(map)

    assertThat(config).isEqualTo(DeployConfig.disabled)
  }

  @Test
  fun fromMapWhenMissingProperties() {
    val map = mapOf(Pair(DeployConfigKeys.dashboardEnabled, "true"))

    val config = DeployConfig.fromMap(map)

    assertThat(config).isEqualTo(DeployConfig(
          dashboardEnabled = "true",
          projectKey = "",
          environmentKey = "",
          environments = "",
          refreshSecs = ""
    ))
  }

  @Test
  internal fun enabledWhenKeyIndicated() {
    val config = DeployConfig("true", "", "", "", "")

    assertThat(config.isEnabled()).isTrue()
  }

  @Test
  internal fun disabledWhenKeyIndicated() {
    val config = DeployConfig("false", "", "", "", "")

    assertThat(config.isEnabled()).isFalse()
  }

  @Test
  internal fun disabledConfig() {
    val config = DeployConfig.disabled

    assertThat(config).isEqualTo(DeployConfig(
          dashboardEnabled = "false",
          projectKey = "",
          environmentKey = "",
          environments = "",
          refreshSecs = ""
    ))
  }

  @Test
  internal fun environmentsAsList() {
    fun config(envs: String) = DeployConfig("", "", "", envs, "")

    assertThat(config("").environmentsAsList()).isEmpty()
    assertThat(config("DEV").environmentsAsList()).containsOnly("DEV")
    assertThat(config("DEV,UAT,PRD").environmentsAsList()).containsExactly("DEV", "UAT", "PRD")
  }

  @Test
  internal fun environmentsAsListWhenEmptyString() {
    val config = DeployConfig("", "", "", "", "")

    assertThat(config.environmentsAsList()).isEmpty()
  }

}
