import * as releasesPerApp from './releasesPerApp'

it('releases are grouped by name, and the latest versions are marked as such', () => {
  let data = [
    {
      name: "Mercury",
      version: "2.1.0",
      environment: "DEV"
    },
    {
      name: "Mercury",
      version: "2.0.0",
      environment: "UAT"
    },
    {
      name: "Mercury",
      version: "2.0.0",
      environment: "PRD"
    },
    {
      name: "Venus",
      version: "3.0.1",
      environment: "DEV"
    },
    {
      name: "Venus",
      version: "3.0.1",
      environment: "TST"
    },
    {
      name: "Venus",
      version: "3.0.1",
      environment: "PRD"
    }
  ]

  let promise = fetch(data)

  expect(promise).resolves.toEqual(
    {
      Mercury: [
        {
          name: "Mercury",
          version: "2.1.0",
          environment: "DEV",
          latest: true
        },
        {
          name: "Mercury",
          version: "2.0.0",
          environment: "UAT",
          latest: false
        },
        {
          name: "Mercury",
          version: "2.0.0",
          environment: "PRD",
          latest: false
        }
      ],
      Venus: [
        {
          name: "Venus",
          version: "3.0.1",
          environment: "DEV",
          latest: true
        },
        {
          name: "Venus",
          version: "3.0.1",
          environment: "TST",
          latest: true
        },
        {
          name: "Venus",
          version: "3.0.1",
          environment: "PRD",
          latest: true
        }
      ]
    }
  )
})

const fetch = data => {
  let fetch = () => new Promise(resolve => resolve(data))
  let parse = data => data
  return releasesPerApp.fetch({fetch, parse})
}
