import React, { Component } from 'react'
import ReactDOM from 'react-dom'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCheckCircle } from '@fortawesome/free-solid-svg-icons'
import { faExclamationCircle } from '@fortawesome/free-solid-svg-icons'
import { faCircleNotch } from '@fortawesome/free-solid-svg-icons'
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons'
import Collections from './Collections';
import Versions from './Versions';
import './App.css'
import InMemorySource from "./inmemory/InMemorySource"
import TeamCitySource from "./teamcity/TeamCitySource"

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
    let source = new InMemorySource()
    // let source = new TeamCitySource()
    let promise = fetchReleasesPerApp(source.fetch, data => data)
    // let promise = fetchReleasesPerApp(source, convertTeamCityFormat)
    
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
    let latestVersion = Versions.maxVersion(versions)
    
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

function assertEqual(actual, expected) {
  let equal = JSON.stringify(actual) === JSON.stringify(expected)
  if (equal) {
    console.log(assertEqual.caller.name + ": Passed")
  } else {
    console.log(assertEqual.caller.name + ": FAILED! Expecting " + JSON.stringify(expected) + " but got " + JSON.stringify(actual))
  }
}


export default App;
