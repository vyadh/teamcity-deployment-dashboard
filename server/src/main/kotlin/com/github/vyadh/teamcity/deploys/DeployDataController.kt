package com.github.vyadh.teamcity.deploys

import com.github.vyadh.teamcity.deploys.buildfinder.BuildFinder
import com.github.vyadh.teamcity.deploys.buildfinder.LastBuildFinder
import com.github.vyadh.teamcity.deploys.buildfinder.MultiBuildFinder
import com.github.vyadh.teamcity.deploys.buildparams.ObfuscatedParameterExtractor
import com.github.vyadh.teamcity.deploys.processing.BuildAttributeConverter
import com.github.vyadh.teamcity.deploys.processing.DeployEnvironment
import com.github.vyadh.teamcity.deploys.processing.DeployFinder
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.BuildHistory
import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.serverSide.WebLinks
import jetbrains.buildServer.serverSide.parameters.types.PasswordsSearcher
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Controller to query data needed for the React frontend as a JSON payload.
 * Looks up deployment builds based on configuration and the specified project
 * (specified by the frontend as part of the URL).
 */
class DeployDataController(
      private val projectManager: ProjectManager,
      private val pluginDescriptor: PluginDescriptor,
      private val links: WebLinks,
      private val buildHistory: BuildHistory,
      passwordsSearcher: PasswordsSearcher,
      webManager: WebControllerManager
) : BaseController() {

  private val configStore = DeployConfigStore()
  private val converter = BuildAttributeConverter(ObfuscatedParameterExtractor(passwordsSearcher))
  private val appPath = "/app/deployment-dashboard/**"
  private val jspPath = "deploys-project-data.jsp"

  init {
    webManager.registerController(appPath, this)
  }

  public override fun doHandle(
        request: HttpServletRequest,
        response: HttpServletResponse): ModelAndView? {

    response.setContentType("application/json")

    val path = pluginDescriptor.getPluginResourcesPath(jspPath)
    val project = project(request) ?: return ModelAndView(path, emptyModel())

    val config = configStore.findAvailable(project)
    if (!config.isEnabled()) return ModelAndView(path, emptyModel())

    val deploys = createDeployFinder(config).search(project)
    val model = populatedModel(deploys, config)

    return ModelAndView(path, model)
  }

  private fun project(request: HttpServletRequest): SProject? {
    val id = projectId(request)
    return projectManager.findProjectByExternalId(id)
  }

  private fun emptyModel(): Map<String, Any> {
    return mapOf(
          Pair("environments", emptyList<String>()),
          Pair("refreshSecs", ""),
          Pair("deploys", emptyList<String>())
    )
  }

  private fun populatedModel(deploys: List<Deploy>, config: DeployConfig): Map<String, Any> {
    return mapOf(
          Pair("environments", config.environmentsAsList()),
          Pair("refreshSecs", config.refreshSecs),
          Pair("deploys", deploys)
    )
  }

  private fun createDeployFinder(config: DeployConfig): DeployFinder {
    val environment = DeployEnvironment(
        config.environmentKey,
        config.environmentsAsList(),
        converter
    )

    return DeployFinder(
          links,
          config.projectKey,
          config.versionKey,
          config.customKey,
          environment,
          createBuildFinder(config, environment),
          converter
    )
  }

  private fun createBuildFinder(config: DeployConfig, environment: DeployEnvironment): BuildFinder {
    return if (config.isMultiEnvConfig())
      MultiBuildFinder(buildHistory, environment)
    else
      LastBuildFinder(buildHistory)
  }

  companion object {
    internal fun projectId(request: HttpServletRequest): String {
      val uri = request.requestURI
      return uri.substring(uri.lastIndexOf('/') + 1)
    }
  }

}
