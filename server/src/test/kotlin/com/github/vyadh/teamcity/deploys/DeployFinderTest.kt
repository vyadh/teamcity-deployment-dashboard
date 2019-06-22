package com.github.vyadh.teamcity.deploys

import com.github.vyadh.teamcity.deploys.buildfinder.FoundBuildFinder
import com.github.vyadh.teamcity.deploys.buildfinder.MissingBuildFinder
import com.nhaarman.mockitokotlin2.*
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

  @Test
  fun searchWithNoBuildTypes() {
    val project = project(types = emptyList())

    val result = finder().search(project)

    assertThat(result).isEmpty()
  }

  @Test
  fun searchWithNoDeploymentTypes() {
    val project = project(listOf(regularBuildType()))

    val result = finder().search(project)

    assertThat(result).isEmpty()
  }

  @Test
  fun searchWithRunningBuild() {
    val build = mock<SRunningBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Ruminous"), Pair(envKey, "UAT"))
      on { buildNumber } doReturn "1.0"
      on { buildStatus } doReturn Status.NORMAL
      on { startDate } doReturn Date()
    }
    val project = project(listOf(buildTypeWith(build)))

    val result = finder().search(project).first()

    assertThat(result.project).isEqualTo("Ruminous")
    assertThat(result.environment).isEqualTo("UAT")
  }

  @Test
  fun searchWithFinishedBuild() {
    val project = project(listOf(deploymentBuildType()))
    val build = mock<SFinishedBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Frustrum"), Pair(envKey, "PRD"))
      on { buildNumber } doReturn "1.0"
      on { buildStatus } doReturn Status.NORMAL
      on { finishDate } doReturn Date()
    }
    val finder = finder(build)

    val result = finder.search(project).first()

    assertThat(result.project).isEqualTo("Frustrum")
    assertThat(result.environment).isEqualTo("PRD")
  }

  @Test
  fun searchWithMultipleBuilds() {
    val buildFinished = mock<SFinishedBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Ruminous"), Pair(envKey, "PRD"))
      on { buildNumber } doReturn "1.0.0"
      on { buildStatus } doReturn Status.NORMAL
      on { finishDate } doReturn Date()
    }
    val buildRunning = mock<SRunningBuild> {
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Frustrum"), Pair(envKey, "DEV"))
      on { buildNumber } doReturn "1.0.0"
      on { buildStatus } doReturn Status.NORMAL
      on { startDate } doReturn Date()
    }
    val finder = finder(buildFinished)
    val project = project(listOf(
          buildTypeWith(buildRunning),
          deploymentBuildType(),
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
      on { buildStatus } doReturn Status.NORMAL
      on { startDate } doReturn Date.from(started.toInstant())
      on { finishDate } doReturn Date.from(finished.toInstant())
    }
    val deployLinks = links("http://host/build/1")
    val deployFinder = DeployFinder(deployLinks, projectKey, envKey, MissingBuildFinder())

    val result = deployFinder.toDeploy(build)

    assertThat(result?.project).isEqualTo("Dash")
    assertThat(result?.environment).isEqualTo("DEV")
    assertThat(result?.version).isEqualTo("1.1.0")
    assertThat(result?.time).isEqualTo(finished)
    assertThat(result?.status).isEqualTo("SUCCESS")
    assertThat(result?.link).isEqualTo("http://host/build/1")
  }

  @Test
  internal fun toDeployWhenRunning() {
    val started = ZonedDateTime.parse("2019-05-19T16:54:30+01:00")
    val build = mock<SRunningBuild> { // Signifies running
      on { buildOwnParameters } doReturn mapOf(Pair(projectKey, "Dash"), Pair(envKey, "DEV"))
      on { buildNumber } doReturn "1.1.0"
      on { buildStatus } doReturn Status.NORMAL
      on { startDate } doReturn Date.from(started.toInstant())
    }
    val deployLinks = links("http://host/build/2")
    val deployFinder = DeployFinder(deployLinks, projectKey, envKey, MissingBuildFinder())

    val result = deployFinder.toDeploy(build)

    assertThat(result?.project).isEqualTo("Dash")
    assertThat(result?.environment).isEqualTo("DEV")
    assertThat(result?.version).isEqualTo("1.1.0")
    assertThat(result?.time).isEqualTo(started)
    assertThat(result?.status).isEqualTo("RUNNING")
    assertThat(result?.link).isEqualTo("http://host/build/2")
  }

  @Test
  internal fun toDeployWhenProjectParameterNameBlank() {
    val build = buildWith("Project", "Build", emptyMap())
    val finder = DeployFinder(links, "", envKey, MissingBuildFinder())

    val result = finder.toDeploy(build)

    assertThat(result?.project).isEqualTo("Project")
  }

  @Test
  internal fun toDeployWhenEnvironmentParameterNameBlank() {
    val build = buildWith("Project", "Build",
          mapOf(Pair(projectKey, "Project Alt")))
    val finder = DeployFinder(links, projectKey, "", MissingBuildFinder())

    val result = finder.toDeploy(build)

    assertThat(result?.environment).isEqualTo("Build")
  }

  @Test
  internal fun toDeployReturnsNullWhenProjectParameterNotFound() {
    val build = buildWith("Project", "Build", emptyMap())

    val result = finder().toDeploy(build)

    assertThat(result?.project).isNull()
  }

  @Test
  internal fun toDeployShowsAsMissingWhenEnvironmentParameterNotFound() {
    val build = buildWith("Project", "Build",
          mapOf(Pair(projectKey, "Ruminous")))

    val result = finder().toDeploy(build)

    assertThat(result?.environment).isEqualTo("[missing]")
  }


  private fun links(link: String): WebLinks = mock {
    on { getViewResultsUrl(any()) } doReturn link
  }

  private fun finder(build: SFinishedBuild? = null): DeployFinder {
    val lastBuilds = if (build == null) MissingBuildFinder() else FoundBuildFinder(build)
    return DeployFinder(links, projectKey, envKey, lastBuilds)
  }

  private fun project(types: List<SBuildType>): SProject = mock {
    on { buildTypes } doReturn types
  }

  private fun regularBuildType(): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "REGULAR"
  }

  private fun deploymentBuildType(): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn emptyList()
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
      on { buildStatus } doReturn Status.NORMAL
      on { finishDate } doReturn Date()
    }
  }

}
