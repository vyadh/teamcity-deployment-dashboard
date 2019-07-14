import React, { useState, useEffect, useMemo } from 'react';
import { Search } from './components/Search'
import * as deploysProcessor from './deploysProcessor'
import './App.css'
import {filterApps} from "./searchApps"
import {sort} from "./util/collections"
import {Deployment} from "./Deployment";

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
        <table style={{ '--environment-count': environments.length }}>
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

/* Header + spacer column + environment columns */
const DeployHeader = ({environments}) => (
  <tr>
    <th/>
    <th/>
    {environments.map(env => <th key={env}>{env}</th>)}
  </tr>
)

const DeployRows = ({environments, deploysPerApp}) => {
  let apps = useMemo(() => sort(Object.keys(deploysPerApp)), [deploysPerApp])

  return apps.map(app =>
      <DeployRow
          key={app}
          app={app}
          environments={environments}
          deploys={deploysPerApp[app]}/>
  )
}

/* Header + spacer column + environment columns */
const DeployRow = ({app, environments, deploys}) => (
  <tr>
    <td>{app}</td>
    <td/>
    {environments.map(env =>
        <Deployment key={env} environment={env} deploys={deploys}/>)}
  </tr>
)


export default App;
