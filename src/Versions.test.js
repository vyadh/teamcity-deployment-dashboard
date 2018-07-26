import Versions from './Versions';

/* Use maxVersion to sort a list of likely-seen versions for concise tests */
it ('compareVersionTest', () => {
  let versions = [
    '1',
    '2',
    '1.0',
    '1.1',
    '1.0.0',
    '1.0.1',
    '1.0.2',
    '1.1.0',
    '2.0.0',
    '1.0.1.4159',
    '1.0.1.4160',
    '1.9.0',
    '1.10.0',
    '2.34.5',
    '2.3.34',
    '2.3.34+123',
    '2.3.34+23'
  ]
  
  let result = versions.sort(Versions.compareVersion)

  expect(result).toEqual([
    '1',
    '1.0',
    '1.0.0',
    '1.0.1',
    '1.0.1.4159',
    '1.0.1.4160',
    '1.0.2',
    '1.1',
    '1.1.0',
    '1.9.0',
    '1.10.0',
    '2',
    '2.0.0',
    '2.3.34',
    '2.3.34+23',
    '2.3.34+123',
    '2.34.5'
  ])
})

it ('maxVersionTest with minor position change', () => {
  let versions = ['1.1', '1.0.1', '1.0.1.4159', '1.1.0+123']
  
  let result = Versions.maxVersion(versions)

  expect(result).toEqual('1.1.0+123')
})

it ('maxVersionTest with build number change', () => {
  let versions = ['1.0.1+1', '1.0.0', '1.0.1+4159', '1.0.1+123']
  
  let result = Versions.maxVersion(versions)

  expect(result).toEqual('1.0.1+4159')
})
