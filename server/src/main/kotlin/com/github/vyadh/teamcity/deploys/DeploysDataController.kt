package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.serverSide.WebLinks
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class DeploysDataController(
      webManager: WebControllerManager,
      private val projectManager: ProjectManager,
      private val pluginDescriptor: PluginDescriptor,
      links: WebLinks
) : BaseController() {

  // todo from config
  private val finder = DeployFinder(links, "PROJECT", "ENVIRONMENT")

  init {
    webManager.registerController("/app/deploys/**", this)
  }

  public override fun doHandle(
        request: HttpServletRequest,
        response: HttpServletResponse): ModelAndView? {

    response.setContentType("application/json")

    val deploys = findDeploys(request)
    val path = pluginDescriptor.getPluginResourcesPath("deploys-project-data.jsp")
    val map = mapOf(Pair("deploys", deploys))

    return ModelAndView(path, map)
  }

  private fun findDeploys(request: HttpServletRequest): List<Deploy> {
    val id = projectId(request)
    val project = projectManager.findProjectByExternalId(id)
    return if (project == null) emptyList()
           else finder.search(project)
  }

  companion object {
    internal fun projectId(request: HttpServletRequest): String {
      val uri = request.requestURI
      return uri.substring(uri.lastIndexOf('/') + 1)
    }
  }

}
