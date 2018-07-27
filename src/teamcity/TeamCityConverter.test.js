import TeamCityConverter from './TeamCityConverter'

it('teamCityConversionTest', () => {
  let builds = {
    buildType: [
      {
        id: 'Releases_Betelgeuse_Dev',
        name: 'Deploy Dev',
        type: 'deployment',
        builds: {
          build: [
            {
              number: '1.0.2',
              status: 'SUCCESS',
              finishDate: "20180617T083210+0100",
              properties: {
                property: [
                  {
                    name: 'ENVIRONMENT',
                    value: 'DEV'
                  },
                  {
                    name: 'PROJECT',
                    value: 'Betelgeuse'
                  }
                ]
              }
            }
          ]
        }
      },
      {
        id: 'Releases_Betelgeuse_Test',
        name: 'Deploy Test',
        type: 'deployment',
        builds: {
          build: [
            {
              number: '1.0.1',
              status: 'SUCCESS',
              finishDate: "20180617T082834+0100",
              properties: {
                property: [
                  {
                    name: 'ENVIRONMENT',
                    value: 'TST'
                  },
                  {
                    name: 'PROJECT',
                    value: 'Betelgeuse'
                  }
                ]
              }
            }
          ]
        }
      },
      {
        id: 'Releases_Rigel_Dev',
        name: 'Deploy Dev',
        type: 'deployment',
        builds: {
          build: [
            {
              number: '1.0.1',
              status: 'SUCCESS',
              finishDate: "20180616T083737+0100",
              properties: {
                property: [
                  {
                    name: 'ENVIRONMENT',
                    value: 'DEV'
                  },
                  {
                    name: 'PROJECT',
                    value: 'Rigel'
                  }
                ]
              }
            }
          ]
        }
      }
    ]
  }
  
  let converter = new TeamCityConverter
  let results = converter.convert(builds)
  
  expect(results).toEqual([
    {
      name: "Betelgeuse",
      version: "1.0.2",
      environment: "DEV",
      time: new Date("2018-06-17T08:32:10"),
      status: "SUCCESS"
    },
    {
      name: "Betelgeuse",
      version: "1.0.1",
      environment: "TST",
      time: new Date("2018-06-17T08:28:34"),
      status: "SUCCESS"
    },
    {
      name: "Rigel",
      version: "1.0.1",
      environment: "DEV",
      time: new Date("2018-06-16T08:37:37"),
      status: "SUCCESS"
    }
  ]);
})
