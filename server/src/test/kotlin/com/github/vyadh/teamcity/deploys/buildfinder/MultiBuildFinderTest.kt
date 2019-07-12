package com.github.vyadh.teamcity.deploys.buildfinder

import com.github.vyadh.teamcity.deploys.processing.BuildAttributeConverter
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.buildTypeWith
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.deploymentBuildType
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.finished
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.running
import com.github.vyadh.teamcity.deploys.processing.DeployEnvironment
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SFinishedBuild
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.stream.Collectors.toList
import java.util.stream.Stream

internal class MultiBuildFinderTest {

  private val projectKey = "PROJECT"
  private val versionKey = "VERSION"
  private val envKey = "ENV"
  private val converter = BuildAttributeConverter()

  @Test
  internal fun noBuilds() {
    val type = deploymentBuildType()
    val finder = MultiBuildFinder(
          SimulatedBuildHistory.empty(),
          DeployEnvironment(envKey, emptyList(), converter))

    val result = finder.find(type).collect(toList())

    assertThat(result).isEmpty()
  }

  @Test
  internal fun multipleRunningBuilds() {
    val type = buildTypeWith(listOf(
          running(properties("Vega", "DEV")),
          running(properties("Vega", "PRD"))
    ))
    val finder = MultiBuildFinder(
          SimulatedBuildHistory.empty(),
          DeployEnvironment(envKey, listOf("DEV", "PRD"), converter))

    val result = finder.find(type).map { info(it) }.collect(toList())
    
    assertThat(result).containsExactly(Pair("Vega", "DEV"), Pair("Vega", "PRD"))
  }

  @Test
  internal fun multipleFinishedBuilds() {
    val type = deploymentBuildType()
    val environment = DeployEnvironment(envKey, listOf("DEV", "PRD"), converter)
    val finder = MultiBuildFinder(SimulatedBuildHistory(
          finished(properties("Vega", "DEV")),
          finished(properties("Vega", "PRD"))
    ), environment)

    val result = finder.find(type).map { info(it) }.collect(toList())

    assertThat(result).containsExactly(Pair("Vega", "DEV"), Pair("Vega", "PRD"))
  }

  @Test
  internal fun multipleBuildsWithMixedCaseIncluded() {
    val running = listOf(
          running(properties("Vega", "Dev")),
          running(properties("Vega", "tst"))
    )
    val finished = SimulatedBuildHistory(
          finished(properties("Vega", "UAT")),
          finished(properties("Vega", "prd"))
    )
    val environment = DeployEnvironment(envKey, listOf("DEV", "TST", "UAT", "PRD"), converter)
    val type = buildTypeWith(running)
    val finder = MultiBuildFinder(finished, environment)

    val result = finder.find(type).map { info(it) }.collect(toList())

    assertThat(result).containsExactly(
          Pair("Vega", "Dev"),
          Pair("Vega", "tst"),
          Pair("Vega", "UAT"),
          Pair("Vega", "prd")
    )
  }

  @Test
  internal fun onlyBuildsForSpecifiedEnvironments() {
    val running = listOf(
          running(properties("Sol", "DEV")),
          running(properties("Sol", "UnknownRunning"))
    )
    val finished = SimulatedBuildHistory(
          finished(properties("Sol", "UAT")),
          finished(properties("Sol", "UnknownFinished"))
    )
    val environment = DeployEnvironment(envKey, listOf("DEV", "UAT"), converter)
    val type = buildTypeWith(running)
    val finder = MultiBuildFinder(finished, environment)

    val result = finder.find(type).map { info(it) }.collect(toList())

    assertThat(result).containsExactly(
          Pair("Sol", "DEV"),
          Pair("Sol", "UAT")
    )
  }

  @Test
  internal fun matchOnlyOneEnvironmentFromHistory() {
    val finished = SimulatedBuildHistory(
          finished(properties("Sol", "DEV", "1.0")),
          finished(properties("Sol", "UAT", "1.1")),
          finished(properties("Sol", "DEV", "2.0")),
          finished(properties("Sol", "UAT", "2.1"))
    )
    val environment = DeployEnvironment(envKey, listOf("DEV", "UAT"), converter)
    val type = deploymentBuildType()
    val finder = MultiBuildFinder(finished, environment)

    val result = finder.find(type).map {
      listOf(project(it), env(it), version(it))
    }.collect(toList())

    assertThat(result).containsExactly(
          listOf("Sol", "DEV", "1.0"),
          listOf("Sol", "UAT", "1.1")
    )
  }

  @Test
  internal fun doesNotBotherWithVeryOldHistory() {
    val buildsDev = Stream.iterate(1, { it + 1} )
          .map { i -> finished(properties("Vega", "DEV", i.toString()))}
          .limit(1000)
    val buildsUat = Stream.of(finished(properties("Vega", "UAT", "1rc")))
    val history = SimulatedBuildHistory(*toArray(Stream.concat(buildsDev, buildsUat)))
    val environment = DeployEnvironment(envKey, listOf("DEV", "UAT"), converter)
    val type = deploymentBuildType()
    val finder = MultiBuildFinder(history, environment)

    val result = finder.find(type).map {
      listOf(project(it), env(it), version(it))
    }.collect(toList())

    assertThat(result).containsExactly(
          listOf("Vega", "DEV", "1")
    )
  }

  private fun properties(project: String, env: String, version: String = "1.0"):
        Map<String, String> {

    return mapOf(
          Pair(projectKey, project),
          Pair(versionKey, version),
          Pair(envKey, env)
    )
  }

  private fun info(build: SBuild): Pair<String, String> {
    return Pair(project(build), env(build))
  }

  private fun project(build: SBuild) = build.buildOwnParameters[projectKey]!!
  private fun env(build: SBuild) = build.buildOwnParameters[envKey]!!
  private fun version(build: SBuild) = build.buildOwnParameters[versionKey]!!

  private fun toArray(stream: Stream<SFinishedBuild>): Array<SFinishedBuild> {
    return stream.toArray { size -> arrayOfNulls<SFinishedBuild>(size) }
  }

}
