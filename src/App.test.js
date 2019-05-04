import React from 'react'
import ReactDOM from 'react-dom'
import App from './App'
import {createMemorySource} from "./memory/memorySource"

const config = {
  source: createMemorySource(),
  environments: [
    "DEV",
    "TST",
    "UAT",
    "PRD"
  ]
}

it('renders without crashing', () => {
  const div = document.createElement('div');
  ReactDOM.render(<App configuration={config}/>, div);
  ReactDOM.unmountComponentAtNode(div);
});
