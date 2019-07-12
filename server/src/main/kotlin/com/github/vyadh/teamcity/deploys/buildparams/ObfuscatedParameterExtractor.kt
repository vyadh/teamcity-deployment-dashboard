package com.github.vyadh.teamcity.deploys.buildparams

import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.parameters.types.PasswordsSearcher

class ObfuscatedParameterExtractor(private val passwordsSearcher: PasswordsSearcher) :
      BasicParameterExtractor() {

  companion object {
    const val obfuscated = "******"
  }

  override fun extract(build: SBuild, key: String, default: () -> String?): String? {
    val value = super.extract(build, key, default)
    val passwords = passwordsSearcher.collectPasswords(build)
    return obfuscate(value, passwords)
  }

  private fun obfuscate(value: String?, passwords: Set<String>): String? {
    if (value == null) return null

    var current: String = value
    for (password in passwords) {
      current = current.replace(password, obfuscated)
    }
    return current
  }

}
