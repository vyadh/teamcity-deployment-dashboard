import {createTeamCityRestSource} from "../teamcityRest/teamCityRestSource"

export const createTeamCityPluginSource = () => {
  return {
    fetch: () => createTeamCityRestSource("")
  }
}
