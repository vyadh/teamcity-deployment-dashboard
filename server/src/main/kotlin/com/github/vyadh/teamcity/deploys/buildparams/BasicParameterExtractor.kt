package com.github.vyadh.teamcity.deploys.buildparams

import jetbrains.buildServer.serverSide.SBuild

open class BasicParameterExtractor : ParameterExtractor {

  /**
   * If the parameter key is specified (non-blank) then get the required param,
   * otherwise use the default.
   */
  override fun extract(build: SBuild, key: String, default: () -> String?): String? {
    return if (key.isBlank())
      default()
    else {
      // Resolves parameters in the finished build
      val value = build.parametersProvider[key]
      // Resolves parameters in a running build
      if (value == null) null else build.valueResolver.resolve(value).result
    }
  }

}
