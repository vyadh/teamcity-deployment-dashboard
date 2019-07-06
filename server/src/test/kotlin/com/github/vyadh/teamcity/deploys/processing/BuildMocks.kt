package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.buildfinder.BuildFinder
import com.github.vyadh.teamcity.deploys.buildfinder.LastBuildFinder
import com.github.vyadh.teamcity.deploys.buildfinder.SimulatedBuildHistory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import jetbrains.buildServer.messages.Status
import jetbrains.buildServer.serverSide.*
import java.time.Instant
import java.util.*

object BuildMocks {

  internal fun links(link: String): WebLinks = mock {
    on { getViewResultsUrl(any()) } doReturn link
  }

  internal fun lastBuild(build: SFinishedBuild): BuildFinder =
        LastBuildFinder(SimulatedBuildHistory(build))

  internal fun project(types: List<SBuildType>): SProject = mock {
    on { buildTypes } doReturn types
  }

  internal fun regularBuildType(): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "REGULAR"
  }

  internal fun deploymentBuildType(): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn emptyList()
  }

  internal fun buildTypeWith(build: SRunningBuild): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn listOf(build)
  }

  internal fun buildTypeWith(builds: List<SRunningBuild>): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn builds
  }

  internal fun buildTypeWith(build: String, project: String): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { name } doReturn build
    on { internalId } doReturn "internal-id"
    on { projectName } doReturn project
  }

  internal fun buildWith(type: SBuildType, buildNum: String, params: Map<String, String>): SBuild {
    return mock {
      on { buildOwnParameters } doReturn params
      on { buildType } doReturn type
      on { buildNumber } doReturn buildNum
      on { buildStatus } doReturn Status.NORMAL
      on { finishDate } doReturn Date()
    }
  }

  internal fun running(
        properties: Map<String, String> = emptyMap(),
        status: Status = Status.NORMAL,
        start: Date = Date.from(Instant.parse("2019-01-01T00:00:00.00Z"))
  ): SRunningBuild {

    return mock {
      on { buildOwnParameters } doReturn properties
      on { buildStatus } doReturn status
      on { startDate } doReturn start
    }
  }

  internal fun finished(
        properties: Map<String, String> = emptyMap(),
        status: Status = Status.NORMAL,
        finish: Date = Date.from(Instant.parse("2019-01-01T00:00:00.00Z"))
  ): SFinishedBuild {

    return mock {
      on { buildOwnParameters } doReturn properties
      on { buildStatus } doReturn status
      on { finishDate } doReturn finish
    }
  }

}
