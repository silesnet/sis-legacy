package cz.silesnet.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.PeriodType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.util.Calendar.*;

/**
 * Class to hold time periods.
 *
 * @author Richard Sikora
 */
public class Period implements HistoricToString, Serializable {
  private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//  private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

  public static final Period NONE = new Period() {
    @Override
    public String toString() {
      return "[ , ]";
    }
  };

  private static final long serialVersionUID = -2754636726635894230L;

  protected final Log log = LogFactory.getLog(getClass());

  // ~ Instance fields
  // --------------------------------------------------------

  private Date fFrom;

  private Date fTo;

  // ~ Constructors
  // -----------------------------------------------------------

  public Period() {
    super();
  }


  /**
   * @param from
   * @param to
   */
  public Period(Date from, Date to) {
    super();
    setFrom(from);
    setTo(to);
  }

  // ~ Methods
  // ----------------------------------------------------------------

  // FIXME after JVM upgrade can remove it
  // just for Java 1.5 bug wiht Date and Timestamp
  // see
  // http://bugs.sun.com/bugdatabase/view_bug.do;jsessionid=e51f4fd13c55374ac47b38c5cc1e9:WuuT?bug_id=5103041

  private Date sanate(Date d) {
    if (d instanceof Timestamp)
      return new Date(d.getTime());
    else
      return d;
  }

  public void setFrom(Date from) {
    fFrom = sanate(from);
  }

  public Date getFrom() {
    return fFrom;
  }

  public void setTo(Date to) {
    fTo = sanate(to);
  }

  public Date getTo() {
    return fTo;
  }

  public String getHistoricToString() {
    return getPeriodString();
  }

  public String getPeriodString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    StringBuffer periodStr = new StringBuffer();
    if (getFrom() != null)
      periodStr.append(dateFormat.format(getFrom()));
    else
      periodStr.append("<");

    periodStr.append("-");
    if (getTo() != null)
      periodStr.append(dateFormat.format(getTo()));
    else
      periodStr.append(">");

