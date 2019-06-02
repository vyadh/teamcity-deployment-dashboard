import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import registerServiceWorker from './registerServiceWorker';
import configuration from './configuration'

ReactDOM.render(
    <App configuration={configuration}/>,
    document.getElementById('deploys'));

if (!configuration.embedded) {
  registerServiceWorker();
}
