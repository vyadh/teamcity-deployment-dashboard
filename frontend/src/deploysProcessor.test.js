import * as deploys from './deploysProcessor'

it('deploys are grouped by name, and the latest versions are marked as such', () => {
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

  let deploysPerApp = deploys.groupPerApp(data)

  expect(deploysPerApp).toEqual(
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

it('only the first word is used in latest calculation', () => {
  let data = [
    {
      name: "Mercury",
      version: "1.2.3+45 ijk",
      environment: "DEV"
    },
    {
      name: "Mercury",
      version: "1.2.3+45 xyz",
      environment: "TST"
    },
    {
      name: "Mercury",
      version: "1.2.3+45 abc",
      environment: "UAT"
    },
    {
      name: "Mercury",
      version: "1.2.3+21 aaa",
      environment: "PRD"
    }
  ]

  let deploysPerApp = deploys.groupPerApp(data)

  expect(deploysPerApp).toEqual(
      {
        Mercury: [
          {
            name: "Mercury",
            version: "1.2.3+45 ijk",
            environment: "DEV",
            latest: true
          },
          {
            name: "Mercury",
            version: "1.2.3+45 xyz",
            environment: "TST",
            latest: true
          },
          {
            name: "Mercury",
            version: "1.2.3+45 abc",
            environment: "UAT",
            latest: true
          },
          {
            name: "Mercury",
            version: "1.2.3+21 aaa",
            environment: "PRD",
            latest: false
          }
        ]
      }
  )
})
