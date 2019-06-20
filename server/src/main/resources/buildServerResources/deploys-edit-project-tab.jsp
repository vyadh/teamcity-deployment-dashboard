<%@ include file="/include.jsp"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>

<c:url value="/admin/deploys-edit-project.html" var="actionUrl" />

<bs:linkScript>
    ${teamcityPluginResourcesPath}js/deploys-edit-project-config.js
</bs:linkScript>

<form
      id="deployDashboardForm"
      method="post"
      action="${actionUrl}"
      onsubmit="return saveDeploymentDashboardConfig(this, '${actionUrl}')">

  <div class="editDeploySettingsPage">

    <h2 class="noBorder">Deployment Dashboard</h2>
    <div class="grayNote">
      Defines what configuration properties are used from builds to get project and environment
      information for project '${projectExternalId}' and sub-projects in order to generate the
      deployment dashboard.
    </div>

    <br/>

    <p>
      <forms:checkbox
        name="dashboardEnabled"
        checked="${dashboardEnabled}"
        onclick="$('deployDashboardConfig').hidden = !this.checked"/>

      <label for="dashboardEnabled">Enable Dashboard</label>
    </p>

    <table id="deployDashboardConfig" class="runnerFormTable" ${dashboardEnabled ? '' : 'hidden'}>
      <tr>
        <td><label for="projectKey">Project Property:</label></td>
        <td>
          <forms:textField maxlength="100" name="projectKey" value="${projectKey}"/>
          <span class="smallNote">
            Property name for the project, or leave blank to use the actual project name.
            Use no prefix for configuration parameters, 'env.' for environment variables
            and 'system.' for system properties.
          </span>
        </td>
      </tr>
      <tr>
        <td><label for="environmentKey">Environment Property:</label></td>
        <td>
          <forms:textField maxlength="100" name="environmentKey" value="${environmentKey}"/>
          <span class="smallNote">
            Property name for the environment where the build was deployed, or blank to use the
            name of the build configuration. Use no prefix for configuration parameters,
            'env.' for environment variables and 'system.' for system properties.
          </span>
        </td>
      </tr>
      <tr>
        <td><label for="environments">Environments:</label></td>
        <td>
          <forms:textField maxlength="500" name="environments" value="${environments}" className="longField"/>
          <span class="smallNote">
            Comma separated and ordered list of environments being deploy to.
            Deployment builds not in this list will not be shown.
          </span>
        </td>
      </tr>
      <tr>
        <td><label for="refreshSecs">Refresh Interval:</label></td>
        <td>
          <forms:select name="refreshSecs" enableFilter="true" className="smallField">
            <forms:option selected="${refreshSecs.isEmpty()}" value="">No Refresh</forms:option>
            <forms:option selected="${refreshSecs == '5'}" value="5">5 seconds</forms:option>
            <forms:option selected="${refreshSecs == '10'}" value="10">10 seconds</forms:option>
            <forms:option selected="${refreshSecs == '30'}" value="30">30 seconds</forms:option>
            <forms:option selected="${refreshSecs == '60'}" value="60">60 seconds</forms:option>
          </forms:select>
          <span class="smallNote">
            Polling interval between fetching new deployment build information, in seconds.
          </span>
        </td>
      </tr>
    </table>

    <div class="saveButtonsBlock">
      <forms:submit label="Save" />
        <input type="hidden" id="projectExternalId" name="projectExternalId" value="${projectExternalId}"/>
      <forms:saving />
    </div>
  </div>
</form>
