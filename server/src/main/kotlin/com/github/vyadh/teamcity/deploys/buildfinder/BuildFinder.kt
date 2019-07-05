package com.github.vyadh.teamcity.deploys.buildfinder

import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SBuildType

interface BuildFinder {

  fun find(type: SBuildType): SBuild?

}
