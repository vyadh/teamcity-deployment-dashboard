import React from 'react'
import ReactDOM from 'react-dom'
import { act } from 'react-dom/test-utils';
import App from './App'
import {createMemorySource} from "./sources/memory/memorySource"

const config = {
  source: createMemorySource(),
  environments: [
    "DEV",
    "TST",
    "UAT",
    "PRD"
  ]
}

// Hack to silence a warning that we'll get until react fixes this:
//   https://github.com/facebook/react/pull/14853
// See: https://github.com/testing-library/react-testing-library/issues/281
const originalError = console.error
beforeAll(() => {
  console.error = (...args) => {
    if (/Warning.*not wrapped in act/.test(args[0])) {
      return
    }
    originalError.call(console, ...args)
  }
})

it('renders without crashing', () => {
  const div = document.createElement('div');
  act(() => {
    ReactDOM.render(<App configuration={config}/>, div);
    ReactDOM.unmountComponentAtNode(div);
  })
});
