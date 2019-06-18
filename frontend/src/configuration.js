import {createTeamCityPluginSource} from "./sources/teamcityPlugin/teamCityPluginSource"
// import {createMemorySource} from "./sources/memory/memorySource"

const configuration = {

  // Running options (with appropriate imports):
  //   testing: createMemorySource()
  //   running outside of TeamCity: createTeamCityRestSource("http://localhost:8111")
  //   running as a TeamCity plugin: createTeamCityPluginSource()
  source: createTeamCityPluginSource(),
  // source: createMemorySource(),

  // Indicates whether the app is embedded into TeamCity where some things are turned off
  embedded: true,

}

export default configuration
