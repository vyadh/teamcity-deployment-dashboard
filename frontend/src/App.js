import React, { useState, useEffect, useMemo } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCheckCircle } from '@fortawesome/free-solid-svg-icons'
import { faExclamationCircle } from '@fortawesome/free-solid-svg-icons'
import { faCircleNotch } from '@fortawesome/free-solid-svg-icons'
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons'
import { Search } from './components/Search'
import * as dateTimes from './util/dateTimes'
import * as releases from './releasesPerApp'
import './App.css'
import {filterApps} from "./searchApps"

const App = ({configuration}) => {

  let environments = configuration.environments
  let source = configuration.source
  let [releasesPerApp, setReleasesPerApp] = useState([])

  let load = () => releases
      .fetch(source)
      .then(setReleasesPerApp)

  useEffect(() => {
    load()
  }, [source])

  useEffect(() => {
    if (configuration.refreshPeriodMillis) {
      let id = setInterval(() => load(), configuration.refreshPeriodMillis)
      return () => clearInterval(id)
    }
  })

  return (
      <div>
        <Title embedded={configuration.embedded}/>
        <Page
            environments={environments}
            unfilteredReleasesPerApp={releasesPerApp}/>
      </div>
  )
}

const Title = ({embedded}) =>
    <h1 className={embedded ? "invisible" : "visible"}>Deployments</h1>

const Page = ({environments, unfilteredReleasesPerApp}) => {
  let [filter, setFilter] = useState("")
  let releasesPerApp = useMemo(
      () => filterApps(filter, unfilteredReleasesPerApp),
      [filter, unfilteredReleasesPerApp])

  return <>
    <Search filter={setFilter}/>
    <Releases environments={environments} releasesPerApp={releasesPerApp}/>
  </>
}

const Releases = ({environments, releasesPerApp}) => {
  return (
      <div className="list">
        <table>
          <thead>
          <ReleaseHeader environments={environments}/>
          </thead>
          <tbody>
          <ReleaseRows environments={environments} releasesPerApp={releasesPerApp}/>
          </tbody>
        </table>
      </div>
  )
}

const ReleaseHeader = ({environments}) => (
    <tr>
      <th>Project</th>
      {environments.map(env => <th key={env}>{env}</th>)}
    </tr>
)

const ReleaseRows = ({environments, releasesPerApp}) => {
  let apps = useMemo(() => Object.keys(releasesPerApp).sort(), [releasesPerApp])

  return apps.map(app =>
      <ReleaseRow
          key={app}
          app={app}
          environments={environments}
          releases={releasesPerApp[app]}/>
  )
}

const ReleaseRow = ({app, environments, releases}) => (
    <tr>
      <td>{app}</td>
      {environments.map(env =>
          <Release key={env} environment={env} releases={releases}/>)}
    </tr>
)

const Release = ({environment, releases}) => {
  let found = releases.find(release => release.environment === environment)

  if (found === undefined) {
    return <td key={environment}/>
  } else {
    return <td key={environment}><Build release={found}/></td>
  }
}

const Build = ({release}) => (
    <a href={release.link}>
      <div className="build">
        <div className="build-status">
          <StatusIcon status={release.status} latest={release.latest}/>
        </div>
        <div className="build-info">
          <span className="build-version">{release.version}</span>
          <span className="build-time">{dateTimes.format(release.time)}</span>
        </div>
      </div>
    </a>
)

const StatusIcon = ({status, latest}) => {
  let iconType = statusIconClass(status)
  let rotateClass = status === "RUNNING" ? "fa-spin" : ""
  let latestClass = latest ? "status-latest" : "status-older"
  let classes = `status-icon status-${status} ${rotateClass} ${latestClass}`

  return <FontAwesomeIcon icon={iconType} className={classes}/>
}

const statusIconClass = status => {
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

export default App;
