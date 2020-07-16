import React from 'react';
import * as dateTimes from "./util/dateTimes";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faCircleNotch, faExclamationCircle, faQuestionCircle, faClock} from "@fortawesome/free-solid-svg-icons";
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
      <div className={`build-status ${statusClass(deploy.status, deploy.hanging)}`}>
        <StatusIcon
          status={deploy.status}
          running={deploy.running}
          hanging={deploy.hanging}
          latest={deploy.latest}/>
      </div>
      <div className="build-info">
        <span className="build-version">{deploy.version}</span>
        <span className="build-time">{dateTimes.format(deploy.time)}</span>
        {deploy.custom && <div className="build-custom" title={deploy.custom}>{deploy.custom}</div>}
      </div>
    </div>
  </a>
)

const StatusIcon = ({status, running, hanging, latest}) => {
  let iconType = statusIconClass(status, running, hanging)
  let rotateClass = running && !hanging ? "fa-spin" : ""
  let ageClass = latest ? "status-latest" : "status-older"
  let classes = `status-icon ${statusClass(status, hanging)} ${rotateClass} ${ageClass}`

  return <FontAwesomeIcon icon={iconType} className={classes}/>
}

const statusClass = (status, hanging) => {
  return `status-${hanging ? "HANGING" : status}`
}

const statusIconClass = (status, running, hanging) => {
  if (hanging) {
    return faClock
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
