package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.buildfinder.BuildFinder
import com.github.vyadh.teamcity.deploys.buildfinder.LastBuildFinder
import com.github.vyadh.teamcity.deploys.buildfinder.SimulatedBuildHistory
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.buildTypeWith
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.buildWith
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.deploymentBuildType
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.lastBuild
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.project
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.regularBuildType
import com.nhaarman.mockitokotlin2.*
import jetbrains.buildServer.messages.Status
import jetbrains.buildServer.serverSide.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.*

internal class DeployFinderTest {

  private val links = BuildMocks.links("http://link")
  private val projectKey = "PROJECT"
  private val versionKey = "VERSION"
  private val envKey = "ENV"
  private val converter = BuildAttributeConverter()
  private val environment = DeployEnvironment(envKey, listOf("DEV", "UAT", "PRD"), converter)

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
    val buildFinder = lastBuild(build)
    val finder = finder(buildFinder = buildFinder)

    val result = finder.search(project).first()

    assertThat(result.project).isEqualTo("Frustrum")
    assertThat(result.environment).isEqualTo("PRD")
  }

  @Test
  fun searchWithMultipleBuildsOfDifferentCase() {
    val buildFinished = mock<SFinishedBuild> {
      on { buildOwnParameters } doReturn properties("Ruminous", "1.0", "prd")
      on { buildStatus } doReturn Status.NORMAL
      on { finishDate } doReturn Date()
    }
    val buildRunning = mock<SRunningBuild> {
      on { buildOwnParameters } doReturn properties("Frustrum", "1.0", "Dev")
      on { buildStatus } doReturn Status.NORMAL
      on { startDate } doReturn Date()
    }
    val finder = finder(buildFinder = lastBuild(buildFinished))
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
      on { buildOwnParameters } doReturn properties("Dash", "1.1.0", "DEV")
      on { buildStatus } doReturn Status.NORMAL
      on { startDate } doReturn Date.from(started.toInstant())
      on { finishDate } doReturn Date.from(finished.toInstant())
    }
    val deployLinks = BuildMocks.links("http://host/build/1")
    val deployFinder = finder(links = deployLinks)

    val result = deployFinder.toDeploy(build).findFirst().get()

    assertThat(result.project).isEqualTo("Dash")
    assertThat(result.environment).isEqualTo("DEV")
    assertThat(result.version).isEqualTo("1.1.0")
    assertThat(result.time).isEqualTo(finished)
    assertThat(result.status).isEqualTo("SUCCESS")
    assertThat(result.running).isEqualTo(false)
    assertThat(result.link).isEqualTo("http://host/build/1")
  }

  @Test
  internal fun toDeployWhenRunning() {
    val started = ZonedDateTime.parse("2019-05-19T16:54:30+01:00")
    val build = mock<SRunningBuild> { // Signifies running
      on { buildOwnParameters } doReturn properties("Dash", "1.1.0", "DEV")
      on { buildStatus } doReturn Status.NORMAL
      on { startDate } doReturn Date.from(started.toInstant())
    }
    val deployLinks = BuildMocks.links("http://host/build/2")
    val deployFinder = finder(links = deployLinks)

    val result = deployFinder.toDeploy(build).findFirst().get()

    assertThat(result.project).isEqualTo("Dash")
    assertThat(result.version).isEqualTo("1.1.0")
    assertThat(result.environment).isEqualTo("DEV")
    assertThat(result.time).isEqualTo(started)
    assertThat(result.status).isEqualTo("SUCCESS")
    assertThat(result.running).isEqualTo(true)
    assertThat(result.link).isEqualTo("http://host/build/2")
  }

  @Test
  internal fun toDeployWhenProjectKeyBlank() {
    val build = buildWith(project = "Project")
    val finder = finder(projectKey = "")

    val result = finder.toDeploy(build).findFirst().get()

    assertThat(result.project).isEqualTo("Project")
  }

  @Test
  internal fun toDeployWhenVersionKeyBlank() {
    val build = buildWith(buildNumber = "1.2.3")
    val finder = finder(versionKey = "")

    val result = finder.toDeploy(build).findFirst().get()

    assertThat(result.version).isEqualTo("1.2.3")
  }

  @Test
  internal fun toDeployWhenEnvironmentKeyBlank() {
    val build = buildWith(buildType = "Build")
    val finder = finder(environment = DeployEnvironment("", listOf("DEV"), converter))

    val result = finder.toDeploy(build).findFirst().get()

    assertThat(result.environment).isEqualTo("Build")
  }

  @Test
  internal fun toDeployReturnsNullWhenProjectParameterNotFound() {
    val build = buildWith(params = emptyMap())

    val result = finder(projectKey = projectKey).toDeploy(build).findFirst()

    assertThat(result).isEmpty
  }

  @Test
  internal fun toDeployShowsMissingWhenVersionParameterNotFound() {
    val build = buildWith(params = defaultParams())

    val result = finder(versionKey = versionKey).toDeploy(build).findFirst().get()

    assertThat(result.version).isEqualTo("[missing]")
  }

  @Test
  internal fun toDeployShowsAsMissingWhenEnvironmentParameterNotFound() {
    val build = buildWith(params = defaultParams())
    val env = DeployEnvironment(envKey, emptyList(), converter)

    val result = finder(environment = env).toDeploy(build).findFirst().get()

    assertThat(result.environment).isEqualTo("[missing]")
  }

  private fun finder(
        links: WebLinks = this.links,
        projectKey: String = this.projectKey,
        versionKey: String = this.versionKey,
        environment: DeployEnvironment = this.environment,
        buildFinder: BuildFinder = LastBuildFinder(SimulatedBuildHistory.empty())
  ): DeployFinder {

    return DeployFinder(links, projectKey, versionKey, environment, buildFinder, converter)
  }

  private fun buildWith(
        project: String = "ProjectName",
        buildType: String = "BuildTypeName",
        buildNumber: String = "#1",
        params: Map<String, String> = defaultParams()
  ): SBuild {

    return buildWith(buildTypeWith(buildType, project), buildNumber, params)
  }

  private fun defaultParams() =
        mapOf(Pair(projectKey, "DefaultProject"))

  private fun properties(
        project: String? = null, version: String? = null, env: String? = null
  ): Map<String, String> {

    val map = HashMap<String, String>()
    if (project != null) map[projectKey] = project
    if (version != null) map[versionKey] = version
    if (env != null) map[envKey] = env
    return map
  }

}
