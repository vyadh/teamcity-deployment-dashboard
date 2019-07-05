package com.github.vyadh.teamcity.deploys.buildfinder

import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SBuildType
import java.util.stream.Stream

interface BuildFinder {

  fun find(type: SBuildType): Stream<SBuild>

}
