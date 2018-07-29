class Collections {

  static groupBy(array, byKey) {
    let reducer = (acc, current) => {
      let key = byKey(current)
      if (!acc[key]) {
        acc[key] = []
      }
      acc[key].push(current)
      return acc
    }
    return array.reduce(reducer, { })
  }

}

export default Collections
