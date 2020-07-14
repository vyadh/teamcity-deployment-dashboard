package com.github.vyadh.teamcity.deploys

import com.github.vyadh.teamcity.deploys.processing.BuildMocks.finished
import com.nhaarman.mockitokotlin2.*
import jetbrains.buildServer.serverSide.*
import jetbrains.buildServer.serverSide.parameters.types.PasswordsSearcher
import jetbrains.buildServer.util.ItemProcessor
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.invocation.InvocationOnMock
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class DeployDataControllerTest {

  private val webManager = mock<WebControllerManager> { }
  private val links = links("http://host/builds/id/123")

  @Test
  internal fun doHandleRespondsAsJson() {
    val projectManager = mock<ProjectManager> { }
    val controller = controllerWith(projectManager, buildHistory())
    val response = mock<HttpServletResponse> { }

    controller.doHandle(anyRequest(), response)

    verify(response).contentType = "application/json"
  }

  @Test
  internal fun doHandleReturnsEmptyCollectionsWhenProjectNotFound() {
    val projectManager = projectManager(null)
    val controller = controllerWith(projectManager, buildHistory())

    val result = controller.doHandle(anyRequest(), anyResponse())

    val environments = environments(result) ?: error("no environments entry found")
    assertThat(environments).isEmpty()

    val deploys = deploys(result) ?: error("no deploys entry found")
    assertThat(deploys).isEmpty()
  }

  @Test
  internal fun doHandlePopulatesDeploys() {
    val project = projectWith(environments = "")
    val build = build("SomeProject", "DEV")
    val controller = controllerWith(projectManager(project), buildHistory(build))

    val result = controller.doHandle(anyRequest(), anyResponse())

    val deploy = deploys(result)?.first()!!
    assertThat(deploy.project).isEqualTo("SomeProject")
    assertThat(deploy.environment).isEqualTo("DEV")
  }

  @Test
  internal fun doHandlePopulatesEnvironments() {
    val project = projectWith(environments = "DEV,PRD")
    val build = build("SomeProject", "ANY")
    val controller = controllerWith(projectManager(project), buildHistory(build))

    val result = environments(controller.doHandle(anyRequest(), anyResponse()))

    assertThat(result).containsExactlyInAnyOrder("DEV", "PRD")
  }

  @Test
  internal fun doHandlePopulatesRefreshSecs() {
    val project = projectWith(refreshSecs = "5")
    val build = build("SomeProject", "ANY")
    val controller = controllerWith(projectManager(project), buildHistory(build))

    val result = refreshSecs(controller.doHandle(anyRequest(), anyResponse()))

    assertThat(result).isEqualTo("5")
  }

  @Test
  internal fun projectIdIsDerivedFromURL() {
    val request = requestWithURI("http://host/app/deployment-dashboard/SomeProjectId")

    val result = DeployDataController.projectId(request)

    assertThat(result).isEqualTo("SomeProjectId")
  }

  @Test
  internal fun projectIdIsEmptyWhenLastCharacterIsSlash() {
    val request = requestWithURI("http://host/app/deployment-dashboard/")

    val result = DeployDataController.projectId(request)

    assertThat(result).isEqualTo("")
  }

  @Test
  internal fun secretsAreObfuscatedWhenPresent() {
    val project = projectWith(environments = "")
    val build = build("Some[passwd]Project", "DEV", "Ver[passwd]sion")
    val controller = controllerWith(
          projectManager(project), buildHistory(build), passwordsSearcher("passwd"))

    val result = controller.doHandle(anyRequest(), anyResponse())

    val deploy = deploys(result)?.first()!!
    assertThat(deploy.project).isEqualTo("Some[******]Project")
    assertThat(deploy.version).isEqualTo("Ver[******]sion")
    assertThat(deploy.environment).isEqualTo("DEV")
  }


  private fun controllerWith(
        projectManager: ProjectManager,
        buildHistory: BuildHistory,
        passwordsSearcher: PasswordsSearcher = passwordsSearcher()): DeployDataController {

    return DeployDataController(
          projectManager, pluginDescriptor(), links, buildHistory, passwordsSearcher, webManager)
  }

  private fun pluginDescriptor(): PluginDescriptor = mock {
    on { getPluginResourcesPath(any()) } doReturn "path"
  }

  private fun passwordsSearcher(vararg passwords: String): PasswordsSearcher = mock {
    on { collectPasswords(any()) } doReturn setOf(*passwords)
  }

  private fun anyRequest() = requestWithURI("http://host/app/deployment-dashboard/id")

  private fun anyResponse() = mock<HttpServletResponse> { }

  private fun requestWithURI(uri: String) = mock<HttpServletRequest> {
    on { requestURI } doReturn uri
  }

  @Suppress("SameParameterValue")
  private fun links(link: String): WebLinks = mock {
    on { getViewResultsUrl(any()) } doReturn link
  }

  private fun projectManager(project: SProject?): ProjectManager = mock {
    on { findProjectByExternalId(any()) } doReturn project
  }

  private fun projectWith(
        environments: String = "DEV",
        refreshSecs: String = ""): SProject {

    val type = buildType()
    val feature = mock<SProjectFeatureDescriptor> {
      on { parameters } doReturn DeployConfig(
            "true",
            "PROJECT",
            "VERSION",
            "ENVIRONMENT",
            environments,
            "CUSTOM",
            refreshSecs
      ).toMap()
    }
    return mock {
      on { buildTypes } doReturn listOf(type)
      on { getAvailableFeaturesOfType(DeployConfigStore.type) } doReturn listOf(feature)
    }
  }

  private fun buildType(): SBuildType {
    return mock {
      on { internalId } doReturn "id"
      on { getOption(BuildTypeOptions.BT_BUILD_CONFIGURATION_TYPE) } doReturn "DEPLOYMENT"
      on { runningBuilds } doReturn emptyList()
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun buildHistory(build: SFinishedBuild? = null): BuildHistory = mock {
    return mock {
      val invokeWithBuild: (InvocationOnMock) -> Unit = { invocation ->
        if (build != null) {
          val processor = invocation.arguments[5] as ItemProcessor<SFinishedBuild>
          processor.processItem(build)
        }
      }
      on {
        processEntries(
              anyString(),
              isNull(),
              eq(true),
              eq(false),
              eq(false),
              any<ItemProcessor<SFinishedBuild>>()
        )
      } doAnswer invokeWithBuild
    }
  }

  @Suppress("SameParameterValue")
  private fun build(name: String, env: String, version: String = ""): SFinishedBuild {
    return finished(
          properties = mapOf(
                Pair("PROJECT", name),
                Pair("VERSION", version),
                Pair("ENVIRONMENT", env)
          )
    )
  }

  @Suppress("UNCHECKED_CAST")
  private fun deploys(result: ModelAndView?): List<Deploy>? {
    return result?.model?.get("deploys") as List<Deploy>?
  }

  @Suppress("UNCHECKED_CAST")
  private fun environments(result: ModelAndView?): List<String>? {
    return result?.model?.get("environments") as List<String>?
  }

  private fun refreshSecs(result: ModelAndView?): String? {
    return result?.model?.get("refreshSecs") as String?
  }

}
