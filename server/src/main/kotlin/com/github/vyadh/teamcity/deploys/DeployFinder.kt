package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.messages.Status
import jetbrains.buildServer.serverSide.*
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class DeployFinder(
      private val links: WebLinks,
      private val projectKey: String?,
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
      listOf(toDeploy(runningBuilds[0], "RUNNING"))
    }
  }

  internal fun toDeploy(build: SBuild, status: String): Deploy {
    return Deploy(
          param(build, projectKey) { build.buildType?.projectName ?: "n/a" },
          build.buildNumber,
          param(build, environmentKey) { "n/a" },
          timeOf(build),
          status,
          links.getViewResultsUrl(build)
    )
  }

  companion object {
    private fun isDeployment(type: SBuildType?): Boolean {
      return type != null &&
            type.getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) == "DEPLOYMENT"
    }

    private fun param(build: SBuild, key: String?, default: () -> String): String {
      return build.buildOwnParameters.getOrElse(key, default)
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
  }

}
