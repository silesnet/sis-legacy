package cz.silesnet.model.enums;

import cz.silesnet.model.Period;
import cz.silesnet.model.invoice.Percent;
import org.joda.time.DateTime;
import org.joda.time.PeriodType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Frequency enumerator for setting servicess/billing periods.
 *
 * @author Richard Sikora
 */
public enum Frequency implements EnumPersistenceMapping<Frequency> {
  ONE_TIME(10, "enum.frequency.one_time", 0),
  DAILY(20, "enum.frequency.daily", 0),
  WEEKLY(30, "enum.frequency.weekly", 0),
  MONTHLY(40, "enum.frequency.monthly", 1),
  Q(50, "enum.frequency.q", 3),
  QQ(60, "enum.frequency.qq", 6),
  ANNUAL(70, "enum.frequency.annual", 12);

  private int fId;

  private String fName;

  private int fMonths;

  // has to declare it by hand
  private static ReverseEnumMap<Frequency> sReverseMap = new ReverseEnumMap<Frequency>(
      Frequency.class);

  private Frequency(int id, String name, int months) {
    fId = id;
    fName = name;
    fMonths = months;
  }

  public String getName() {
    return fName;
  }

  public String toString() {
    return getName();
  }

  public int getId() {
    return fId;
  }

  public Frequency valueOf(int id) {
    return sReverseMap.get(id);
  }

  public int getMonths() {
    return fMonths;
  }

  public Period periodFor(final Date due) {
    Period period;
    switch (this) {
      case ONE_TIME:
      case DAILY:
        Date day = calendarWithZeroTimeFrom(due).getTime();
        period = new Period(day, day);
        break;
      case WEEKLY:
        period = weeklyFrequencyPeriodFor(due);
        break;
      default:
        period = monthsFrequencyPeriodFor(due);
    }
    return period;
  }

  public Percent percentageFor(final Period period) {
    if (period == Period.NONE)
      return Percent.ZERO;

    if (period.isWithinOneMonth()) {
      BigDecimal daysOfMonth = BigDecimal.valueOf(daysOfMonth(period.getFrom()));
      BigDecimal periodDays = BigDecimal.valueOf(periodDays(period));
      BigDecimal rate = periodDays.movePointRight(2).divide(daysOfMonth, RoundingMode.HALF_UP);
      return Percent.rate(rate.intValue());
    }

    if (period.isWholeMonthsOnly()) {
      return Percent.rate(getMonths(period) * 100);
    }

    Period leadingDays = period.daysLeadingToFirstOfNextMonth();
    Period trailingDays = period.daysTrailingAfterLastOfPreviousMonth();

    Period months = period.duplicate();
    months.adjustThisPeriodStartBy(leadingDays);
    months.adjustThisPeriodEndBy(trailingDays);

    return percentageFor(leadingDays)
        .plus(percentageFor(months))
        .plus(percentageFor(trailingDays));
  }

  private int periodDays(final Period period) {
    org.joda.time.Period jodaPeriod = toJodaPeriod(period, PeriodType.days());
    return jodaPeriod.getDays();
  }

  private int getMonths(final Period period) {
    return toJodaPeriod(period, PeriodType.months()).getMonths();
  }

  private org.joda.time.Period toJodaPeriod(final Period period, final PeriodType type) {
    DateTime from = new DateTime(period.getFrom());
    DateTime to = new DateTime(period.getTo()).plusDays(1);
    return new org.joda.time.Period(from, to, type);
  }

  private int daysOfMonth(final Date date) {
    return new DateTime(date).dayOfMonth().getMaximumValue();
  }

  private Period weeklyFrequencyPeriodFor(final Date due) {
    Calendar from = calendarWithZeroTimeFrom(due);
    while (from.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
      from.add(Calendar.DAY_OF_WEEK, -1);

    Calendar to = (Calendar) from.clone();
    to.add(Calendar.DAY_OF_WEEK, to.getActualMaximum(Calendar.DAY_OF_WEEK) - 1);

    return new Period(from.getTime(), to.getTime());
  }

  private Period monthsFrequencyPeriodFor(final Date due) {
    Calendar from = calendarWithZeroTimeFrom(due);
    int startingMonth = frequencyStartingMonth(from.get(Calendar.MONTH));
    from.set(Calendar.MONTH, startingMonth);
    from.set(Calendar.DAY_OF_MONTH, 1);

    Calendar to = (Calendar) from.clone();
    to.add(Calendar.MONTH, getMonths());
    to.add(Calendar.DAY_OF_MONTH, -1);

    return new Period(from.getTime(), to.getTime());
  }

  private Calendar calendarWithZeroTimeFrom(final Date due) {
    Calendar calendar = calendarFrom(due);
    setTimeToZero(calendar);
    return calendar;
  }

  private Calendar calendarFrom(final Date due) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(due);
    calendar.setLenient(false);
    return calendar;
  }

  private void setTimeToZero(final Calendar calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
  }

  private int frequencyStartingMonth(final int month) {
    int from = month;
    while (from % getMonths() != 0)
      from--;
    return from;
  }

}