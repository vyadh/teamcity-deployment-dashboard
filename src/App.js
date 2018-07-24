import React, { Component } from 'react'
import ReactDOM from 'react-dom'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCheckCircle } from '@fortawesome/free-solid-svg-icons'
import { faExclamationCircle } from '@fortawesome/free-solid-svg-icons'
import { faCircleNotch } from '@fortawesome/free-solid-svg-icons'
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons'
import Collections from './Collections';
import './App.css'

class App extends React.Component {
  constructor() {
    super();
    
    this.state = {
      environments: [
        "DEV",
        "TST",
        "UAT",
        "PRD"
      ],
      filter: "",
      unfilteredReleasesPerApp: [],
      releasesPerApp: []
    }
  }
  
  componentDidMount() {
    this.load()
    
    //let id = setInterval(() => this.load(), 2000)
    // Allow debugging just one change
    // setInterval(() => clearInterval(id), 6000)
  }
  
  load() {
    let promise = fetchReleasesPerApp(fetchPlanetDeploys, data => data)
    //let promise = fetchReleasesPerApp(fetchTeamCityDeploys, convertTeamCityFormat)
    
    promise.then(releasesPerApp => {
      let filteredReleasesPerApp = filterApps(this.state.filter, releasesPerApp)
      
      this.setState({
        unfilteredReleasesPerApp: releasesPerApp,
        releasesPerApp: filteredReleasesPerApp
      })
    })
  }
  
  filter(value) {
    let filteredReleasesPerApp = filterApps(value, this.state.unfilteredReleasesPerApp)
    
    this.setState({
      filter: value, // Needed for interval refreshes
      releasesPerApp: filteredReleasesPerApp
    })
  }

  render() {
    return <Page state={this.state} filter={value => this.filter(value)}/>
  }
}


//todo test
function filterApps(text, unfilteredReleasesPerApp) {
  let searchText = text.toLowerCase()
  let apps = Object.keys(unfilteredReleasesPerApp)
  let appsFiltered = apps.filter(app => app.toLowerCase().indexOf(searchText) !== -1)
  let filteredReleasesPerApp = appsFiltered.reduce(
    (acc, app) => objectWith(acc, app, unfilteredReleasesPerApp[app]),
    { });
  return filteredReleasesPerApp
}

//todo test
function objectWith(object, key, value) {
  object[key] = value
  return object
}


function fetchReleasesPerApp(fetch, parse) {
  return fetch()
    .then(data => parse(data))
    .then(deploys => Collections.groupBy(deploys, deploy => deploy.name))
    .then(grouped => markLatestAppRelease(grouped))
}

function markLatestAppRelease(releasesPerApp) {
  let apps = Object.keys(releasesPerApp)
  for (let app of apps) {
    let releases = releasesPerApp[app]
    let versions = releases.map(release => release.version)
    let latestVersion = maxVersion(versions)
    
    markLatestVersion(app, releases, latestVersion)
  }
  return releasesPerApp
}

function markLatestVersion(app, releases, latestVersion) {
  for (let release of releases) {
    release.latest = release.version === latestVersion
  }
}

function Page(props) {
  return (
    <div>
      <h1>Releases</h1>
      <Search2 filter={props.filter}/>
      <Releases environments={props.state.environments} releasesPerApp={props.state.releasesPerApp}/>
    </div>
  )
}

function Search(props) {
  return (
    <input type="text" onChange={event => props.filter(event.target.value)}/>
  )
}

