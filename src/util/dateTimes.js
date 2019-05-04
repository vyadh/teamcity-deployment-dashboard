
export const format = isoDateTime => {
  let date = new Date(isoDateTime);
  if (isToday(date)) {
    return date.toLocaleTimeString();
  } else {
    return date.toLocaleString(
        "en-gb", {day: "numeric", month: "long", year: "numeric"});
  }
}

export const isToday = dateTime => {
  return dateTime.toDateString() === new Date().toDateString();
}
