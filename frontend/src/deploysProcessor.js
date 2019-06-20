import {groupBy} from "./util/collections"
import {maxVersion} from "./util/versions"

export const groupPerApp = deploys => {
  let grouped = groupBy(deploys, deploy => deploy.name)
  return markLatestAppDeploy(grouped)
}

export const markLatestAppDeploy = deploysPerApp => {
  let apps = Object.keys(deploysPerApp)
  for (let app of apps) {
    let deploys = deploysPerApp[app]
    let versions = deploys.map(deploy => deploy.version)
    let latestVersion = maxVersion(versions)

    markLatestVersion(app, deploys, latestVersion)
  }
  return deploysPerApp
}

export const markLatestVersion = (app, deploys, latestVersion) => {
  for (let deploy of deploys) {
    deploy.latest = deploy.version === latestVersion
  }
}
