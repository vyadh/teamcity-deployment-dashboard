import {extractJson, projectId} from './teamCityPluginSource'

it('project id was found', () => {
  let search = "?param1=value1&projectId=Amiga&param2=value2"

  let result = projectId(search)

  expect(result).toEqual("Amiga")
})

it('project id was not found', () => {
  let search = "?param1=value1&param2=value2"

  let call = () => projectId(search)

  expect(call).toThrow()
})

it('extract JSON when response OK status', () => {
  let response = {
    ok: true,
    json: () => JSON.parse('{ "key": "value" }')
  }

  let result = extractJson(response)

  expect(result.key).toEqual("value")
})

it('error when response status is not OK', () => {
  let response = {
    ok: false
  }

  let call = () => extractJson(response)

  expect(call).toThrow()
})
