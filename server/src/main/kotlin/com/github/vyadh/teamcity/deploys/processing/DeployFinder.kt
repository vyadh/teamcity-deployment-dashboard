package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.Deploy
import com.github.vyadh.teamcity.deploys.buildfinder.BuildFinder
import jetbrains.buildServer.RunningBuild
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
 * @param versionKey configuration property name to lookup the version number or blank
 * if the TeamCity build number should be used.
 * @param environmentKey configuration property name to lookup the environment name
 * or blank if the build type name should be used.
 */
class DeployFinder(
      private val links: WebLinks,
      private val projectKey: String,
      private val versionKey: String,
      private val environmentKey: String,
      private val buildFinder: BuildFinder) {

  fun search(project: SProject): List<Deploy> {
    return project
          .buildTypes
          .filter { isDeployment(it) }
          .flatMap { toDeploys(it) }
  }

  internal fun toDeploys(type: SBuildType): List<Deploy> {
    val deploy = toDeployOrNull(type)
    return if (deploy == null) emptyList() else listOf(deploy)
  }

  private fun toDeployOrNull(type: SBuildType): Deploy? {
    val build = type.runningBuilds.firstOrNull() ?: buildFinder.find(type)
    return if (build == null) null else toDeploy(build)
  }

  internal fun toDeploy(build: SBuild): Deploy? {
    val projectName = projectName(build) ?: return null

    return Deploy(
          projectName,
          version(build),
          environmentName(build),
          timeOf(build),
          toStatus(build),
          links.getViewResultsUrl(build)
    )
  }

  private fun projectName(build: SBuild) =
        param(build, projectKey) { build.buildType?.projectName }

  private fun version(build: SBuild) =
        param(build, versionKey) { build.buildNumber } ?: missing

  private fun environmentName(build: SBuild) =
        param(build, environmentKey) { build.buildType?.name } ?: missing

  companion object {
    const val missing = "[missing]"

    private fun isDeployment(type: SBuildType?): Boolean {
      return type != null &&
            type.getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) == "DEPLOYMENT"
    }

    private fun param(build: SBuild, key: String, default: () -> String?): String? {
      return if (key.isBlank()) default()
             else build.buildOwnParameters[key]
    }

    private fun timeOf(build: SBuild): ZonedDateTime {
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

}
