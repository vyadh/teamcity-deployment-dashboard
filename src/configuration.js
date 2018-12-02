import TeamCitySource from "./teamcity/TeamCitySource"

const configuration = {

  // Alternatively InMemorySource for testing
  source: new TeamCitySource("http://localhost:8080"),
  // source: new InMemorySource(),

  // Environments to display, as specified in the 'environment' property in builds
  environments: [
    "DEV",
    "TST",
    "UAT",
    "PRD"
  ]

}

export default configuration
