import TeamCityConverter from "./TeamCityConverter"

/*
 * Requires setting TeamCity internal CORS property 'rest.cors.origins'. For example:
 *   rest.cors.origins=http://localhost:3000,http://127.0.0.1:3000
 */
export const createTeamCitySource = baseUrl => {
  return {
    fetch: () => fetchRest(baseUrl)
  }
}

const fetchRest = baseUrl => {
  console.log("Fetching deployments from TeamCity...")

  let query = "locator=type:deployment&" +
      "fields=buildType(" +
      "id,name,type,builds(" +
      "$locator(canceled:false,count:1)," +
      "build(number,status,finishDate,properties(property(name,value)))" +
      "))"

  let url = baseUrl + "/app/rest/latest/buildTypes?" + query

  return fetch(url, {
    credentials: 'include',
    headers: {
      'Accept': 'application/json'
    }
  })
      .then(extractJson)
      .then(parse)
      // .then(data => { console.log(data); return data })
      .catch(error => console.log(error))
}

const extractJson = response => {
  if (response.ok) {
    return response.json()
  } else {
    throw new Error('Could not fetch from URL')
  }
}

const parse = data => {
  let converter = new TeamCityConverter()
  return converter.convert(data)
}
