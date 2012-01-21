package cz.silesnet.model.enums;

import cz.silesnet.model.Period;
import cz.silesnet.model.invoice.Percent;
import cz.silesnet.util.Dates;
import org.joda.time.DurationFieldType;
import org.joda.time.PeriodType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

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

  private static final int PRECISION = 16;
  private static final MathContext MATH = new MathContext(PRECISION, RoundingMode.HALF_UP);
  private static final DurationFieldType[] WEEKS_DAYS_FIELD_TYPES = new DurationFieldType[]{DurationFieldType.weeks(), DurationFieldType.days()};
  private static final PeriodType WEEKS_DAYS_TYPE = PeriodType.forFields(WEEKS_DAYS_FIELD_TYPES);
  private static final BigDecimal DAYS_OF_WEEK = BigDecimal.valueOf(7);

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
        Date day = Dates.calendarWithZeroTimeFrom(due).getTime();
        period = new Period(day, day);
        break;
      case WEEKLY:
        period = weeksFrequencyPeriodFor(due);
        break;
      default:
        period = monthsFrequencyPeriodFor(due);
    }
    return period;
  }

  public Percent percentageFor(final Period period) {
    if (period != Period.NONE && !period.isCompleteAndValid())
      throw new IllegalArgumentException("period has to be complete and valid");
    Percent percentage;
    switch (this) {
      case ONE_TIME:
        percentage = oneTimeFrequencyPercentageFor(period);
        break;
      case DAILY:
        percentage = daysFrequencyPercentageFor(period);
        break;
      case WEEKLY:
        percentage = weeksFrequencyPercentageFor(period);
        break;
      default:
        percentage = monthsFrequencyPercentageFor(period);
    }
    return percentage;
  }

  private Percent oneTimeFrequencyPercentageFor(final Period period) {
    if (period == Period.NONE)
      return Percent.ZERO;
    return Percent.HUNDRED;
  }

  private Percent daysFrequencyPercentageFor(final Period period) {
    if (period == Period.NONE)
      return Percent.ZERO;
    return Percent.rate(period.days() * 100);
  }

  private Percent weeksFrequencyPercentageFor(final Period period) {
    if (period == Period.NONE)
      return Percent.ZERO;

    org.joda.time.Period weeksAndDays = period.toJodaPeriod(WEEKS_DAYS_TYPE);
    Percent weeksPercent = Percent.rate(weeksAndDays.getWeeks() * 100);

    BigDecimal days = BigDecimal.valueOf(weeksAndDays.getDays());
    BigDecimal daysRate = days.divide(DAYS_OF_WEEK, MATH);

    return weeksPercent.plus(rateToPercent(daysRate));
  }

  private Percent monthsFrequencyPercentageFor(final Period period) {
    if (period == Period.NONE)
      return Percent.ZERO;

    if (period.isWithinOneMonth()) {
      BigDecimal daysOfMonth = BigDecimal.valueOf(Dates.daysOfMonth(period.getFrom()));
      BigDecimal periodDays = BigDecimal.valueOf(period.days());
      BigDecimal monthlyRate = periodDays.divide(daysOfMonth, MATH);
      BigDecimal rate = adjustRateToFrequencyMonths(monthlyRate);
      return rateToPercent(rate);
    }

    if (period.isWholeMonthsOnly()) {
      BigDecimal periodMonths = BigDecimal.valueOf(period.months());
      BigDecimal rate = adjustRateToFrequencyMonths(periodMonths);
      return rateToPercent(rate);
    }

    Period leadingDays = period.daysLeadingToFirstOfNextMonth();
    Period trailingDays = period.daysTrailingAfterLastOfPreviousMonth();

    Period months = period.duplicate();
    months.adjustThisPeriodStartBy(leadingDays);
    months.adjustThisPeriodEndBy(trailingDays);

    return monthsFrequencyPercentageFor(leadingDays)
        .plus(monthsFrequencyPercentageFor(months))
        .plus(monthsFrequencyPercentageFor(trailingDays));
  }

  private BigDecimal adjustRateToFrequencyMonths(final BigDecimal rate) {
    return rate.divide(thisFrequencyMonths(), MATH);
  }

  private BigDecimal thisFrequencyMonths() {
    return BigDecimal.valueOf(this.getMonths());
  }

  private Percent rateToPercent(final BigDecimal rate) {
    int integerRate = rate.movePointRight(2)
        .setScale(0, MATH.getRoundingMode())
        .intValue();
    return Percent.rate(integerRate);
  }

  private Period weeksFrequencyPeriodFor(final Date due) {
    Calendar from = Dates.calendarWithZeroTimeFrom(due);
    while (from.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
      from.add(Calendar.DAY_OF_WEEK, -1);

    Calendar to = (Calendar) from.clone();
    to.add(Calendar.DAY_OF_WEEK, to.getActualMaximum(Calendar.DAY_OF_WEEK) - 1);

    return new Period(from.getTime(), to.getTime());
  }

  private Period monthsFrequencyPeriodFor(final Date due) {
    Calendar from = Dates.calendarWithZeroTimeFrom(due);
    int startingMonth = frequencyStartingMonth(from.get(Calendar.MONTH));
    from.set(Calendar.MONTH, startingMonth);
    from.set(Calendar.DAY_OF_MONTH, 1);

    Calendar to = (Calendar) from.clone();
    to.add(Calendar.MONTH, getMonths());
    to.add(Calendar.DAY_OF_MONTH, -1);

    return new Period(from.getTime(), to.getTime());
  }

  private int frequencyStartingMonth(final int month) {
    int from = month;
    while (from % getMonths() != 0)
      from--;
    return from;
  }

}