    return periodStr.toString();
  }

  public String getPeriodShortString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
    String periodStr = null;
    if (getTo() != null)
      periodStr = dateFormat.format(getTo());
    else
      periodStr = dateFormat.format(getFrom());
    return periodStr;
  }

  public boolean isValid() {
    // if begining and end are null period is invalid
    if (getFrom() == null && getTo() == null)
      return false;
    if ((getFrom() != null) && (getTo() != null)
        && (getTo().compareTo(getFrom()) < 0))
      return false;
    return true;
  }

  public boolean isComplete() {
    return (getFrom() != null) && (getTo() != null)
        && (getFrom().compareTo(getTo()) <= 0) ? true : false;
  }

  public boolean isCompleteAndValid() {
    if (getFrom() == null || getTo() == null)
      return false;
    return !getFrom().after(getTo());
  }

  public boolean contains(Date date) {
    if (date == null)
      throw new NullPointerException();
    if (!isValid())
      throw new IllegalStateException();
    if (isComplete()) {
      return (getFrom().compareTo(date) <= 0)
          && (date.compareTo(getTo()) <= 0) ? true : false;
    } else {
      return ((getFrom() != null) && (getFrom().compareTo(date) <= 0))
          || ((getTo() != null) && (date.compareTo(getTo()) <= 0)) ? true
          : false;
    }
  }

  public Period intersection(Period p) {
    if (p == null)
      throw new NullPointerException();
    if (!p.isValid())
      throw new IllegalArgumentException();
    if (!isValid())
      throw new IllegalStateException();

    Period result = new Period(maxFrom(getFrom(), p.getFrom()), minTo(
        getTo(), p.getTo()));
    return result.isValid() ? result : null;
  }

  private Date minTo(Date d1, Date d2) {
    if (d1 == null && d2 == null)
      return null;
    if (d1 != null && d2 != null)
      if (d1.compareTo(d2) < 0)
        return d1;
      else
        return d2;
    return d1 != null ? d1 : d2;
  }

  private Date maxFrom(Date d1, Date d2) {
    if (d1 == null && d2 == null)
      return null;
    if (d1 != null && d2 != null)
      if (d1.compareTo(d2) > 0)
        return d1;
      else
        return d2;
    return d1 != null ? d1 : d2;
  }

  /**
   * Computes float amout of months in period. Full mont == 1, two months == 2
   * ect.
   *
   * @return months amount
   */
  public float getMonthsRate() {
    if (!isComplete())
      throw new IllegalStateException();
    if (log.isDebugEnabled())
      log.debug("Period: " + getPeriodString());

    // initialize vars
    Calendar cFrom = new GregorianCalendar();
    Calendar cTo = new GregorianCalendar();
    cFrom.setTime(getFrom());
    cTo.setTime(getTo());
    int fDay = cFrom.get(Calendar.DAY_OF_MONTH);
    int fMonth = (cFrom.get(Calendar.YEAR) * 12)
        + (cFrom.get(Calendar.MONTH) + 1);
    int fMonthDays = cFrom.getActualMaximum(Calendar.DAY_OF_MONTH);
    int tDay = cTo.get(Calendar.DAY_OF_MONTH);
    int tMonth = (cTo.get(Calendar.YEAR) * 12)
        + (cTo.get(Calendar.MONTH) + 1);
    int tMonthDays = cTo.getActualMaximum(Calendar.DAY_OF_MONTH);
    float result = 0;

    if (fMonth == tMonth) {
      // the same month simple count
      result = (float) (tDay - fDay + 1) / fMonthDays;
    } else {
      // different months use some logic
      int fullMonths = tMonth - fMonth - 1;
      log.debug("Full months: " + fullMonths);
      // count overlaping fractions
      log.debug("Border days: " + (fMonthDays - fDay + 1) + "/"
          + fMonthDays + " " + tDay + "/" + tMonthDays);
      float fFract = (float) (fMonthDays - fDay + 1) / fMonthDays;
      float tFract = (float) tDay / tMonthDays;
      log.debug("Fractions: " + fFract + "/" + tFract);
      result = fFract + fullMonths + tFract;
    }
    log.debug("Result: " + result);
    return result;
  }

  public Period daysLeadingToFirstOfNextMonth() {
    if (isWithinOneMonth())
      return Period.NONE;

    Calendar from = calendarFrom(getFrom());
    if (from.get(DAY_OF_MONTH) == 1)
      return Period.NONE; // it is in more than o month and starts on first => no leading

    Calendar to = calendarFrom(getFrom());
    to.set(DAY_OF_MONTH, to.getActualMaximum(DAY_OF_MONTH));
    return new Period(getFrom(), to.getTime());
  }

  public Period daysTrailingAfterLastOfPreviousMonth() {
    if (isWithinOneMonth())
      return Period.NONE;

    Calendar to = calendarFrom(getTo());
    if (to.get(DAY_OF_MONTH) == to.getActualMaximum(DAY_OF_MONTH))
      return Period.NONE; // it is in more than a month and ends on last => no trailer

    Calendar from = calendarFrom(getTo());
    from.set(DAY_OF_MONTH, 1);
    return new Period(from.getTime(), to.getTime());
  }

  public boolean isWithinOneMonth() {
    Calendar from = calendarFrom(getFrom());
    Calendar to = calendarFrom(getTo());
    return from.get(MONTH) == to.get(MONTH)
        && from.get(YEAR) == to.get(YEAR);
  }

  public boolean isWholeMonthsOnly() {
    Calendar from = calendarFrom(getFrom());
    Calendar to = calendarFrom(getTo());
    return from.get(DAY_OF_MONTH) == 1
        && to.get(DAY_OF_MONTH) == to.getActualMaximum(DAY_OF_MONTH);
  }

  private Calendar calendarFrom(final Date date) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  public void adjustThisPeriodStartBy(final Period leadingDays) {
    if (leadingDays != Period.NONE) {
      Calendar newFrom = calendarFrom(leadingDays.getTo());
      newFrom.add(DAY_OF_MONTH, 1);
      if (newFrom.getTime().after(getFrom()))
        setFrom(newFrom.getTime());
    }
  }

  public void adjustThisPeriodEndBy(final Period trailingDays) {
    if (trailingDays != Period.NONE) {
      Calendar newTo = calendarFrom(trailingDays.getFrom());
      newTo.add(DAY_OF_MONTH, -1);
      if (newTo.getTime().before(getTo()))
        setTo(newTo.getTime());
    }
  }

  public int days() {
    return toJodaPeriod(PeriodType.days()).getDays();
  }

  public int months() {
    return toJodaPeriod(PeriodType.months()).getMonths();
  }

  public org.joda.time.Period toJodaPeriod(final PeriodType type) {
    DateTime from = new DateTime(getFrom());
    DateTime to = new DateTime(getTo()).plusDays(1);
    return new org.joda.time.Period(from, to, type);
  }

  public Period duplicate() {
    return new Period(new Date(getFrom().getTime()), new Date(getTo().getTime()));
  }

  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public String toString() {
    StringBuilder period = new StringBuilder(24);
    period.append("[")
        .append(FORMAT.format(getFrom()))
        .append(", ")
        .append(FORMAT.format(getTo()))
        .append("]");
    return period.toString();
  }
}