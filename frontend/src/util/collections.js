
export const groupBy = (array, byKey) => {
  const reducer = (acc, current) => {
    let key = byKey(current)
    if (!acc[key]) {
      acc[key] = []
    }
    acc[key].push(current)
    return acc
  }

  return array.reduce(reducer, { })
}
