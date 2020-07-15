function saveDeploymentDashboardConfig(form, actionUrl) {

  let parameters = {
    projectExternalId: form.projectExternalId.value,
    dashboardEnabled: form.dashboardEnabled.checked,
    projectKey: form.projectKey.value,
    versionKey: form.versionKey.value,
    environmentKey: form.environmentKey.value,
    environments: form.environments.value,
    customKey: form.customKey.value,
    refreshSecs: form.refreshSecs.value,
    multiEnvConfig: form.multiEnvConfig.checked
  }

  console.log("Saving deploy config for " + form.projectExternalId + ": " + JSON.stringify(parameters))

  BS.ajaxRequest(actionUrl, {
    parameters: Object.toQueryString(parameters),

    onComplete: transport => {
      if (transport.responseXML) {
        BS.XMLResponse.processErrors(transport.responseXML, {
          onProfilerProblemError: elem => {
            alert(elem.firstChild.nodeValue)
          }
        });
      }
      BS.reload(true)
    }
  })

  return false

}
