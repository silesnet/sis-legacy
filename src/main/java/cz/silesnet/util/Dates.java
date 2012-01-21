package cz.silesnet.util;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * User: der3k
 * Date: 26.3.11
 * Time: 16:39
 */
public class Dates {

  public static Calendar calendarWithZeroTimeFrom(final Date due) {
    Calendar calendar = calendarFrom(due);
    setTimeToZero(calendar);
    return calendar;
  }

  public static Calendar calendarFrom(final Date due) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(due);
    calendar.setLenient(false);
    return calendar;
  }

  public static void setTimeToZero(final Calendar calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
  }

  public static Calendar firstDayOfMonth(Date date) {
    Calendar first = calendarFor(date);
    first.set(Calendar.DAY_OF_MONTH, 1);
    return first;
  }

  public static int getMonth(Date date) {
    return calendarFor(date).get(Calendar.MONTH) + 1;
  }

  public static Calendar calendarFor(Date date) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  public static int daysOfMonth(final Date date) {
    return new DateTime(date).dayOfMonth().getMaximumValue();
  }

}