function Search2(props) {
  return (
    <div className="search">
      <svg xmlns="http://www.w3.org/2000/svg" style={{display: "none"}}>
        <symbol xmlns="http://www.w3.org/2000/svg" id="search-icon-magnifier" viewBox="0 0 40 41">
          <path d="M26.51 28.573c-2.803 2.34-6.412 3.748-10.35 3.748C7.236 32.32 0 25.087 0 16.16 0 7.236 7.235 0 16.16 0c8.926 0 16.16 7.235 16.16 16.16 0 4.213-1.61 8.048-4.25 10.925L40 39.015l-1.524 1.524L26.51 28.572zm-10.35 2.132c8.033 0 14.545-6.512 14.545-14.544S24.193 1.617 16.16 1.617 1.617 8.128 1.617 16.16c0 8.033 6.512 14.545 14.545 14.545z"
          fillRule="evenodd"/>
        </symbol>
        <symbol xmlns="http://www.w3.org/2000/svg" id="search-icon-cross" viewBox="0 0 20 20">
          <path d="M8.96 10L.52 1.562 0 1.042 1.04 0l.522.52L10 8.96 18.438.52l.52-.52L20 1.04l-.52.522L11.04 10l8.44 8.438.52.52L18.96 20l-.522-.52L10 11.04l-8.438 8.44-.52.52L0 18.96l.52-.522L8.96 10z" fillRule="evenodd"/>
        </symbol>
      </svg>

      <form noValidate="noValidate" className="search-form">
        <input type="search" placeholder="Search..." required="required" className="search-input" onChange={event => props.filter(event.target.value)}/>
        <span className="search-submit">
          <svg role="img" aria-label="Search">
            <use xlinkHref="#search-icon-magnifier"/>
          </svg>
        </span>
        <button type="reset" className="search-reset" onClick={event => props.filter("")}>
          <svg role="img" aria-label="Reset">
            <use xlinkHref="#search-icon-cross"/>
          </svg>
        </button>
      </form>
    </div>
  )
}

function Releases(props) {
  let releasesPerApp = props.releasesPerApp
  let apps = Object.keys(releasesPerApp).sort()

  return (
    <div className="list">
      <table>
        <thead>
          <ReleaseHeader environments={props.environments}/>
        </thead>
        <tbody>
          {apps.map(app =>
            <ReleaseRow 
              key={app} 
              app={app} 
              environments={props.environments}
              releases={releasesPerApp[app]}/>
          )}
        </tbody>
      </table>
    </div>
  )
}

function ReleaseHeader(props) {
  return (
    <tr>
      <th>Project</th>
      {props.environments.map(env => <th key={env}>{env}</th>)}
    </tr>
  )
}

function ReleaseRow(props) {
  return (
    <tr>
      <td>{props.app}</td>
      {props.environments.map(env =>
        <Release key={env} environment={env} releases={props.releases}/>)}
    </tr>
  )
}

function Release(props) {
  let found = props.releases.find(release => release.environment === props.environment)
  
  if (found === undefined) {
    return <td key={props.environment}/>
  } else {
    return <td key={props.environment}><Build release={found}/></td>
  }
}

function Build(props) {
  return (
    <div className="build">
      <div className="build-status">
        <StatusIcon status={props.release.status} latest={props.release.latest}/>
      </div>
      <div className="build-info">
        <span className="version">{props.release.version}</span>
        <span className="time">{formatDateTime(props.release.time)}</span>
      </div>
    </div>
  )
}

function formatDateTime(isoDateTime) {
  let date = new Date(isoDateTime);
  if (isToday(date)) {
    return date.toLocaleTimeString();
  } else {
    return date.toLocaleString(
      "en-gb", {day: "numeric", month: "long", year: "numeric"});
  }
}

function isToday(date) {
  return date.toDateString() == new Date().toDateString();
}

function StatusIcon(props) {
  let iconType = statusIconClass(props.status)
  let rotate = props.status === "RUNNING" ? "fa-spin" : ""
  let latest = props.latest ? "status-latest" : "status-older"
  let classes = `status-icon status-${props.status} ${rotate} ${latest}`

  return <FontAwesomeIcon icon={iconType} className={classes}/>
}

function statusIconClass(status) {
  if (status === "SUCCESS") {
    return faCheckCircle
  } else if (status === "FAILURE") {
    return faExclamationCircle
  } else if (status === "RUNNING") {
    return faCircleNotch // fa-spin";
  } else {
    return faQuestionCircle
  }
}

/*
compareVersionTest()
maxVersionTest()
teamCityConversionTest()
*/


