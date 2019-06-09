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
  fun findWhenZeroFeatureFound() {
    val project = projectWith(emptyList())

    val result = store.find(project)

    assertThat(result).isNull()
  }

  @Test
  fun findWhenNoMatchingFeatureFound() {
    val project = projectWith(listOf(feature("other-type", emptyMap())))

    val result = store.find(project)

    assertThat(result).isNull()
  }

  @Test
  fun findWhenFeatureFound() {
    val params = mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, "true"),
          Pair(DeployConfigKeys.projectKey, "project"),
          Pair(DeployConfigKeys.environmentKey, "env"),
          Pair(DeployConfigKeys.environments, "dev,prod")
    )
    val project = projectWith(listOf(feature(type, params)))

    val result = store.find(project)

    assertThat(result?.toMap()).isEqualTo(params)
  }

  @Test
  internal fun findUsingParentWhenOwnFeatureDisabled() {
    val params = mapOf(
          Pair(DeployConfigKeys.dashboardEnabled, "true"),
          Pair(DeployConfigKeys.projectKey, "project"),
          Pair(DeployConfigKeys.environmentKey, "env"),
          Pair(DeployConfigKeys.environments, "dev,prod")
    )
    val project = projectWith(listOf(
          feature(type, DeployConfig.disabled.toMap()),
          feature(type, params)
    ))

    val result = store.find(project)

    assertThat(result?.toMap()).isEqualTo(params)
  }


  private fun projectWith(features: Collection<SProjectFeatureDescriptor>): SProject {
    return mock {
      on { getAvailableFeaturesOfType(type) } doReturn features
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
