import {format, isToday} from "./dateTimes"

const dateFormatter = new Intl.DateTimeFormat(
  'en-GB', { day: "numeric", month: "long", year: "numeric" })
const timeFormatter = new Intl.DateTimeFormat(
  'en-GB', { hour: 'numeric', minute: 'numeric', second: 'numeric' })

it('time is today', () => {
  expect(isToday(new Date())).toBe(true)
})

it('time is not today', () => {
  expect(isToday(new Date('2019-06-28T08:30:00'))).toBe(false)
})

it('date-time is formatted to short string', () => {
  // Locale is US in tests
  expect(format('2019-06-28T08:30:00', dateFormatter)).toBe('June 28, 2019')
})

it('date-time is formatted as time', () => {
  let date = new Date().toISOString().substring(0, 10)

  expect(format(date + 'T09:45:22', timeFormatter)).toBe('9:45:22 AM')
})

it('time is formatted to the correct time zone', () => {
  let date = new Date().toISOString().substring(0, 10)
  let timeToday = date + 'T18:30:00Z' // As from server in UTC

  expect(format(timeToday, timeFormatter)).toBe('7:30:00 PM') // as BST
})
