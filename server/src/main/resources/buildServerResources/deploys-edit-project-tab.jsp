<%@ include file="/include.jsp"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>

<c:url value="/admin/deploys-edit-project.html" var="actionUrl" />

<bs:linkScript>
    ${teamcityPluginResourcesPath}js/deploys-edit-project-config.js
</bs:linkScript>

<!-- todo show saving success and failure messages (rather than reload in js) -->

<form
      id="deployDashboardForm"
      method="post"
      action="${actionUrl}"
      onsubmit="return saveDeploymentDashboardConfig(this, '${actionUrl}')">

  <div class="editDeploySettingsPage">

    <h2 class="noBorder">Deployment Dashboard</h2>
    <div class="grayNote">
      Describes how to interpret the project '${projectExternalId}' and sub-projects to generate the
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
          <forms:textField maxlength="80" name="projectKey" value="${projectKey}"/>
          <span class="smallNote">Property name for the project, or leave blank to use the actual project name.</span>
        </td>
      </tr>
      <tr>
        <td><label for="environmentKey">Environment Property:</label></td>
        <td>
          <forms:textField maxlength="80" name="environmentKey" value="${environmentKey}"/>
          <span class="smallNote">
            Property name for the environment where the build was deployed, or blank to use the
            name of the build configuration.
          </span>
        </td>
      </tr>
      <tr>
        <td><label for="environments">Environments:</label></td>
        <td>
          <forms:textField maxlength="200" name="environments" value="${environments}"/>
          <span class="smallNote">
            Comma separated and ordered list of environments being deploy to.
            Deployment builds not in this list will not be shown.
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
