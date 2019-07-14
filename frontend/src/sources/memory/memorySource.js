
export const createMemorySource = () => {
  return {
    fetch: () => {
      console.log("Fetching deployments...")

      return new Promise(resolve => {
        resolve(data())
      })
    }
  }
}

const data = () => {
  return {
    environments: environments,
    refreshSecs: "",
    deploys: deploys()
  }
}

const environments = ["Development", "Test", "UAT", "Production"]

const deploys = () => {
  return [
    {
      name: "Mercury",
      version: "1.3.3",
      environment: "Development",
      time: "2019-06-09T21:58:12",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Mercury",
      version: "1.3.3",
      environment: "Test",
      time: "2019-06-09T21:58:12",
      status: "FAILURE",
      running: true,
      personal: false
    },
    {
      name: "Mercury",
      version: "1.3.3",
      environment: "UAT",
      time: "2019-06-09T21:58:12",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Mercury",
      version: "1.3.3",
      environment: "Production",
      time: "2019-06-09T21:58:12",
      status: "SUCCESS",
      running: false
    },
    {
      name: "Haumea",
      version: "2.1.3",
      environment: "Development",
      time: "2019-06-03T20:12:03",
      status: "FAILURE",
      running: true,
      personal: true
    },
    {
      name: "Haumea",
      version: "2.1.3",
      environment: "Test",
      time: "2019-06-05T18:33:12",
      status: "SUCCESS",
      running: true,
      personal: true
    },
    {
      name: "Haumea",
      version: "2.1.3",
      environment: "UAT",
      time: "2019-06-10T08:48:02",
      status: "FAILURE",
      running: false,
      personal: true
    },
    {
      name: "Haumea",
      version: "2.1.3",
      environment: "Production",
      time: "2019-06-14T09:57:04",
      status: "SUCCESS",
      running: false,
      personal: true
    },
    {
      name: "Venus",
      version: "17.0.1",
      environment: "Production",
      time: "2019-06-04T22:20:33",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Venus",
      version: "17.0.2",
      environment: "Test",
      time: new Date().toISOString(),
      status: "SUCCESS",
      running: true,
      personal: false
    },
    {
      name: "Venus",
      version: "17.0.1",
      environment: "UAT",
      time: "2019-06-01T22:20:33",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Venus",
      version: "17.0.2",
      environment: "Development",
      time: "2019-06-01T22:20:33",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Earth",
      version: "5.3.0",
      environment: "Development",
      time: "2019-06-04T21:59:51",
      status: "FAILURE",
      running: false,
      personal: false
    },
    {
      name: "Earth",
      version: "5.2.1",
      environment: "Test",
      time: "2019-06-04T21:59:51",
      status: "SUCCESS",
      running: false,
      personal: true
    },
    {
      name: "Earth",
      version: "5.2.1",
      environment: "UAT",
      time: "2019-06-04T21:59:51",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Earth",
      version: "5.2.1",
      environment: "Production",
      time: "2019-06-04T21:59:51",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "The Winter-Morning Star",
      version: "1.0.0",
      environment: "Test",
      time: "2019-07-13T12:00:00",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Uranus",
      version: "11.2.113",
      environment: "Development",
      time: "2019-06-04T22:20:33",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Uranus",
      version: "11.2.113",
      environment: "Test",
      time: "2019-06-04T22:20:33",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Uranus",
      version: "11.2.112",
      environment: "Production",
      time: "2019-06-04T22:20:33",
      status: "OTHER",
      running: false,
      personal: false
    },
    {
      name: "Ceres",
      version: "1.0.0",
      environment: "Development",
      time: "2019-06-16T09:54:00",
      status: "SUCCESS",
      running: false,
      personal: false
    },
    {
      name: "Ceres",
      version: "1.0.0",
      environment: "Test",
      time: new Date().toISOString(),
      status: "SUCCESS",
      running: true,
      personal: false
    },
    ...randomDeployment("Mars"),
    ...randomDeployment("Jupiter"),
    ...randomDeployment("Saturn"),
    ...randomDeployment("Neptune"),
    ...randomDeployment("Pluto"),
    ...randomDeployment("plutino"),
    ...randomDeployment("Makemake"),
    ...randomDeployment("Eris"),
    ...randomDeployment("Quaoar"),
    ...randomDeployment("Sedna"),
    ...randomDeployment("Orcas")
  ]
}

const randomDeployment = name => {
  let envs = ["Development", "Test", "UAT", "Production"]
  let ver = (max) => Math.floor(Math.random() * max)
  let major = ver(9) + 1
  let minor = ver(20)
  let bugfix = [ver(3), ver(3), ver(3), ver(3)].sort().reverse()
  let failure = () => ver(15) < 2

  return [0, 1, 2, 3].map(i => {
    let version = major + "." + minor + "." + bugfix[i]

    return {
      name: name,
      version: version,
      environment: envs[i],
      time: "2019-06-16T09:54:00",
      status: failure() ? "FAILURE" : "SUCCESS",
      running: false,
      personal: false,
      link: `http://localhost/${name}/${version}/${envs[i]}`
    }
  })
}
