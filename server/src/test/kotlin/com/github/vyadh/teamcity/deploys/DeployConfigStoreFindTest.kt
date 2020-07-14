package com.github.vyadh.teamcity.deploys

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DeployConfigStoreFindTest {

  private val store = DeployConfigStore()

  @Test
  fun findAvailableWhenZeroFeatureFound() {
    val project = projectWith(emptyList())

    val result = store.findAvailable(project)

    assertThat(result.isEnabled()).isFalse()
  }

  @Test
  fun findAvailableWhenNoMatchingFeatureFound() {
    val project = projectWith(listOf(feature("other-type", emptyMap())))

    val result = store.findAvailable(project)

    assertThat(result.isEnabled()).isFalse()
  }

  @Test
  fun findAvailableWhenFeatureFound() {
    val params = mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, "true"),
          Pair(DeployConfigKeys.projectKey, "project"),
          Pair(DeployConfigKeys.versionKey, "1.0.0"),
          Pair(DeployConfigKeys.environmentKey, "env"),
          Pair(DeployConfigKeys.environments, "dev,prod"),
          Pair(DeployConfigKeys.customKey, "branch"),
          Pair(DeployConfigKeys.refreshSecs, "10"),
          Pair(DeployConfigKeys.multiEnvConfig, "false")
    )
    val project = projectWith(listOf(feature(type, params)))

    val result = store.findAvailable(project)

    assertThat(result.toMap()).isEqualTo(params)
  }

  @Test
  internal fun findAvailableUsingParentWhenOwnFeatureDisabled() {
    val params = mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, "true"),
          Pair(DeployConfigKeys.projectKey, "project"),
          Pair(DeployConfigKeys.versionKey, "1.0.0"),
          Pair(DeployConfigKeys.environmentKey, "env"),
          Pair(DeployConfigKeys.environments, "dev,prod"),
          Pair(DeployConfigKeys.customKey, "branch"),
          Pair(DeployConfigKeys.refreshSecs, "30"),
          Pair(DeployConfigKeys.multiEnvConfig, "true")
    )
    val project = projectWith(listOf(
          feature(type, DeployConfig.disabled.toMap()),
          feature(type, params)
    ))

    val result = store.findAvailable(project)

    assertThat(result.toMap()).isEqualTo(params)
  }

  @Test
  internal fun findOwnDoesNotInheritFromParent() {
    val params = mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, "true"),
          Pair(DeployConfigKeys.projectKey, "project"),
          Pair(DeployConfigKeys.versionKey, "1.0.0"),
          Pair(DeployConfigKeys.environmentKey, "env"),
          Pair(DeployConfigKeys.environments, "dev,prod"),
          Pair(DeployConfigKeys.customKey, "branch"),
          Pair(DeployConfigKeys.refreshSecs, "10"),
          Pair(DeployConfigKeys.multiEnvConfig, "true")
    )
    val project = projectWithOwn(listOf(
          feature(type, DeployConfig.disabled.toMap()),
          feature(type, params)
    ))

    val result = store.findAvailable(project)

    assertThat(result.isEnabled()).isFalse()
  }

  @Test
  internal fun findOwnDoesSeesOwnConfig() {
    val params = mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, "true"),
          Pair(DeployConfigKeys.projectKey, "project"),
          Pair(DeployConfigKeys.versionKey, "1.0.0"),
          Pair(DeployConfigKeys.environmentKey, "env"),
          Pair(DeployConfigKeys.environments, "dev,prod"),
          Pair(DeployConfigKeys.customKey, "branch"),
          Pair(DeployConfigKeys.refreshSecs, "10"),
          Pair(DeployConfigKeys.multiEnvConfig, "true")
    )
    val project = projectWithOwn(listOf(feature(type, params)))

    val result = store.findOwn(project)

    assertThat(result.toMap()).isEqualTo(params)
  }


  private fun projectWith(features: Collection<SProjectFeatureDescriptor>): SProject {
    return mock {
      on { getAvailableFeaturesOfType(type) } doReturn features
    }
  }

  private fun projectWithOwn(features: Collection<SProjectFeatureDescriptor>): SProject {
    return mock {
      on { getOwnFeaturesOfType(type) } doReturn features
    }
  }

  private fun feature(type: String, params: Map<String, String>): SProjectFeatureDescriptor {
    return mock {
      on { getType() }.doReturn(type)
      on { parameters }.doReturn(params)
    }
  }

  companion object {
    const val type = "deployment-dashboard-config"
  }

}
