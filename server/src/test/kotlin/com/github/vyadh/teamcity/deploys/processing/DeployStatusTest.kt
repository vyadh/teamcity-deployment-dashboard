package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.buildfinder.LastBuildFinder
import com.github.vyadh.teamcity.deploys.buildfinder.SimulatedBuildHistory
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.parametersProvider
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.valueResolver
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import jetbrains.buildServer.messages.Status
import jetbrains.buildServer.serverSide.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

internal class DeployStatusTest {

  private val links = links("http://link")
  private val projectKey = "PROJECT"
  private val versionKey = "VERSION"
  private val envKey = "ENV"
  private val converter = BuildAttributeConverter()
  private val finder = DeployFinder(
        links, projectKey, versionKey, "", DeployEnvironment(envKey, listOf("ENV"), converter),
        LastBuildFinder(SimulatedBuildHistory.empty()), converter)

  @Test
  internal fun statusOfBuildWhenNormal() {
    val build = mock<SFinishedBuild> {
      on { buildStatus } doReturn Status.NORMAL
    }

    val result = converter.toStatus(build)

    assertThat(result).isEqualTo("SUCCESS")
  }

  @Test
  internal fun statusOfBuildWhenFailure() {
    val build = mock<SFinishedBuild> {
      on { buildStatus } doReturn Status.FAILURE
    }

    val result = converter.toStatus(build)

    assertThat(result).isEqualTo("FAILURE")
  }

  @Test
  internal fun statusOfBuildWhenUnknown() {
    val build = mock<SFinishedBuild> {
      on { buildStatus } doReturn Status.UNKNOWN
    }

    val result = converter.toStatus(build)

    assertThat(result).isEqualTo("UNKNOWN")
  }

  @Test
  internal fun statusOfBuildWhenRunning() {
    val buildType = buildTypeWith(runningBuild())

    val result = finder.toDeploys(buildType).findAny().get().running

    assertThat(result).isEqualTo(true)
  }


  @Suppress("SameParameterValue")
  private fun links(link: String): WebLinks = mock {
    on { getViewResultsUrl(any()) } doReturn link
  }

  private fun buildTypeWith(build: SRunningBuild): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn listOf(build)
  }

  private fun runningBuild(): SRunningBuild {
    val mockValueResolver = valueResolver()

    return mock {
      on { buildStatus } doReturn Status.NORMAL
      on { startDate } doReturn Date()
      on { parametersProvider } doReturn parametersProvider(params())
      on { valueResolver } doReturn mockValueResolver
    }
  }

  private fun params() = mapOf(
        Pair(projectKey, "Project"),
        Pair(versionKey, "1.0"),
        Pair(envKey, "ENV"))

}
