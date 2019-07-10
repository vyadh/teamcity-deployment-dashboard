package com.github.vyadh.teamcity.deploys.buildfinder

import com.github.vyadh.teamcity.deploys.processing.DeployEnvironment
import jetbrains.buildServer.serverSide.*
import jetbrains.buildServer.util.ItemProcessor
import java.util.*
import java.util.stream.Stream
import kotlin.collections.ArrayList

/**
 * Walks the TeamCity build history trying to find all builds for the configured
 * environments. This is necessary for situations where a single configuration
 * deploys to multiple environments. This is potentially rather slow.
 *
 * Unlike [LastBuildFinder] which leaves filtering to the client, this class filters
 * to the known environments as a best-effort way of avoiding reading the entire
 * build history once it has found the ones expected.
 *
 * Note that we're not concerned with duplicates as that is handled later in the
 * pipe by [com.github.vyadh.teamcity.deploys.processing.DeployDuplicateResolver].
 */
class MultiBuildFinder(
      private val history: BuildHistory,
      private val environment: DeployEnvironment
) : BuildFinder {

  companion object {
    /** Point at which to give up searching to problems with massive build histories */
    const val upperBound = 1000
  }

  override fun find(type: SBuildType): Stream<SBuild> {
    return Stream.concat(running(type), finished(type))
  }

  private fun running(type: SBuildType): Stream<SRunningBuild> {
    return type.runningBuilds.stream()
          .filter { environment.contains(it) }
  }

  private fun finished(type: SBuildType): Stream<SFinishedBuild> {
    val processor = MatchingItemProcessor(environment)

    history.processEntries(
          type.internalId,
          null,
          true,
          false,
          false,
          processor
    )

    return processor.builds.stream()
  }

  class MatchingItemProcessor(val environment: DeployEnvironment): ItemProcessor<SFinishedBuild> {
    val builds = ArrayList<SFinishedBuild>(100)

    private val leftToMatch = LinkedList(environment.list)
    private var countDown = upperBound

    override fun processItem(build: SFinishedBuild): Boolean {
      val env = environment.name(build)

      if (leftToMatch.contains(env)) {
        builds.add(build)
        leftToMatch.remove(env)
      }

      countDown--
      return leftToMatch.isNotEmpty() && countDown > 0
    }

  }

}
