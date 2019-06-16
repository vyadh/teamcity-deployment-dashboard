
/* Default (non-test) formatting in user's locale. */
const defaultDateFormat = new Intl.DateTimeFormat(
  'default', { day: "numeric", month: "long", year: "numeric" })
const defaultTimeFormat = new Intl.DateTimeFormat(
  'default', { hour: 'numeric', minute: 'numeric', second: 'numeric' })

export const format = (isoDateTime, dateFormat, timeFormat) => {
  let date = new Date(isoDateTime);
  if (isToday(date)) {
    return timeFormatter(dateFormat).format(date);
  } else {
    return dateFormatter(timeFormat).format(date)
  }
}

const dateFormatter = (formatter) => {
  return formatter ? formatter : defaultDateFormat
}

const timeFormatter = (formatter) => {
  return formatter ? formatter : defaultTimeFormat
}

export const isToday = dateTime => {
  return dateTime.toDateString() === new Date().toDateString();
}
