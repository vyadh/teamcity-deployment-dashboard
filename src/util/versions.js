
export const maxVersion = versions => {
  return versions.reduce(
      (acc, current) => compareVersion(acc, current) > 0 ? acc : current)
}

export const compareVersion = (a, b) => {
  // Hat tip: https://stackoverflow.com/a/47159772
  let ver1 = a.split(/[.+]/).map(s => s.padStart(10, "0")).join('.')
  let ver2 = b.split(/[.+]/).map(s => s.padStart(10, "0")).join('.')
  return ver1 === ver2 ? 0 : (ver1 < ver2 ? -1 : 1)
}
