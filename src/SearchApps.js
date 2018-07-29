export default class SearchApps {

  static filter(text, unfilteredReleasesPerApp) {
    let searchText = text.toLowerCase()
    let apps = Object.keys(unfilteredReleasesPerApp)
    let appsFiltered = apps.filter(app => app.toLowerCase().indexOf(searchText) !== -1)

    // noinspection UnnecessaryLocalVariableJS
    let filteredReleasesPerApp = appsFiltered.reduce(
        (acc, app) => this.objectWith(acc, app, unfilteredReleasesPerApp[app]),
        { });
    return filteredReleasesPerApp
  }

  static objectWith(object, key, value) {
    object[key] = value
    return object
  }

}
