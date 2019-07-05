package com.github.vyadh.teamcity.deploys.buildfinder

import jetbrains.buildServer.BuildType
import jetbrains.buildServer.serverSide.*
import jetbrains.buildServer.users.User
import jetbrains.buildServer.util.ItemProcessor
import java.util.*

class SimulatedBuildHistory(private vararg val builds: SFinishedBuild) : BuildHistory {

  companion object {
    fun empty() = SimulatedBuildHistory()
  }

  override fun processEntries(
        buildTypeId: String?,
        user: User?,
        includePersonalBuildsIfUserNotSpecified: Boolean,
        includeCanceled: Boolean,
        orderByChanges: Boolean,
        processor: ItemProcessor<SFinishedBuild>) {

    for (build in builds) {
      val more = processor.processItem(build)
      if (!more) return
    }
  }


  // Unimplemented

  override fun findEntry(buildId: Long): SFinishedBuild? {
    throw UnsupportedOperationException()
  }

  override fun findEntry(buildId: Long, addToCache: Boolean): SFinishedBuild? {
    throw UnsupportedOperationException()
  }

  override fun findEntry(buildTypeId: String, buildNumber: String): SFinishedBuild? {
    throw UnsupportedOperationException()
  }

  override fun getHistoryByAgent(agent: SBuildAgent, user: User?, includeCanceled: Boolean): MutableList<SFinishedBuild> {
    throw UnsupportedOperationException()
  }

  override fun getLastFinishedBuildDateOnAgent(agent: SBuildAgent, finishTimeAfter: Date): Date? {
    throw UnsupportedOperationException()
  }

  override fun removeEntry(finishedBuild: SFinishedBuild, comment: String?, cleanupLevel: CleanupLevel) {
    throw UnsupportedOperationException()
  }

  override fun removeEntry(finishedBuild: SFinishedBuild, comment: String?) {
    throw UnsupportedOperationException()
  }

  override fun removeEntry(finishedBuild: SFinishedBuild) {
    throw UnsupportedOperationException()
  }

  override fun removeEntry(buildId: Long): Boolean {
    throw UnsupportedOperationException()
  }

  override fun processEntries(itemProcessor: ItemProcessor<SFinishedBuild>) {
    throw UnsupportedOperationException()
  }

  override fun getEntriesSince(sinceBuildInclusive: SBuild?, buildType: BuildType): MutableList<SFinishedBuild> {
    throw UnsupportedOperationException()
  }

  override fun getEntries(includeCanceled: Boolean): MutableList<SFinishedBuild> {
    throw UnsupportedOperationException()
  }

  override fun findEntries(buildIds: MutableCollection<Long>): MutableCollection<SFinishedBuild> {
    throw UnsupportedOperationException()
  }

  override fun findEntries(buildIds: MutableCollection<Long>, addToCache: Boolean): MutableCollection<SFinishedBuild> {
    throw UnsupportedOperationException()
  }

  override fun findEntries(buildTypeId: String, buildNumber: String): MutableList<SFinishedBuild> {
    throw UnsupportedOperationException()
  }

  override fun getEntriesBefore(beforeThisBuild: SBuild, successfulOnly: Boolean): MutableList<SFinishedBuild> {
    throw UnsupportedOperationException()
  }

}
