import SearchApps from './SearchApps'

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
  expect(SearchApps.filter('', data()))
      .toEqual(data())
})

it('filter narrows results', () => {
  expect(SearchApps.filter('ar', data()))
      .toEqual({ Earth: [], Mars: [] })

  expect(SearchApps.filter('tu', data()))
      .toEqual({ Saturn: [], Neptune: [] })
})

it('filter is case insensitive', () => {
  expect(SearchApps.filter('er', data()))
      .toEqual({ Mercury: [], Jupiter: [] })
})

it('filter works for single characters', () => {
  expect(SearchApps.filter('u', data()))
      .toEqual({ Mercury: [], Venus: [], Jupiter: [], Saturn: [], Uranus: [], Neptune: [] })
})
