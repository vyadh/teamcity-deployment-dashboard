import {createMemorySource} from "./sources/memory/memorySource"
// import {createTeamCityRestSource} from "./sources/teamcityRest/teamCityRestSource"
// import {createTeamCityPluginSource} from "./sources/teamcityPlugin/teamCityPluginSource"

const configuration = {

  // Alternatively InMemorySource for testing
  source: createMemorySource(),
  // source: createTeamCityRestSource("http://localhost:8111"),
  // source: createTeamCityPluginSource(),

  // Environments to display, as specified in the 'environment' property in builds
  environments: [
    "DEV",
    "TST",
    "UAT",
    "PRD"
  ]

}

export default configuration
