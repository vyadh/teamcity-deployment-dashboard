import TeamCityConverter from "./TeamCityConverter"

/*
 * Requires setting TeamCity internal CORS property 'rest.cors.origins'. For example:
 *   rest.cors.origins=http://localhost:3000,http://127.0.0.1:3000
 */
class TeamCitySource {

  converter() {
    let converter = new TeamCityConverter()
    return (data) => converter.convert(data)
  }

  fetch() {
    console.log("Fetching deployments from TeamCity...")

    let query = "locator=type:deployment&fields=buildType(id,name,type,builds($locator(canceled:false,count:1),build(number,status,finishDate,properties(property(name,value)))))"
    // let url = "http://localhost:8080/guestAuth/app/rest/latest/buildTypes?" + query
    // let url = "http://localhost:8080/httpAuth/app/rest/latest/buildTypes?" + query
    let url = "http://localhost:8080/app/rest/latest/buildTypes?" + query

    return fetch(url, {
          credentials: 'include',
          headers: {
            'Accept': 'application/json'
          }
        })
        .then(response => {
          if (response.ok) {
            return response.json()
          } else {
            throw new Error('Could not fetch from URL')
          }
        })
        // .then(data => { console.log(data); return data })
        .catch(error => console.log(error))
  }

}

export default TeamCitySource
