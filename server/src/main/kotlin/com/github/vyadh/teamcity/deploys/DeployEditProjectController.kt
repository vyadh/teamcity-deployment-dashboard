package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Responsible for saving the configuration
 */
class DeployEditProjectController(
      sBuildServer: SBuildServer,
      webControllerManager: WebControllerManager,
      private val projectManager: ProjectManager
) : BaseController(sBuildServer) {

  private val configStore = DeployConfigStore()
  private val controllerPath = "/admin/deploys-edit-project.html"

  init {
    webControllerManager.registerController(controllerPath, this)
  }

  override fun doHandle(
        request: HttpServletRequest,
        response: HttpServletResponse): ModelAndView? {

    val config = DeployConfig(
          dashboardEnabled = request.getParameter(DeployConfigKeys.dashboardEnabled),
          projectKey = request.getParameter(DeployConfigKeys.projectKey),
          versionKey = request.getParameter(DeployConfigKeys.versionKey),
          environmentKey = request.getParameter(DeployConfigKeys.environmentKey),
          environments = request.getParameter(DeployConfigKeys.environments),
          customKey = request.getParameter(DeployConfigKeys.customKey) ?: "",
          refreshSecs = request.getParameter(DeployConfigKeys.refreshSecs) ?: "",
          multiEnvConfig = request.getParameter(DeployConfigKeys.multiEnvConfig)
    )

    val projectExternalId = request.getParameter(DeployConfigKeys.projectExternalId)
    val project = projectManager.findProjectByExternalId(projectExternalId)

    if (project != null) {
      configStore.store(project, config)
    }

    return null
  }

}
