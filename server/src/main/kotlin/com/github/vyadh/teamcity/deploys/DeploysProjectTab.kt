package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.project.ProjectTab
import javax.servlet.http.HttpServletRequest

class DeploysProjectTab(pagePlaces: PagePlaces, projectManager: ProjectManager) :
        ProjectTab(
              "deployment-dashboard",
              "Deployments",
              pagePlaces,
              projectManager,
              "deploys-project-tab.jsp") {

  override fun isAvailable(request: HttpServletRequest): Boolean {
    val project = getProject(request)
    // todo at least one deployment type config
    return project != null && super.isAvailable(request)
  }

  override fun fillModel(
        model: MutableMap<String, Any>,
        request: HttpServletRequest,
        project: SProject,
        user: SUser?) {
  }

}
