import React from 'react';
import * as dateTimes from "./util/dateTimes";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faCircleNotch, faExclamationCircle, faQuestionCircle, faUserCircle} from "@fortawesome/free-solid-svg-icons";
import './Deployment.css'

export const Deployment = ({environment, deploys}) => {
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
        <StatusIcon
          status={deploy.status}
          running={deploy.running}
          personal={deploy.personal}
          latest={deploy.latest}/>
      </div>
      <div className="build-info">
        <span className="build-version">{deploy.version}</span>
        <span className="build-time">{dateTimes.format(deploy.time)}</span>
      </div>
    </div>
  </a>
)

const StatusIcon = ({status, running, personal, latest}) => {
  let iconType = statusIconClass(status, running, personal)
  let rotateClass = running ? "fa-spin" : ""
  let ageClass = latest ? "status-latest" : "status-older"
  let classes = `status-icon ${statusClass(status)} ${rotateClass} ${ageClass}`

  return <FontAwesomeIcon icon={iconType} className={classes}/>
}

const statusClass = (status) => {
  return `status-${status}`
}

const statusIconClass = (status, running, personal) => {
  if (personal) {
    return faUserCircle
  } else if (running) {
    return faCircleNotch
  } else if (status === "SUCCESS") {
    return faCheckCircle
  } else if (status === "FAILURE") {
    return faExclamationCircle
  } else {
    return faQuestionCircle
  }
}
