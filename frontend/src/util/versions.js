
/*
 * Returns the lexicographically-latest version of the array specified.
 * Assumes the first word is the SemVer 2 version string to compare and return.
 * Ignores any subsequent words, assuming it is non-relevant data.
 */
export const maxVersion = versions => {
  let firstWords = versions.map(firstWord)
  return firstWords.reduce(
      (acc, current) => compareVersion(acc, current) > 0 ? acc : current)
}

export const firstWord = sentence => {
  let index = sentence.indexOf(" ")
  let end = index === -1 ? sentence.length : index
  return sentence.slice(0, end)
}

export const compareVersion = (a, b) => {
  // Hat tip: https://stackoverflow.com/a/47159772
  let ver1 = a.split(/[.+]/).map(s => s.padStart(10, "0")).join('.')
  let ver2 = b.split(/[.+]/).map(s => s.padStart(10, "0")).join('.')
  return ver1 === ver2 ? 0 : (ver1 < ver2 ? -1 : 1)
}
