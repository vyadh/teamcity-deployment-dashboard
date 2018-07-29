export default class DateTimes {

  static format(isoDateTime) {
    let date = new Date(isoDateTime);
    if (DateTimes.isToday(date)) {
      return date.toLocaleTimeString();
    } else {
      return date.toLocaleString(
          "en-gb", {day: "numeric", month: "long", year: "numeric"});
    }
  }

  static isToday(dateTime) {
    return dateTime.toDateString() === new Date().toDateString();
  }

}
