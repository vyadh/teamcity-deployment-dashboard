class InMemorySource {

  fetch() {
    return new Promise(resolve => {
      resolve(InMemorySource.fetchSync())
    })
  }

  static fetchSync() {
    return [
      {
        name: "Mercury",
        version: "1.3.3",
        environment: "DEV",
        time: "2018-06-09T21:58:12",
        status: "SUCCESS"
      },
      {
        name: "Mercury",
        version: "1.3.3",
        environment: "TST",
        time: "2018-06-09T21:58:12",
        status: "SUCCESS"
      },
      {
        name: "Mercury",
        version: "1.3.3",
        environment: "UAT",
        time: "2018-06-09T21:58:12",
        status: "SUCCESS"
      },
      {
        name: "Mercury",
        version: "1.3.3",
        environment: "PRD",
        time: "2018-06-09T21:58:12",
        status: "SUCCESS"
      },
      {
        name: "Venus",
        version: "17.0.1",
        environment: "PRD",
        time: "2018-06-04T22:20:33",
        status: "SUCCESS"
      },
      {
        name: "Venus",
        version: "17.0.2",
        environment: "TST",
        time: new Date().toISOString(),
        status: "RUNNING"
      },
      {
        name: "Venus",
        version: "17.0.1",
        environment: "UAT",
        time: "2018-06-01T22:20:33",
        status: "SUCCESS"
      },
      {
        name: "Venus",
        version: "17.0.2",
        environment: "DEV",
        time: "2018-06-01T22:20:33",
        status: "SUCCESS"
      },
      {
        name: "Earth",
        version: "5.3.0",
        environment: "DEV",
        time: "2018-06-04T21:59:51",
        status: "FAILURE"
      },
      {
        name: "Earth",
        version: "5.2.1",
        environment: "TST",
        time: "2018-06-04T21:59:51",
        status: "SUCCESS"
      },
      {
        name: "Earth",
        version: "5.2.1",
        environment: "UAT",
        time: "2018-06-04T21:59:51",
        status: "SUCCESS"
      },
      {
        name: "Earth",
        version: "5.2.1",
        environment: "PRD",
        time: "2018-06-04T21:59:51",
        status: "SUCCESS"
      },
      {
        name: "Uranus",
        version: "11.2.113",
        environment: "DEV",
        time: "2018-06-04T22:20:33",
        status: "SUCCESS"
      },
      {
        name: "Uranus",
        version: "11.2.113",
        environment: "TST",
        time: "2018-06-04T22:20:33",
        status: "SUCCESS"
      },
      {
        name: "Uranus",
        version: "11.2.112",
        environment: "PRD",
        time: "2018-06-04T22:20:33",
        status: "OTHER"
      },
      {
        name: "Ceres",
        version: "1.0.0",
        environment: "DEV",
        time: "2018-06-16T09:54:00",
        status: "SUCCESS"
      },
      {
        name: "Ceres",
        version: "1.0.0",
        environment: "TST",
        time: new Date().toISOString(),
        status: "RUNNING"
      },
      ...InMemorySource.randomDeployment("Mars"),
      ...InMemorySource.randomDeployment("Jupiter"),
      ...InMemorySource.randomDeployment("Saturn"),
      ...InMemorySource.randomDeployment("Neptune"),
      ...InMemorySource.randomDeployment("Pluto"),
      ...InMemorySource.randomDeployment("Haumea"),
      ...InMemorySource.randomDeployment("Makemake"),
      ...InMemorySource.randomDeployment("Eris"),
      ...InMemorySource.randomDeployment("Quaoar"),
      ...InMemorySource.randomDeployment("Sedna"),
      ...InMemorySource.randomDeployment("Orcas")
    ]
  }
  
  static randomDeployment(name) {
    let envs = ["DEV", "TST", "UAT", "PRD"]
    let ver = (max) => Math.floor(Math.random() * max)
    let major = ver(9) + 1
    let minor = ver(20)
    let bugfix = [ver(3), ver(3), ver(3), ver(3)].sort().reverse()
    let failure = () => ver(15) < 2
    
    return [0, 1, 2, 3].map(i => ({
      name: name,
      version: major + "." + minor + "." + bugfix[i],
      environment: envs[i],
      time: "2018-06-16T09:54:00",
      status: failure() ? "FAILURE" : "SUCCESS"
    }))
  }
    
}

export default InMemorySource
