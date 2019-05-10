import {groupBy} from "./util/collections"
import {maxVersion} from "./util/versions"

export const fetch = source => source.fetch()
    .then(deploys => groupBy(deploys, deploy => deploy.name))
    .then(grouped => markLatestAppRelease(grouped))

export const markLatestAppRelease = releasesPerApp => {
  let apps = Object.keys(releasesPerApp)
  for (let app of apps) {
    let releases = releasesPerApp[app]
    let versions = releases.map(release => release.version)
    let latestVersion = maxVersion(versions)

    markLatestVersion(app, releases, latestVersion)
  }
  return releasesPerApp
}

export const markLatestVersion = (app, releases, latestVersion) => {
  for (let release of releases) {
    release.latest = release.version === latestVersion
  }
}
