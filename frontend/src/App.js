import React, { useState, useEffect, useMemo } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCheckCircle } from '@fortawesome/free-solid-svg-icons'
import { faExclamationCircle } from '@fortawesome/free-solid-svg-icons'
import { faCircleNotch } from '@fortawesome/free-solid-svg-icons'
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons'
import { Search } from './components/Search'
import * as dateTimes from './util/dateTimes'
import * as deploysProcessor from './deploysProcessor'
import './App.css'
import {filterApps} from "./searchApps"

const App = ({configuration}) => {

  let source = configuration.source
  let [environments, setEnvironments] = useState([])
  let [deploysPerApp, setDeploysPerApp] = useState([])
  let [refreshSecs, setRefreshSecs] = useState("")

  let load = () => source.fetch().then(data => {
    let {environments, refreshSecs, deploys} = data

    setEnvironments(environments)
    setDeploysPerApp(deploysProcessor.groupPerApp(deploys))
    setRefreshSecs(refreshSecs)
  })

  useEffect(() => {
    load()
  }, [source])

  useEffect(() => {
    if (refreshSecs) {
      let id = setInterval(() => load(), parseInt(refreshSecs) * 1000)
      return () => clearInterval(id)
    }
  }, [refreshSecs])

  return (
      <div>
        <Title embedded={configuration.embedded}/>
        <Page
            environments={environments}
            unfilteredDeploysPerApp={deploysPerApp}/>
      </div>
  )
}

const Title = ({embedded}) =>
    <h1 className={embedded ? "invisible" : "visible"}>Deployments</h1>

const Page = ({environments, unfilteredDeploysPerApp}) => {
  let [filter, setFilter] = useState("")
  let deploysPerApp = useMemo(
      () => filterApps(filter, unfilteredDeploysPerApp),
      [filter, unfilteredDeploysPerApp])

  return <>
    <Search filter={setFilter}/>
    <Deploys environments={environments} deploysPerApp={deploysPerApp}/>
  </>
}

const Deploys = ({environments, deploysPerApp}) => {
  return (
      <div className="list">
        <table>
          <thead>
            <DeployHeader environments={environments}/>
          </thead>
          <tbody>
            <DeployRows environments={environments} deploysPerApp={deploysPerApp}/>
          </tbody>
        </table>
      </div>
  )
}

const DeployHeader = ({environments}) => (
    <tr>
      <th>Project</th>
      {environments.map(env => <th key={env}>{env}</th>)}
    </tr>
)

const DeployRows = ({environments, deploysPerApp}) => {
  let apps = useMemo(() => Object.keys(deploysPerApp).sort(), [deploysPerApp])

  return apps.map(app =>
      <DeployRow
          key={app}
          app={app}
          environments={environments}
          deploys={deploysPerApp[app]}/>
  )
}

const DeployRow = ({app, environments, deploys}) => (
    <tr>
      <td>{app}</td>
      {environments.map(env =>
          <Deploy key={env} environment={env} deploys={deploys}/>)}
    </tr>
)

const Deploy = ({environment, deploys}) => {
  let found = deploys.find(deploy => deploy.environment === environment)

  if (found === undefined) {
    return <td key={environment}/>
  } else {
    return <td key={environment}><Build deploy={found}/></td>
  }
}

const Build = ({deploy}) => (
    <a href={deploy.link}>
      <div className="build">
        <div className={`build-status ${statusClass(deploy.status)}`}>
          <StatusIcon status={deploy.status} latest={deploy.latest}/>
        </div>
        <div className="build-info grow">
          <span className="build-version">{deploy.version}</span>
          <span className="build-time">{dateTimes.format(deploy.time)}</span>
        </div>
      </div>
    </a>
)

const StatusIcon = ({status, latest}) => {
  let iconType = statusIconClass(status)
  let rotateClass = status === "RUNNING" ? "fa-spin" : ""
  let ageClass = latest ? "status-latest" : "status-older"
  let classes = `status-icon ${statusClass(status)} ${rotateClass} ${ageClass}`

  return <FontAwesomeIcon icon={iconType} className={classes}/>
}

const statusClass = (status) => {
  return `status-${status}`
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
