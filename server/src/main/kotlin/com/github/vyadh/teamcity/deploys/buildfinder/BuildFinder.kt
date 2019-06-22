package com.github.vyadh.teamcity.deploys.buildfinder

import jetbrains.buildServer.serverSide.SBuildType
import jetbrains.buildServer.serverSide.SFinishedBuild

interface BuildFinder {

  fun find(type: SBuildType): SFinishedBuild?

}
