package com.github.vyadh.teamcity.deploys.buildfinder

import jetbrains.buildServer.serverSide.SBuildType
import jetbrains.buildServer.serverSide.SFinishedBuild

class FoundBuildFinder(private val build: SFinishedBuild) : BuildFinder {

  override fun find(type: SBuildType): SFinishedBuild? {
    return build
  }

}
