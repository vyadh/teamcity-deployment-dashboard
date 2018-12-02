import Collections from './util/Collections'
import Versions from './Versions'

export default class ReleasesPerApp {

  static fetch(source) {
    return source.fetch()
        .then(data => source.parse(data))
        .then(deploys => Collections.groupBy(deploys, deploy => deploy.name))
        .then(grouped => this.markLatestAppRelease(grouped))
  }

  static markLatestAppRelease(releasesPerApp) {
    let apps = Object.keys(releasesPerApp)
    for (let app of apps) {
      let releases = releasesPerApp[app]
      let versions = releases.map(release => release.version)
      let latestVersion = Versions.maxVersion(versions)

      this.markLatestVersion(app, releases, latestVersion)
    }
    return releasesPerApp
  }

  static markLatestVersion(app, releases, latestVersion) {
    for (let release of releases) {
      release.latest = release.version === latestVersion
    }
  }

}
