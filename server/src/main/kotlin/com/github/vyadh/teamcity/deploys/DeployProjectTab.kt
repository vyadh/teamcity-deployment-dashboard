package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.project.ProjectTab
import javax.servlet.http.HttpServletRequest

/**
 * Deployments tab for project-level views to show actual dashboard.
 * Only responsibility is when the tab is shown and to hand off to the React frontend.
 */
class DeployProjectTab(pagePlaces: PagePlaces, projectManager: ProjectManager) :
        ProjectTab(DeployPlugin.id, title, pagePlaces, projectManager, jspPath) {

  companion object {
    const val jspPath = "deploys-project-tab.jsp"
    const val title = "Deployments"
  }

  private val configStore = DeployConfigStore()

  override fun isAvailable(request: HttpServletRequest): Boolean {
    return super.isAvailable(request) && isTabShowing(getProject(request))
  }

  internal fun isTabShowing(project: SProject?): Boolean {
    if (project == null) return false
    return configStore.findAvailable(project).isEnabled()
  }

  override fun fillModel(
        model: MutableMap<String, Any>,
        request: HttpServletRequest,
        project: SProject,
        user: SUser?) {

    // No need to supply data here as React frontend independently requests deploy data
  }

}
