package com.github.vyadh.teamcity.deploys

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import jetbrains.buildServer.serverSide.*
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class DeploysDataControllerTest {

  private val webManager = mock<WebControllerManager> { }
  private val links = links("http://host/builds/id/123")

  @Test
  internal fun doHandleRespondsAsJson() {
    val projectManager = mock<ProjectManager> { }
    val controller = DeploysDataController(webManager, projectManager, pluginDescriptor(), links)
    val response = mock<HttpServletResponse> { }

    controller.doHandle(anyRequest(), response)

    verify(response).contentType = "application/json"
  }

  @Test
  internal fun doHandleReturnsEmptyDeploysWhenProjectNotFound() {
    val projectManager = projectManagerReturning(null)
    val controller = DeploysDataController(webManager, projectManager, pluginDescriptor(), links)

    val result = controller.doHandle(anyRequest(), anyResponse())

    val deploys = modelMap(result)["deploys"] ?: error("no deploys entry found")
    assertThat(deploys).isEmpty()
  }

  @Test
  internal fun doHandlePopulatesDeploys() {
    val project = projectWith(buildTypeWith(buildNamed("SomeProject")))
    val projectManager = projectManagerReturning(project)
    val controller = DeploysDataController(webManager, projectManager, pluginDescriptor(), links)

    val result = controller.doHandle(anyRequest(), anyResponse())

    val deploy = modelMap(result)["deploys"]?.first()!!
    assertThat(deploy.project).isEqualTo("SomeProject")
  }

  @Test
  internal fun projectIdIsDerivedFromURL() {
    val request = requestWithURI("http://host/app/deploys/SomeProjectId")

    val result = DeploysDataController.projectId(request)

    assertThat(result).isEqualTo("SomeProjectId")
  }

  @Test
  internal fun projectIdIsEmptyWhenLastCharacterIsSlash() {
    val request = requestWithURI("http://host/app/deploys/")

    val result = DeploysDataController.projectId(request)

    assertThat(result).isEqualTo("")
  }


  private fun pluginDescriptor(): PluginDescriptor = mock {
    on { getPluginResourcesPath(anyString()) } doReturn "path"
  }

  private fun anyRequest() = requestWithURI("http://host/app/deploys/id")

  private fun anyResponse() = mock<HttpServletResponse> { }

  private fun requestWithURI(uri: String) = mock<HttpServletRequest> {
    on { requestURI } doReturn uri
  }

  @Suppress("SameParameterValue")
  private fun links(link: String): WebLinks = mock {
    on { getViewResultsUrl(any()) } doReturn link
  }

  private fun projectManagerReturning(project: SProject?): ProjectManager = mock {
    on { findProjectByExternalId(anyString()) } doReturn project
  }

  private fun projectWith(buildType: SBuildType): SProject = mock {
    on { buildTypes } doReturn listOf(buildType)
  }

  private fun buildTypeWith(build: SFinishedBuild): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn emptyList()
    on { lastChangesFinished } doReturn build
  }

  @Suppress("SameParameterValue")
  private fun buildNamed(name: String): SFinishedBuild = mock {
    on { buildOwnParameters } doReturn mapOf(Pair("PROJECT", name), Pair("ENVIRONMENT", "PRD"))
    on { buildNumber } doReturn "1.0"
    on { finishDate } doReturn Date()
  }

  @Suppress("UNCHECKED_CAST")
  private fun modelMap(result: ModelAndView?) = result?.model as Map<String, List<Deploy>>

}
