package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.buildparams.BasicParameterExtractor
import jetbrains.buildServer.RunningBuild
import jetbrains.buildServer.messages.Status
import jetbrains.buildServer.serverSide.SBuild
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 * Responsible for extracting the required data out of TeamCity build objects.
 */
object DeployExtractor {

  private const val missing = "[missing]"
  private val params = BasicParameterExtractor()

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

  //todo indicate hanging or personal build
  /**
   * Values: SUCCESS, WARNING, FAILURE, ERROR, UNKNOWN
   * @see jetbrains.buildServer.messages.Status
   */
  internal fun toStatus(build: SBuild): String {
    val running = build is RunningBuild
    val status = build.buildStatus

    return when {
      running && isFailing(status) -> "FAILING"
      running && isSuccess(status) -> "RUNNING"
      isFailing(status) -> "FAILURE"
      isSuccess(status) -> "SUCCESS"
      else -> "UNKNOWN"
    }
  }

  private fun isFailing(status: Status): Boolean {
    return when (status) {
      Status.ERROR -> true
      Status.FAILURE -> true
      else -> false
    }
  }

  private fun isSuccess(status: Status): Boolean =
        status == Status.NORMAL

}
