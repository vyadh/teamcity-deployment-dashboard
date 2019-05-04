// import {createTeamCitySource} from "./teamcity/teamCitySource"
import {createMemorySource} from "./memory/memorySource"

const configuration = {

  // Alternatively InMemorySource for testing
  // source: createTeamCitySource("http://localhost:8111"),
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
