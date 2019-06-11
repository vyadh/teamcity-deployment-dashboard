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

internal class DeployDataControllerTest {

  private val webManager = mock<WebControllerManager> { }
  private val links = links("http://host/builds/id/123")
  private val noEnvironments = ""

  @Test
  internal fun doHandleRespondsAsJson() {
    val projectManager = mock<ProjectManager> { }
    val controller = controllerWith(projectManager)
    val response = mock<HttpServletResponse> { }

    controller.doHandle(anyRequest(), response)

    verify(response).contentType = "application/json"
  }

  @Test
  internal fun doHandleReturnsEmptyCollectionsWhenProjectNotFound() {
    val projectManager = projectManagerReturning(null)
    val controller = controllerWith(projectManager)

    val result = controller.doHandle(anyRequest(), anyResponse())

    val environments = environments(result) ?: error("no environments entry found")
    assertThat(environments).isEmpty()

    val deploys = deploys(result) ?: error("no deploys entry found")
    assertThat(deploys).isEmpty()
  }

  @Test
  internal fun doHandlePopulatesDeploys() {
    val project = projectWith(buildTypeWith(build("SomeProject", "DEV")), noEnvironments)
    val projectManager = projectManagerReturning(project)
    val controller = controllerWith(projectManager)

    val result = controller.doHandle(anyRequest(), anyResponse())

    val deploy = deploys(result)?.first()!!
    assertThat(deploy.project).isEqualTo("SomeProject")
    assertThat(deploy.environment).isEqualTo("DEV")
  }

  @Test
  internal fun doHandlePopulatesEnvironments() {
    val project = projectWith(buildTypeWith(build("SomeProject", "ANY")), "DEV,PRD")
    val projectManager = projectManagerReturning(project)
    val controller = controllerWith(projectManager)

    val result = environments(controller.doHandle(anyRequest(), anyResponse()))

    assertThat(result).containsExactlyInAnyOrder("DEV", "PRD")
  }

  @Test
  internal fun projectIdIsDerivedFromURL() {
    val request = requestWithURI("http://host/app/deploys/SomeProjectId")

    val result = DeployDataController.projectId(request)

    assertThat(result).isEqualTo("SomeProjectId")
  }

  @Test
  internal fun projectIdIsEmptyWhenLastCharacterIsSlash() {
    val request = requestWithURI("http://host/app/deploys/")

    val result = DeployDataController.projectId(request)

    assertThat(result).isEqualTo("")
  }


  private fun controllerWith(projectManager: ProjectManager): DeployDataController {
    return DeployDataController(projectManager, pluginDescriptor(), links, webManager)
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

  private fun projectWith(buildType: SBuildType, environments: String): SProject {
    val feature = mock<SProjectFeatureDescriptor> {
      on { parameters } doReturn DeployConfig(
            "true", "PROJECT", "ENVIRONMENT", environments).toMap()
    }
    return mock {
      on { buildTypes } doReturn listOf(buildType)
      on { getAvailableFeaturesOfType(DeployConfigStore.type) } doReturn listOf(feature)
    }
  }

  private fun buildTypeWith(build: SFinishedBuild): SBuildType = mock {
    on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
    on { runningBuilds } doReturn emptyList()
    on { lastChangesFinished } doReturn build
  }

  @Suppress("SameParameterValue")
  private fun build(name: String, env: String): SFinishedBuild = mock {
    on { buildOwnParameters } doReturn mapOf(Pair("PROJECT", name), Pair("ENVIRONMENT", env))
    on { buildNumber } doReturn "1.0"
    on { finishDate } doReturn Date()
  }

  @Suppress("UNCHECKED_CAST")
  private fun deploys(result: ModelAndView?): List<Deploy>? {
    return result?.model?.get("deploys") as List<Deploy>?
  }

  @Suppress("UNCHECKED_CAST")
  private fun environments(result: ModelAndView?): List<String>? {
    return result?.model?.get("environments") as List<String>?
  }

}
