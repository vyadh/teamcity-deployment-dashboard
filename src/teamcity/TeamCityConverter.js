class TeamCityConverter {

  convert(json) {
    return TeamCityConverter.convertTeamCityFormat(json)
  }

  static convertTeamCityFormat(json) {
    if (json === undefined) {
      return []
    }

    let result = []

    for (let buildType of json.buildType) {
      if (buildType.type === 'deployment') {
        let builds = buildType.builds.build
        if (builds.length === 0) {
          continue
        }
        let build = builds[0]
        let dateTime = TeamCityConverter.parseTeamCityDateTime(build.finishDate)
        let env = undefined
        let project = undefined
        for (let property of build.properties.property) {
          if (property.name === 'PROJECT') project = property.value
          else if (property.name === 'ENVIRONMENT') env = property.value
        }
        if (project === undefined) {
          continue
        }
        
        result.push({
          name: project,
          version: build.number,
          environment: env,
          time: dateTime,
          status: build.status
        })
      }
    }
  
    return result;
  }
  
  /* Example: 20180617T083210+0100 -> 2018-06-16T08:27:06+0100 */
  static parseTeamCityDateTime(text) {
    let year = text.substr(0, 4)
    let month = text.substr(4, 2)
    let day = text.substr(6, 2)
    let hour = text.substr(9, 2)
    let min = text.substr(11, 2)
    let sec = text.substr(13, 2)
    let tz = text.substr(15)
    let iso = `${year}-${month}-${day}T${hour}:${min}:${sec}${tz}`
    return new Date(iso)
  }
  
}

export default TeamCityConverter
