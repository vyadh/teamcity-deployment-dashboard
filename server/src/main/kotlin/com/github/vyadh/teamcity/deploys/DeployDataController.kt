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

  init {
    webManager.registerController("/app/deploys/**", this)
  }

  public override fun doHandle(
        request: HttpServletRequest,
        response: HttpServletResponse): ModelAndView? {

    response.setContentType("application/json")

    val path = pluginDescriptor.getPluginResourcesPath("deploys-project-data.jsp")
    val project = project(request) ?: return ModelAndView(path, emptyModel())

    val config = configStore.find(project)
    val environments = findEnvironments(config)
    val deploys = findDeploys(project, createFinder(config))
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

  private fun createFinder(config: DeployConfig?): DeployFinder? {
    return if (config == null) null
    else DeployFinder(links, config.projectKey, config.environmentKey)
  }

  private fun findEnvironments(config: DeployConfig?): List<String> {
    return config?.environmentsAsList() ?: emptyList()
  }

  private fun findDeploys(project: SProject, finder: DeployFinder?): List<Deploy> {
    return finder?.search(project) ?: emptyList()
  }

  companion object {
    internal fun projectId(request: HttpServletRequest): String {
      val uri = request.requestURI
      return uri.substring(uri.lastIndexOf('/') + 1)
    }
  }

}
