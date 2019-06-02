import {createTeamCityPluginSource} from "./sources/teamcityPlugin/teamCityPluginSource"
import {createMemorySource} from "./sources/memory/memorySource"

const configuration = {

  // Running options (with appropriate imports):
  //   testing: createMemorySource()
  //   running outside of TeamCity: createTeamCityRestSource("http://localhost:8111")
  //   running as a TeamCity plugin: createTeamCityPluginSource()
  source: createTeamCityPluginSource(),
  // source: createMemorySource(),

  // Environments to display, as specified in the 'environment' property in builds
  environments: [
    "DEV",
    "TST",
    "UAT",
    "PRD"
  ]

}

export default configuration
