import {groupBy} from "./collections"

it('groupByWhenEmptyTest', () => {
  expect(groupBy([], item => item.type))
    .toEqual({ });
})

it('groupByWhenEmptyTest', () => {
  expect(groupBy([], item => item.type))
    .toEqual({ });
})

it('groupByWithOneItemTest', () => {
  let planets = [{ type: "rocky" }]
  
  let results = groupBy(planets, item => item.type)
  
  expect(results).toEqual({ 'rocky': [{ type: "rocky" }] });
})

it('groupByWithTwoDifferentItemsTest', () => {
  let planets = [
    { id: 1, type: "rocky" },
    { id: 2, type: "rocky" }
  ]
  
  let results = groupBy(planets, item => item.type)
  
  expect(results).toEqual(
    { 'rocky': [
      { id: 1, type: "rocky" },
      { id: 2, type: "rocky" }]
    }
  );
})

it('groupByWithSameKeyTest', () => {
  let planets = [
    { id: 1, type: "rocky" },
    { id: 3, type: "gaseous" }
  ]
  
  let results = groupBy(planets, item => item.type)
  
  expect(results).toEqual({
    'rocky': [
      { id: 1, type: "rocky" }
    ],
    'gaseous': [
      { id: 3, type: "gaseous" }
    ]
  });
})

it('groupByWithMixedItemsTest', () => {
  let planets = [
    { id: 1, type: "rocky" },
    { id: 2, type: "rocky" },
    { id: 3, type: "gaseous" }
  ]
  
  let results = groupBy(planets, item => item.type)

  expect(results).toEqual({
    'rocky': [
      { id: 1, type: "rocky" },
      { id: 2, type: "rocky" },
    ],
    'gaseous': [
      { id: 3, type: "gaseous" }
    ]
  });
})
