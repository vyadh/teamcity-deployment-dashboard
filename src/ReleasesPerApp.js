import Collections from './util/Collections'
import Versions from './Versions'

export const fetch = source => source.fetch()
    .then(deploys => Collections.groupBy(deploys, deploy => deploy.name))
    .then(grouped => markLatestAppRelease(grouped))

export const markLatestAppRelease = releasesPerApp => {
  let apps = Object.keys(releasesPerApp)
  for (let app of apps) {
    let releases = releasesPerApp[app]
    let versions = releases.map(release => release.version)
    let latestVersion = Versions.maxVersion(versions)

    markLatestVersion(app, releases, latestVersion)
  }
  return releasesPerApp
}

export const markLatestVersion = (app, releases, latestVersion) => {
  for (let release of releases) {
    release.latest = release.version === latestVersion
  }
}
