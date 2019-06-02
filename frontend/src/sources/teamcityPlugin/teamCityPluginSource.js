
export const createTeamCityPluginSource = () => {
  return {
    fetch: () => get("/app/deploys/")
  }
}

const get = urlPrefix => {
  let id = projectId(window.location.search)
  let url = `${urlPrefix}/${id}`

  console.log(`Fetching '${id}' deployments from TeamCity...`)

  return fetch(url, {
        headers: {
          'Accept': 'application/json'
        }
      })
      .then(extractJson)
      .then(json => json.deploys)
}

export const projectId = (search) => {
  let params = new URLSearchParams(search)
  let id = params.get("projectId")
  if (id === null) {
    throw new Error(`Could not find project id in: ${search}`)
  } else {
    return id
  }
}

export const extractJson = response => {
  if (response.ok) {
    return response.json()
  } else {
    throw new Error('Could not fetch from URL')
  }
}
