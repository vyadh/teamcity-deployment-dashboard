package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.buildparams.BasicParameterExtractor
import com.github.vyadh.teamcity.deploys.buildparams.ParameterExtractor
import jetbrains.buildServer.RunningBuild
import jetbrains.buildServer.messages.Status
import jetbrains.buildServer.serverSide.SBuild
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 * Responsible for extracting the required data out of TeamCity build objects.
 */
class BuildAttributeConverter(
      private val params: ParameterExtractor = BasicParameterExtractor()) {

  private val missing = "[missing]"

  fun projectName(build: SBuild, projectKey: String) =
        params.extract(build, projectKey) { build.buildType?.projectName }

  fun version(build: SBuild, versionKey: String) =
        params.extract(build, versionKey) { build.buildNumber } ?: missing

  fun environmentName(build: SBuild, environmentKey: String) =
        params.extract(build, environmentKey) { build.buildType?.name } ?: missing

  fun timeOf(build: SBuild): ZonedDateTime {
    return toUTC(build.finishDate ?: build.startDate)
  }

  private fun toUTC(dateTime: Date): ZonedDateTime {
    return ZonedDateTime.ofInstant(dateTime.toInstant(), ZoneOffset.UTC)
  }

  /**
   * Values: SUCCESS, WARNING, FAILURE, ERROR, UNKNOWN
   * @see jetbrains.buildServer.messages.Status
   */
  internal fun toStatus(build: SBuild): String {
    val status = build.buildStatus

    return when {
      isFailure(status) -> "FAILURE"
      isSuccess(status) -> "SUCCESS"
      else -> "UNKNOWN"
    }
  }

  /** Failure, or failing when running. */
  private fun isFailure(status: Status): Boolean {
    return when (status) {
      Status.ERROR -> true
      Status.FAILURE -> true
      else -> false
    }
  }

  private fun isSuccess(status: Status): Boolean = status == Status.NORMAL

  fun isRunning(build: SBuild): Boolean = build is RunningBuild

}
