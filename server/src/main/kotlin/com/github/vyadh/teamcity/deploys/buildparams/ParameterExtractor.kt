package com.github.vyadh.teamcity.deploys.buildparams

import jetbrains.buildServer.serverSide.SBuild

interface ParameterExtractor {

  fun extract(build: SBuild, key: String, default: () -> String?): String?

}
