package com.github.vyadh.teamcity.deploys

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import jetbrains.buildServer.messages.Status
import jetbrains.buildServer.serverSide.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.*

internal class DeployFinderTest {

  private val links = links("http://link")
  private val projectKey = "PROJECT"
  private val envKey = "ENV"
  private val finder = DeployFinder(links, projectKey, envKey)

  @Test
  fun searchWithNoBuildTypes() {
    val project = project(types = emptyList())

    val result = finder.search(project)

    assertThat(result).isEmpty()
  }

  @Test
  fun searchWithNoDeploymentTypes() {
    val project = project(listOf(regularBuildType()))

    val result = finder.search(project)

    assertThat(result).isEmpty()
  }

  @Test
  fun searchWithRunningBuild() {
    val build = mock<SRunningBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Ruminous"), Pair(envKey, "UAT"))
      on { buildNumber } doReturn "1.0"
      on { startDate } doReturn Date()
    }
    val project = project(listOf(buildTypeWith(build)))

    val result = finder.search(project).first()

    assertThat(result.project).isEqualTo("Ruminous")
    assertThat(result.environment).isEqualTo("UAT")
  }

  @Test
  fun searchWithFinishedBuild() {
    val build = mock<SFinishedBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Frustrum"), Pair(envKey, "PRD"))
      on { buildNumber } doReturn "1.0"
      on { finishDate } doReturn Date()
    }
    val project = project(listOf(buildTypeWith(build)))

    val result = finder.search(project).first()

    assertThat(result.project).isEqualTo("Frustrum")
    assertThat(result.environment).isEqualTo("PRD")
  }

  @Test
  fun searchWithMultipleBuilds() {
    val buildFinished = mock<SFinishedBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Ruminous"), Pair(envKey, "PRD"))
      on { buildNumber } doReturn "1.0.0"
      on { finishDate } doReturn Date()
    }
    val buildRunning = mock<SRunningBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Frustrum"), Pair(envKey, "DEV"))
      on { buildNumber } doReturn "1.0.0"
      on { startDate } doReturn Date()
    }
    val project = project(listOf(
          buildTypeWith(buildRunning),
          buildTypeWith(buildFinished),
          regularBuildType()
    ))

    val results = finder.search(project)

    assertThat(results.map { Pair(it.project, it.environment) })
          .containsExactlyInAnyOrder(Pair("Ruminous", "PRD"), Pair("Frustrum", "DEV"))
  }

  @Test
  internal fun toDeployWhenFinished() {
    val started = ZonedDateTime.parse("2019-05-19T16:54:30+01:00")
    val finished = started.plusMinutes(2)
    val build = mock<SBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Dash"), Pair(envKey, "DEV"))
      on { buildNumber } doReturn "1.1.0"
      on { startDate } doReturn Date.from(started.toInstant())
      on { finishDate } doReturn Date.from(finished.toInstant())
    }
    val deployLinks = links("http://host/build/1")
    val deployFinder = DeployFinder(deployLinks, projectKey, envKey)

    val result = deployFinder.toDeploy(build, "WARNING")

    assertThat(result.project).isEqualTo("Dash")
    assertThat(result.environment).isEqualTo("DEV")
    assertThat(result.version).isEqualTo("1.1.0")
    assertThat(result.time).isEqualTo(finished)
    assertThat(result.status).isEqualTo("WARNING")
    assertThat(result.link).isEqualTo("http://host/build/1")
  }

  @Test
  internal fun toDeployWhenRunning() {
    val started = ZonedDateTime.parse("2019-05-19T16:54:30+01:00")
    val build = mock<SBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Dash"), Pair(envKey, "DEV"))
      on { buildNumber } doReturn "1.1.0"
      on { startDate } doReturn Date.from(started.toInstant())
    }
    val deployLinks = links("http://host/build/2")
    val deployFinder = DeployFinder(deployLinks, projectKey, envKey)

    val result = deployFinder.toDeploy(build, "RUNNING")

    assertThat(result.project).isEqualTo("Dash")
    assertThat(result.environment).isEqualTo("DEV")
    assertThat(result.version).isEqualTo("1.1.0")
    assertThat(result.time).isEqualTo(started)
    assertThat(result.status).isEqualTo("RUNNING")
    assertThat(result.link).isEqualTo("http://host/build/2")
  }

  @Test
  internal fun toDeployWhenProjectParameterBlank() {
    val build = buildWith("Project", "Build", emptyMap())
    val finder = DeployFinder(links, "", envKey)

    val result = finder.toDeploy(build, "SUCCESS")

    assertThat(result.project).isEqualTo("Project")
  }

  @Test
  internal fun toDeployWhenEnvironmentParameterBlank() {
    val build = buildWith("Project", "Build", emptyMap())
    val finder = DeployFinder(links, projectKey, "")

    val result = finder.toDeploy(build, "SUCCESS")

    assertThat(result.environment).isEqualTo("Build")
  }

  @Test
  internal fun toDeployWhenProjectOrEnvironmentParameterNotFound() {
    val build = buildWith("Project", "Build", emptyMap())

    val result = finder.toDeploy(build, "SUCCESS")

    assertThat(result.project).isEqualTo("[missing]")
    assertThat(result.environment).isEqualTo("[missing]")
  }

  @Test
  internal fun toStatusOfBuild() {
    val build = mock<SFinishedBuild> {
      on { buildStatus } doReturn Status.NORMAL
    }

    val result = DeployFinder.toStatus(build)

    assertThat(result).isEqualTo("SUCCESS")
  }



  private fun links(link: String): WebLinks = mock {
    on { getViewResultsUrl(any()) } doReturn link
  }

  private fun project(types: List<SBuildType>): SProject = mock {
    on { buildTypes } doReturn types
  }

  private fun regularBuildType(): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "REGULAR"
  }

  private fun buildTypeWith(build: SFinishedBuild): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn emptyList()
    on { lastChangesFinished } doReturn build
  }

  private fun buildTypeWith(build: SRunningBuild): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn listOf(build)
  }

  private fun buildTypeWith(build: String, project: String): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { name } doReturn build
    on { projectName } doReturn project
  }

  private fun buildWith(project: String, build: String, params: Map<String, String>): SBuild {
    return buildWith(buildTypeWith(build, project), params)
  }

  private fun buildWith(type: SBuildType, params: Map<String, String>): SBuild {
    return mock {
      on { buildOwnParameters } doReturn params
      on { buildType } doReturn type
      on { buildNumber } doReturn "1.0"
      on { finishDate } doReturn Date()
    }
  }

}
