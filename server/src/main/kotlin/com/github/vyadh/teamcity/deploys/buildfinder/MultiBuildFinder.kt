package com.github.vyadh.teamcity.deploys.buildfinder

import jetbrains.buildServer.serverSide.BuildHistory
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SBuildType
import jetbrains.buildServer.serverSide.SFinishedBuild
import jetbrains.buildServer.util.ItemProcessor
import java.util.stream.Stream

/**
 * Walks the TeamCity build history trying to find all builds for the configured
 * environments. This is necessary for situations where a single configuration
 * deploys to multiple environments. This is potentially rather slow, especially
 * if not all environments are not found as it will search the entire history.
 *
 * Note that we're not concerned with duplicates as that is handled later in the
 * pipe by DeployDuplicateResolver.
 */
class MultiBuildFinder(
      private val history: BuildHistory,
      private val environments: List<String>
) : BuildFinder {

  /** Point at which to give up searching to problems with massive build histories */
  val upperBound = 1000

  override fun find(type: SBuildType): Stream<SBuild> {
    return Stream.concat(running(type), finished(type))
  }

  private fun running(type: SBuildType) = type.runningBuilds.stream()

  private fun finished(type: SBuildType): Stream<SFinishedBuild> {
    val builds = ArrayList<SFinishedBuild>(100)

    val captureMostRecent = ItemProcessor { item: SFinishedBuild ->
      builds.add(item)
      true
    }

    history.processEntries(
          type.internalId,
          null,
          true,
          false,
          false,
          captureMostRecent
    )
    return builds.stream()
  }

}
