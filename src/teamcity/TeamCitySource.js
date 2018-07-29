import TeamCityConverter from "./TeamCityConverter"

class TeamCitySource {

  converter(data) {
    let converter = new TeamCityConverter
    return converter.convert(data)
  }

  fetch() {
    let url = "http://localhost:8080/guestAuth/app/rest/latest/buildTypes?locator=type:deployment&fields=buildType(id,name,type,builds($locator(canceled:false,count:1),build(number,status,finishDate,properties(property(name,value)))))"
    return fetch(url, {
      headers: {
//        'Authorization': 'Basic '+btoa('admin:'),
//        'Access-Control-Allow-Credentials': true,
//        'Credentials': 'include',
        'Accept': 'application/json',
//        'Content-Type': 'application/json'
      }
    })
        .then(response => {
          if (response.ok) {
            return response.json()
          } else {
            throw new Error('Could not fetch from URL')
          }
        })
//    .then(data => console.log(data))
//    .catch(error => console.log(error))
  }

}

export default TeamCitySource
