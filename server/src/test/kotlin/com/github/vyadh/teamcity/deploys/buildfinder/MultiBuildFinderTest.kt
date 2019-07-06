package com.github.vyadh.teamcity.deploys.buildfinder

import com.github.vyadh.teamcity.deploys.processing.BuildMocks.buildTypeWith
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.deploymentBuildType
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.finished
import com.github.vyadh.teamcity.deploys.processing.BuildMocks.running
import jetbrains.buildServer.serverSide.SBuild
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.stream.Collectors.toList

internal class MultiBuildFinderTest {

  private val projectKey = "PROJECT"
  private val versionKey = "VERSION"
  private val envKey = "ENV"

  @Test
  internal fun noBuilds() {
    val type = deploymentBuildType()
    val finder = MultiBuildFinder(SimulatedBuildHistory.empty(), emptyList())

    val result = finder.find(type).collect(toList())

    assertThat(result).isEmpty()
  }

  @Test
  internal fun multipleRunningBuilds() {
    val type = buildTypeWith(listOf(
          running(properties("Vega", "DEV")),
          running(properties("Vega", "PRD"))
    ))
    val finder = MultiBuildFinder(SimulatedBuildHistory.empty(), listOf("DEV", "PRD"))

    val result = finder.find(type).map { info(it) }.collect(toList())
    
    assertThat(result).containsExactly(Pair("Vega", "DEV"), Pair("Vega", "PRD"))
  }

  @Test
  internal fun multipleFinishedBuilds() {
    val type = deploymentBuildType()
    val finder = MultiBuildFinder(SimulatedBuildHistory(
          finished(properties("Vega", "DEV")),
          finished(properties("Vega", "PRD"))
    ), listOf("DEV", "PRD"))

    val result = finder.find(type).map { info(it) }.collect(toList())

    assertThat(result).containsExactly(Pair("Vega", "DEV"), Pair("Vega", "PRD"))
  }

  @Test
  internal fun multipleBuilds() {
    val running = listOf(
          running(properties("Vega", "DEV")),
          running(properties("Vega", "TST"))
    )
    val finished = SimulatedBuildHistory(
          finished(properties("Vega", "UAT")),
          finished(properties("Vega", "PRD"))
    )
    val type = buildTypeWith(running)
    val finder = MultiBuildFinder(finished, listOf("DEV", "TST", "UAT", "PRD"))

    val result = finder.find(type).map { info(it) }.collect(toList())

    assertThat(result).containsExactly(
          Pair("Vega", "DEV"),
          Pair("Vega", "TST"),
          Pair("Vega", "UAT"),
          Pair("Vega", "PRD")
    )
  }

  @Test
  internal fun onlyBuildsForSpecifiedEnvironments() {
    val running = listOf(
          running(properties("Sol", "DEV")),
          running(properties("Sol", "Unknown"))
    )
    val finished = SimulatedBuildHistory(
          finished(properties("Sol", "UAT")),
          finished(properties("Sol", "Unknown"))
    )
    val type = buildTypeWith(running)
    val finder = MultiBuildFinder(finished, listOf("DEV", "UAT"))

    val result = finder.find(type).map { info(it) }.collect(toList())

    assertThat(result).containsExactly(
          Pair("Sol", "DEV"),
          Pair("Sol", "UAT")
    )
  }

  
  private fun properties(project: String, env: String): Map<String, String> {
    return mapOf(
          Pair(projectKey, project),
          Pair(versionKey, "1.0"),
          Pair(envKey, env)
    )
  }

  private fun info(build: SBuild): Pair<String, String> {
    return Pair(build.buildOwnParameters[projectKey]!!, build.buildOwnParameters[envKey]!!)
  }

}