function fetchTeamCityDeploys() {
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

function convertTeamCityFormat(json) {
  let buildTypes = json.buildType
  
  let result = []
  
  for (let buildType of buildTypes) {
    if (buildType.type === 'deployment') {
      let builds = buildType.builds.build
      if (builds.length === 0) {
        continue
      }
      let build = builds[0]
      let dateTime = parseTeamCityDateTime(build.finishDate)
      var env = undefined
      var project = undefined
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
function parseTeamCityDateTime(text) {
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




/* Use maxVersion to sort a list of likely-seen versions for concise tests */
function compareVersionTest() {
  let versions = [
    '1',
    '2',
    '1.0',
    '1.1',
    '1.0.0',
    '1.0.1',
    '1.0.2',
    '1.1.0',
    '2.0.0',
    '1.0.1.4159',
    '1.0.1.4160',
    '1.9.0',
    '1.10.0',
    '2.34.5',
    '2.3.34',
    '2.3.34+123',
    '2.3.34+23'
  ]
  
  let result = versions.sort(compareVersion)

  assertEqual(result, [
    '1',
    '1.0',
    '1.0.0',
    '1.0.1',
    '1.0.1.4159',
    '1.0.1.4160',
    '1.0.2',
    '1.1',
    '1.1.0',
    '1.9.0',
    '1.10.0',
    '2',
    '2.0.0',
    '2.3.34',
    '2.3.34+23',
    '2.3.34+123',
    '2.34.5'
  ])
}

function compareVersion(a, b) {
  // Hat tip: https://stackoverflow.com/a/47159772
  let ver1 = a.split(/[\.\+]/).map(s => s.padStart(10, "0")).join('.')
  let ver2 = b.split(/[\.\+]/).map(s => s.padStart(10, "0")).join('.')
  return ver1 > ver2
}


function maxVersionTest() {
  let versions = ['1.1', '1.0.1', '1.0.1.4159', '1.1.0+123']
  
  let result = maxVersion(versions)

  assertEqual(result, '1.1.0+123')
}

function maxVersion(versions) {
  return versions.reduce(
    (acc, current) => compareVersion(acc, current) ? acc : current)
}


function teamCityConversionTest() {
  let builds = {
    buildType: [
      {
        id: 'Releases_Betelgeuse_Dev',
        name: 'Deploy Dev',
        type: 'deployment',
        builds: {
          build: [
            {
              number: '1.0.2',
              status: 'SUCCESS',
              finishDate: "20180617T083210+0100",
              properties: {
                property: [
                  {
                    name: 'ENVIRONMENT',
                    value: 'DEV'
                  },
                  {
                    name: 'PROJECT',
                    value: 'Betelgeuse'
                  }
                ]
              }
            }
          ]
        }
      },
      {
        id: 'Releases_Betelgeuse_Test',
        name: 'Deploy Test',
        type: 'deployment',
        builds: {
          build: [
            {
              number: '1.0.1',
              status: 'SUCCESS',
              finishDate: "20180617T082834+0100",
              properties: {
                property: [
                  {
                    name: 'ENVIRONMENT',
                    value: 'TST'
                  },
                  {
                    name: 'PROJECT',
                    value: 'Betelgeuse'
                  }
                ]
              }
            }
          ]
        }
      },
      {
        id: 'Releases_Rigel_Dev',
        name: 'Deploy Dev',
        type: 'deployment',
        builds: {
          build: [
            {
              number: '1.0.1',
              status: 'SUCCESS',
              finishDate: "20180616T083737+0100",
              properties: {
                property: [
                  {
                    name: 'ENVIRONMENT',
                    value: 'DEV'
                  },
                  {
                    name: 'PROJECT',
                    value: 'Rigel'
                  }
                ]
              }
            }
          ]
        }
      }
    ]
  }
  
  let results = convertTeamCityFormat(builds)
  
  assertEqual(results, [
    {
      name: "Betelgeuse",
      version: "1.0.2",
      environment: "DEV",
      time: new Date("2018-06-17T08:32:10"),
      status: "SUCCESS"
    },
    {
      name: "Betelgeuse",
      version: "1.0.1",
      environment: "TST",
      time: new Date("2018-06-17T08:28:34"),
      status: "SUCCESS"
    },
    {
      name: "Rigel",
      version: "1.0.1",
      environment: "DEV",
      time: new Date("2018-06-16T08:37:37"),
      status: "SUCCESS"
    }
  ]);
}


function assertEqual(actual, expected) {
  let equal = JSON.stringify(actual) === JSON.stringify(expected)
  if (equal) {
    console.log(assertEqual.caller.name + ": Passed")
  } else {
    console.log(assertEqual.caller.name + ": FAILED! Expecting " + JSON.stringify(expected) + " but got " + JSON.stringify(actual))
  }
}


function fetchPlanetDeploys() {
  return new Promise((resolve, reject) => {
    resolve(fetchPlanetDeploysSync())
  })
}

function fetchPlanetDeploysSync() {
  return [
    {
      name: "Mercury",
      version: "1.3.3",
      environment: "DEV",
      time: "2018-06-09T21:58:12",
      status: "SUCCESS"
    },
    {
      name: "Mercury",
      version: "1.3.3",
      environment: "TST",
      time: "2018-06-09T21:58:12",
      status: "SUCCESS"
    },
    {
      name: "Mercury",
      version: "1.3.3",
      environment: "UAT",
      time: "2018-06-09T21:58:12",
      status: "SUCCESS"
    },
    {
      name: "Mercury",
      version: "1.3.3",
      environment: "PRD",
      time: "2018-06-09T21:58:12",
      status: "SUCCESS"
    },
    {
      name: "Venus",
      version: "17.0.1",
      environment: "PRD",
      time: "2018-06-04T22:20:33",
      status: "SUCCESS"
    },
    {
      name: "Venus",
      version: "17.0.2",
      environment: "TST",
      time: new Date().toISOString(),
      status: "RUNNING"
    },
    {
      name: "Venus",
      version: "17.0.1",
      environment: "UAT",
      time: "2018-06-01T22:20:33",
      status: "SUCCESS"
    },
    {
      name: "Venus",
      version: "17.0.2",
      environment: "DEV",
      time: "2018-06-01T22:20:33",
      status: "SUCCESS"
    },
    {
      name: "Earth",
      version: "5.3.0",
      environment: "DEV",
      time: "2018-06-04T21:59:51",
      status: "FAILURE"
    },
    {
      name: "Earth",
      version: "5.2.1",
      environment: "TST",
      time: "2018-06-04T21:59:51",
      status: "SUCCESS"
    },
    {
      name: "Earth",
      version: "5.2.1",
      environment: "UAT",
      time: "2018-06-04T21:59:51",
      status: "SUCCESS"
    },
    {
      name: "Earth",
      version: "5.2.1",
      environment: "PRD",
      time: "2018-06-04T21:59:51",
      status: "SUCCESS"
    },
    {
      name: "Uranus",
      version: "11.2.113",
      environment: "DEV",
      time: "2018-06-04T22:20:33",
      status: "SUCCESS"
    },
    {
      name: "Uranus",
      version: "11.2.113",
      environment: "TST",
      time: "2018-06-04T22:20:33",
      status: "SUCCESS"
    },
    {
      name: "Uranus",
      version: "11.2.112",
      environment: "PRD",
      time: "2018-06-04T22:20:33",
      status: "OTHER"
    },
    {
      name: "Ceres",
      version: "1.0.0",
      environment: "DEV",
      time: "2018-06-16T09:54:00",
      status: "SUCCESS"
    },
    {
      name: "Ceres",
      version: "1.0.0",
      environment: "TST",
      time: new Date().toISOString(),
      status: "RUNNING"
    },
    ...randomDeployment("Mars"),
    ...randomDeployment("Jupiter"),
    ...randomDeployment("Saturn"),
    ...randomDeployment("Neptune"),
    ...randomDeployment("Pluto"),
    ...randomDeployment("Haumea"),
    ...randomDeployment("Makemake"),
    ...randomDeployment("Eris"),
    ...randomDeployment("Quaoar"),
    ...randomDeployment("Sedna"),
    ...randomDeployment("Orcas")
  ]
}

function randomDeployment(name) {
  let envs = ["DEV", "TST", "UAT", "PRD"]
  let ver = (max) => Math.floor(Math.random() * max)
  let major = ver(9) + 1
  let minor = ver(20)
  let bugfix = [ver(3), ver(3), ver(3), ver(3)].sort().reverse()
  let failure = () => ver(15) < 2
  
  return [0, 1, 2, 3].map(i => ({
    name: name,
    version: major + "." + minor + "." + bugfix[i],
    environment: envs[i],
    time: "2018-06-16T09:54:00",
    status: failure() ? "FAILURE" : "SUCCESS"
  }))
}

export default App;
