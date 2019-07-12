package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.processing.BuildMocks.buildTypeWith
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.buildWith
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SFinishedBuild
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeployEnvironmentTest {

  private val envKey = "ENVIRONMENT"
  private val noKey = ""
  private val converter = BuildAttributeConverter()

  @Test
  fun environmentNameFromBuildType() {
    val build = buildWithTypeName("DEV")

    val env = DeployEnvironment(noKey, listOf("DEV"), converter).name(build)

    assertThat(env).isEqualTo("DEV")
  }

  @Test
  fun environmentNameFromBuildProperty() {
    val build = build("DEV")

    val env = DeployEnvironment(envKey, listOf("DEV"), converter).name(build)

    assertThat(env).isEqualTo("DEV")
  }

  @Test
  fun environmentIsNormalisedToSuppliedList() {
    val build = build("dev")

    val env = DeployEnvironment(envKey, listOf("DEV"), converter).name(build)

    assertThat(env).isEqualTo("DEV")
  }

  @Test
  fun environmentIsNotNormalisedWhenMissingFromSuppliedList() {
    val build = build("prod")

    val env = DeployEnvironment(envKey, listOf("DEV", "PRD"), converter).name(build)

    assertThat(env).isEqualTo("prod")
  }

  @Test
  fun environmentsAreAllNormalisedToSuppliedList() {
    val environments = DeployEnvironment(envKey, listOf("Dev", "UAT", "Prod"), converter)

    assertThat(environments.name(build("DEV"))).isEqualTo("Dev")
    assertThat(environments.name(build("uat"))).isEqualTo("UAT")
    assertThat(environments.name(build("prod"))).isEqualTo("Prod")
  }

  @Test
  fun indicateWhenEnvironmentIsKnown() {
    val environments = DeployEnvironment(envKey, listOf("Dev", "UAT", "Prod"), converter)

    assertThat(environments.contains(build("DEV"))).isTrue()
    assertThat(environments.contains(build("uat"))).isTrue()
    assertThat(environments.contains(build("prod"))).isTrue()
    assertThat(environments.contains(build("test"))).isFalse()
  }


  @Suppress("SameParameterValue")
  private fun buildWithTypeName(env: String): SBuild {
    return buildWith(buildTypeWith(env, "Project"), "1.0", emptyMap())
  }

  private fun build(env: String): SFinishedBuild = mock {
    on { buildOwnParameters } doReturn mapOf(
          Pair("PROJECT", "Project"), Pair(envKey, env))
  }

}
