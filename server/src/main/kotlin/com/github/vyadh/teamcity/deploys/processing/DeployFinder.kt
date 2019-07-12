package com.github.vyadh.teamcity.deploys.processing

import com.github.vyadh.teamcity.deploys.Deploy
import com.github.vyadh.teamcity.deploys.buildfinder.BuildFinder
import jetbrains.buildServer.serverSide.*
import java.util.stream.Stream

/**
 * Queries deployment information from the project hierarchy based on the supplied
 * configuration.
 */
class DeployFinder(
      private val links: WebLinks,
      private val projectKey: String,
      private val versionKey: String,
      private val environment: DeployEnvironment,
      private val buildFinder: BuildFinder,
      private val converter: BuildAttributeConverter) {

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
    val projectName = converter.projectName(build, projectKey) ?:
      return Stream.of()

    val deploy = Deploy(
          projectName,
          converter.version(build, versionKey),
          environment.name(build),
          converter.timeOf(build),
          converter.toStatus(build),
          links.getViewResultsUrl(build)
    )

    return Stream.of(deploy)
  }

}
