package com.github.vyadh.teamcity.deploys

import com.github.vyadh.teamcity.deploys.buildfinder.MissingBuildFinder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import jetbrains.buildServer.messages.Status
import jetbrains.buildServer.serverSide.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

internal class DeployFinderStatusTest {

  private val links = links("http://link")
  private val projectKey = "PROJECT"
  private val versionKey = "VERSION"
  private val envKey = "ENV"
  private val finder = DeployFinder(links, projectKey, versionKey, envKey, MissingBuildFinder())

  @Test
  internal fun statusOfBuildWhenNormal() {
    val build = mock<SFinishedBuild> {
      on { buildStatus } doReturn Status.NORMAL
    }

    val result = DeployFinder.toStatus(build)

    assertThat(result).isEqualTo("SUCCESS")
  }

  @Test
  internal fun statusOfBuildWhenFailure() {
    val build = mock<SFinishedBuild> {
      on { buildStatus } doReturn Status.FAILURE
    }

    val result = DeployFinder.toStatus(build)

    assertThat(result).isEqualTo("FAILURE")
  }

  @Test
  internal fun statusOfBuildWhenUnknown() {
    val build = mock<SFinishedBuild> {
      on { buildStatus } doReturn Status.UNKNOWN
    }

    val result = DeployFinder.toStatus(build)

    assertThat(result).isEqualTo("UNKNOWN")
  }

  @Test
  internal fun statusOfBuildWhenRunningNormal() {
    val buildType = buildTypeWith(runningBuildWithStatus(Status.NORMAL))

    val result = finder.toDeploys(buildType)[0].status

    assertThat(result).isEqualTo("RUNNING")
  }

  @Test
  internal fun statusOfBuildWhenRunningAndFailing() {
    val buildType = buildTypeWith(runningBuildWithStatus(Status.FAILURE))

    val result = finder.toDeploys(buildType)[0].status

    assertThat(result).isEqualTo("FAILING")
  }


  @Suppress("SameParameterValue")
  private fun links(link: String): WebLinks = mock {
    on { getViewResultsUrl(any()) } doReturn link
  }

  private fun buildTypeWith(build: SRunningBuild): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn listOf(build)
  }

  private fun runningBuildWithStatus(status: Status): SRunningBuild {
    return mock {
      on { buildStatus } doReturn status
      on { buildOwnParameters } doReturn params()
      on { startDate } doReturn Date()
    }
  }

  private fun params() = mapOf<String?, String>(
        Pair(projectKey, "Project"),
        Pair(versionKey, "1.0"),
        Pair(envKey, "ENV"))

}
