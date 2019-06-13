package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.messages.Status
import jetbrains.buildServer.serverSide.*
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 * Queries deployment information from the project hierarchy based on the supplied
 * configuration.
 *
 * @param projectKey configuration property name to lookup the project name or blank
 * if the project name itself should be used.
 * @param environmentKey configuration property name to lookup the environment name
 * or blank if the build type name should be used.
 */
class DeployFinder(
      private val links: WebLinks,
      private val projectKey: String,
      private val environmentKey: String) {

  fun search(project: SProject): List<Deploy> {
    return project
          .buildTypes
          .filter { isDeployment(it) }
          .flatMap { toDeploy(it) }
  }

  private fun toDeploy(type: SBuildType): List<Deploy> {
    val runningBuilds = type.runningBuilds

    return if (runningBuilds.isEmpty()) {
      val build = type.lastChangesFinished
      return if (build == null) emptyList()
             else listOf(toDeploy(build, toStatus(build)))
    } else {
      listOf(toDeploy(runningBuilds[0], runningStatus))
    }
  }

  internal fun toDeploy(build: SBuild, status: String): Deploy {
    return Deploy(
          param(build, projectKey) { build.buildType?.projectName ?: unknown },
          build.buildNumber,
          param(build, environmentKey) { build.buildType?.name ?: unknown },
          timeOf(build),
          status,
          links.getViewResultsUrl(build)
    )
  }

  companion object {
    const val missing = "[missing]"
    const val unknown = "[unknown]"

    private fun isDeployment(type: SBuildType?): Boolean {
      return type != null &&
            type.getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) == "DEPLOYMENT"
    }

    private fun param(build: SBuild, key: String, default: () -> String): String {
      return if (key.isBlank()) default()
      else build.buildOwnParameters.getOrElse(key, { missing })
    }

    private fun timeOf(build: SBuild): ZonedDateTime {
      return toUTC(build.finishDate ?: build.startDate)
    }

    private fun toUTC(dateTime: Date): ZonedDateTime {
      return ZonedDateTime.ofInstant(dateTime.toInstant(), ZoneOffset.UTC)
    }

    /**
     * Values: SUCCESS, WARNING, FAILURE, ERROR, UNKNOWN
     * @see jetbrains.buildServer.messages.Status
     */
    internal fun toStatus(build: SFinishedBuild): String {
      val status = build.buildStatus ?: Status.UNKNOWN
      return status.text
    }

    /** We currently classify running as just another status. */
    const val runningStatus = "RUNNING"
  }

}
