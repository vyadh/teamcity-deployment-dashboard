import {format, isToday} from "./dateTimes"

it('time is today', () => {
  expect(isToday(new Date())).toBe(true)
})

it('time is not today', () => {
  expect(isToday(new Date('2018-07-28T08:30:00'))).toBe(false)
  expect(isToday(new Date('2018-07-30T08:30:00'))).toBe(false)
})

it('date-time is formatted to short string', () => {
  // Locale is US in tests
  expect(format('2018-07-28T08:30:00')).toBe('July 28, 2018')
})

it('date-time is formatted as time', () => {
  let date = new Date().toISOString().substring(0, 10)

  expect(format(date + 'T09:45:22')).toBe('9:45:22 AM')
})
