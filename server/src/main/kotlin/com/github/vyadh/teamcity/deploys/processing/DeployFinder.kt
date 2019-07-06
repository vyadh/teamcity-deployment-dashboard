package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.Deploy
import com.github.vyadh.teamcity.deploys.buildfinder.BuildFinder
import jetbrains.buildServer.serverSide.*
import java.util.stream.Stream

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
    val deploys = project.buildTypes.stream()
          .filter { isDeployment(it) }
          .flatMap { toDeploys(it) }

    return DeployDuplicateResolver.resolve(deploys)
  }

  private fun isDeployment(type: SBuildType?): Boolean {
    return type != null &&
          type.getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) == "DEPLOYMENT"
  }

  internal fun toDeploys(type: SBuildType): Stream<Deploy> {
    return buildFinder.find(type).flatMap { toDeploy(it) }
  }

  internal fun toDeploy(build: SBuild): Stream<Deploy> {
    val projectName = DeployExtractor.projectName(build, projectKey) ?:
      return Stream.of()

    val deploy = Deploy(
          projectName,
          DeployExtractor.version(build, versionKey),
          DeployExtractor.environmentName(build, environmentKey),
          DeployExtractor.timeOf(build),
          DeployExtractor.toStatus(build),
          links.getViewResultsUrl(build)
    )

    return Stream.of(deploy)
  }

}
