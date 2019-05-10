import {filterApps} from "./searchApps"

const data = () => ({
  Mercury: [],
  Venus: [],
  Earth: [],
  Mars: [],
  Jupiter: [],
  Saturn: [],
  Uranus: [],
  Neptune: []
})

it('empty filter does nothing', () => {
  expect(filterApps('', data()))
      .toEqual(data())
})

it('filter narrows results', () => {
  expect(filterApps('ar', data()))
      .toEqual({ Earth: [], Mars: [] })

  expect(filterApps('tu', data()))
      .toEqual({ Saturn: [], Neptune: [] })
})

it('filter is case insensitive', () => {
  expect(filterApps('er', data()))
      .toEqual({ Mercury: [], Jupiter: [] })
})

it('filter works for single characters', () => {
  expect(filterApps('u', data()))
      .toEqual({ Mercury: [], Venus: [], Jupiter: [], Saturn: [], Uranus: [], Neptune: [] })
})
