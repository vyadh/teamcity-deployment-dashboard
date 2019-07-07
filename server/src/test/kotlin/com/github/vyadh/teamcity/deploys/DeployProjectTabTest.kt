package com.github.vyadh.teamcity.deploys

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor
import jetbrains.buildServer.web.openapi.PagePlaces
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeployProjectTabTest {

  @Test
  internal fun isNotAvailableWhenProjectIsNull() {
    val tab = createTab()

    val available = tab.isTabShowing(null)

    assertThat(available).isFalse()
  }

  @Test
  internal fun isNotAvailableWhenProjectHasDisabledConfiguration() {
    val tab = createTab()
    val project = projectWithConfig(DeployConfig.disabled)

    val available = tab.isTabShowing(project)

    assertThat(available).isFalse()
  }

  @Test
  internal fun isAvailableWhenProjectHasEnabledConfiguration() {
    val tab = createTab()
    val project = projectWithConfig(
          DeployConfig("true", "project", "1.0", "env", "dev", "10", "true"))

    val available = tab.isTabShowing(project)

    assertThat(available).isTrue()
  }


  private fun projectWithConfig(config: DeployConfig): SProject {
    val features = listOf(feature(config.toMap()))
    return mock {
      on { getAvailableFeaturesOfType(featureType) } doReturn features
    }
  }

  private fun createTab(): DeployProjectTab {
    // Required to avoid NPE on creation
    val pagePlaces = mock<PagePlaces> {
      on { getPlaceById(any()) } doReturn mock { }
    }
    return DeployProjectTab(pagePlaces, mock {  })
  }

  private fun feature(params: Map<String, String>): SProjectFeatureDescriptor {
    return mock {
      on { type }.doReturn(featureType)
      on { parameters }.doReturn(params)
    }
  }

  companion object {
    const val featureType = "deployment-dashboard-config"
  }

}
