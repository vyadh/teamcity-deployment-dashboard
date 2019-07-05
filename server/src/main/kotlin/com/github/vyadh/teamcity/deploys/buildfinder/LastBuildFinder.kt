package com.github.vyadh.teamcity.deploys.buildfinder

import jetbrains.buildServer.serverSide.BuildHistory
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SBuildType
import jetbrains.buildServer.serverSide.SFinishedBuild
import jetbrains.buildServer.util.ItemProcessor

class LastBuildFinder(private val history: BuildHistory) : BuildFinder {

  override fun find(type: SBuildType): SBuild? {
    return running(type) ?: finished(type)
  }

  private fun running(type: SBuildType) = type.runningBuilds.firstOrNull()

  private fun finished(type: SBuildType): SFinishedBuild? {
    var build: SFinishedBuild? = null
    val captureMostRecent = ItemProcessor { item: SFinishedBuild -> build = item; false }
    history.processEntries(
          type.internalId,
          null,
          true,
          false,
          false,
          captureMostRecent
    )
    return build
  }

}
