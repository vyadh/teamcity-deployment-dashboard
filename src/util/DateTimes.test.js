import DateTimes from './DateTimes';

it('time is today', () => {
  expect(DateTimes.isToday(new Date('2018-07-29T08:30:00'))).toBe(true)
})

it('time is not today', () => {
  expect(DateTimes.isToday(new Date('2018-07-28T08:30:00'))).toBe(false)
  expect(DateTimes.isToday(new Date('2018-07-30T08:30:00'))).toBe(false)
})

it('date-time is formatted to short string', () => {
  // Locale is US in tests
  expect(DateTimes.format('2018-07-28T08:30:00')).toBe('July 28, 2018')
})

it('date-time is formatted as time', () => {
  let date = new Date().toISOString().substring(0, 10)

  expect(DateTimes.format(date + 'T09:45:22')).toBe('9:45:22 AM')
})
