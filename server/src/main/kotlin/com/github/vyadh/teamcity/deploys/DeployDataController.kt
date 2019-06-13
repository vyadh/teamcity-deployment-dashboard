package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.serverSide.WebLinks
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class DeployDataController(
      private val projectManager: ProjectManager,
      private val pluginDescriptor: PluginDescriptor,
      private val links: WebLinks,
      webManager: WebControllerManager
) : BaseController() {

  private val configStore = DeployConfigStore()
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

    val config = configStore.find(project)
    if (!config.isEnabled()) return ModelAndView(path, emptyModel())

    val environments = config.environmentsAsList()
    val deploys = createFinder(config).search(project)
    val model = populatedModel(environments, deploys)

    return ModelAndView(path, model)
  }

  private fun project(request: HttpServletRequest): SProject? {
    val id = projectId(request)
    return projectManager.findProjectByExternalId(id)
  }

  private fun emptyModel(): Map<String, List<String>> {
    return mapOf(
          Pair("environments", emptyList()),
          Pair("deploys", emptyList())
    )
  }

  private fun populatedModel(environments: List<String>, deploys: List<Deploy>): Map<String, List<Any>> {
    return mapOf(
          Pair("environments", environments),
          Pair("deploys", deploys)
    )
  }

  private fun createFinder(config: DeployConfig): DeployFinder {
    return DeployFinder(links, config.projectKey, config.environmentKey)
  }

  companion object {
    internal fun projectId(request: HttpServletRequest): String {
      val uri = request.requestURI
      return uri.substring(uri.lastIndexOf('/') + 1)
    }
  }

}
