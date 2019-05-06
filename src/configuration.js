// import {createTeamCityRestSource} from "./sources/teamcityRest/teamCityRestSource"
import {createMemorySource} from "./sources/memory/memorySource"

const configuration = {

  // Alternatively InMemorySource for testing
  // source: createTeamCityRestSource("http://localhost:8111"),
  source: createMemorySource(),

  // Environments to display, as specified in the 'environment' property in builds
  environments: [
    "DEV",
    "TST",
    "UAT",
    "PRD"
  ]

}

export default configuration
