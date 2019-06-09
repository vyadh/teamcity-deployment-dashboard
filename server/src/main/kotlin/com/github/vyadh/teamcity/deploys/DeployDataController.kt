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
      webManager: WebControllerManager,
      links: WebLinks
) : BaseController() {

  private val store = DeployConfigStore()
  // todo from config
  private val finder = DeployFinder(links, "PROJECT", "ENVIRONMENT")

  init {
    webManager.registerController("/app/deploys/**", this)
  }

  public override fun doHandle(
        request: HttpServletRequest,
        response: HttpServletResponse): ModelAndView? {

    response.setContentType("application/json")

    val project = project(request)
    val deploys = findDeploys(project)
    val environments = findEnvironments(project)
    val path = pluginDescriptor.getPluginResourcesPath("deploys-project-data.jsp")

    val map = mapOf(
          Pair("environments", environments),
          Pair("deploys", deploys)
    )

    return ModelAndView(path, map)
  }

  private fun findEnvironments(project: SProject?): List<String> {
    if (project == null) return emptyList()
    val config = store.find(project) ?: return emptyList()
    return config.environmentsAsList()
  }

  private fun findDeploys(project: SProject?): List<Deploy> {
    return if (project == null) emptyList()
           else finder.search(project)
  }

  private fun project(request: HttpServletRequest): SProject? {
    val id = projectId(request)
    return projectManager.findProjectByExternalId(id)
  }

  companion object {
    internal fun projectId(request: HttpServletRequest): String {
      val uri = request.requestURI
      return uri.substring(uri.lastIndexOf('/') + 1)
    }
  }

}
