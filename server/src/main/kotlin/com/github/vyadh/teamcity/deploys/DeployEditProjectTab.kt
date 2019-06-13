package com.github.vyadh.teamcity.deploys

import jetbrains.buildServer.controllers.admin.projects.EditProjectTab
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PluginDescriptor
import javax.servlet.http.HttpServletRequest

class DeployEditProjectTab(pagePlaces: PagePlaces, pluginDescriptor: PluginDescriptor) :
      EditProjectTab(
            pagePlaces,
            DeployPlugin.id,
            pluginDescriptor.getPluginResourcesPath(jspPath),
            DeployPlugin.title) {

  companion object {
    const val jspPath = "deploys-edit-project-tab.jsp"
  }

  private val configStore = DeployConfigStore()

  override fun fillModel(model: MutableMap<String, Any>, request: HttpServletRequest) {
    super.fillModel(model, request)

    val project = getProject(request) ?: return
    model[DeployConfigKeys.projectExternalId] = project.externalId

    val config = configStore.find(project)
    if (config.isEnabled()) {
      model.putAll(config.toMap())
    }
  }

}
