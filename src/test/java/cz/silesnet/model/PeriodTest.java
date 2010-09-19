package cz.silesnet.model;

import cz.silesnet.model.enums.Country;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.testng.Assert.*;

public class PeriodTest {

  protected final Log log = LogFactory.getLog(getClass());

  @Test
  public void testIsValid() {

    // both null - invalid
    Period p = new Period();
    assertFalse(p.isValid());

    Calendar c1 = new GregorianCalendar(2006, Calendar.JANUARY, 1);
    Calendar c2 = new GregorianCalendar(2006, Calendar.FEBRUARY, 28);

    // from set - valid
    p.setFrom(c1.getTime());
    assertTrue(p.isValid());

    // both set, from < to - valid
    p.setTo(c2.getTime());
    assertTrue(p.isValid());

    // to set - valid
    p.setFrom(null);
    assertTrue(p.isValid());

    // both set, from = to - valid
    p.setFrom(p.getTo());
    assertTrue(p.isValid());

    // both set, from > to - invalid
    p.setFrom(c2.getTime());
    p.setTo(c1.getTime());
    assertFalse(p.isValid());

  }

  @Test
  public void testIsComplete() {
    // both null - not complete
    Period p = new Period();
    assertFalse(p.isComplete());

    Calendar c1 = new GregorianCalendar(2006, Calendar.JANUARY, 1);
    Calendar c2 = new GregorianCalendar(2006, Calendar.FEBRUARY, 28);

    // from set - not complete
    p.setFrom(c1.getTime());
    assertFalse(p.isComplete());

    // both set, from < to - complete
    p.setTo(c2.getTime());
    assertTrue(p.isComplete());

    // to set - not complete
    p.setFrom(null);
    assertFalse(p.isComplete());

    // both set, from = to - complete
    p.setFrom(p.getTo());
    assertTrue(p.isComplete());

    // both set, from > to - not complete
    p.setFrom(c2.getTime());
    p.setTo(c1.getTime());
    assertFalse(p.isComplete());

  }

  @Test
  public void testContains() {

    Calendar c1 = new GregorianCalendar(2006, Calendar.JANUARY, 1);
    Calendar c2 = new GregorianCalendar(2006, Calendar.FEBRUARY, 28);
    Calendar c3 = new GregorianCalendar(2006, Calendar.MARCH, 10);
    Calendar c4 = new GregorianCalendar(2006, Calendar.APRIL, 15);
    Calendar c5 = new GregorianCalendar(2006, Calendar.MAY, 30);

    // not valid period does not contain anythink
    Period p = new Period();
    assertFalse(p.isValid());
    try {
      // not valid period
      p.contains(c1.getTime());
      fail();
    }
    catch (IllegalStateException e) {
      log.debug("Got expected exception: " + e);
    }

    // c2 ->
    p.setFrom(c2.getTime());
    assertTrue(p.isValid());
    assertTrue(p.contains(c2.getTime()));
    assertTrue(p.contains(c3.getTime()));
    assertFalse(p.contains(c1.getTime()));

    // <- c4
    p.setFrom(null);
    p.setTo(c4.getTime());
    assertTrue(p.isValid());
    assertTrue(p.contains(c3.getTime()));
    assertTrue(p.contains(c4.getTime()));
    assertFalse(p.contains(c5.getTime()));

    // c2 <-> c4
    p.setFrom(c2.getTime());
    p.setTo(c4.getTime());
    assertTrue(p.isValid());
    assertTrue(p.contains(c2.getTime()));
    assertTrue(p.contains(c3.getTime()));
    assertTrue(p.contains(c4.getTime()));
    assertFalse(p.contains(c1.getTime()));
    assertFalse(p.contains(c5.getTime()));

    try {
      p.contains(null);
      fail();
    }
    catch (NullPointerException e) {
      log.debug("Got expected exception: " + e);
    }
  }

  @Test
  public void testGetIntersection() {

    Calendar c1 = new GregorianCalendar(2006, Calendar.JANUARY, 1);
    Calendar c2 = new GregorianCalendar(2006, Calendar.FEBRUARY, 2);
    Calendar c3 = new GregorianCalendar(2006, Calendar.MARCH, 3);
    Calendar c4 = new GregorianCalendar(2006, Calendar.APRIL, 4);

    Period p1 = new Period();
    Period p2 = new Period();

    // p1 valid, p2 not valid
    p1.setFrom(c1.getTime());
    assertTrue(p1.isValid());
    assertFalse(p2.isValid());

    try {
      // intersection with null
      p1.intersection(null);
      fail();
    }
    catch (NullPointerException e) {
      log.debug("Got expected exception: " + e);
    }

    try {
      // intersection with invalid argument
      p1.intersection(p2);
      fail();
    }
    catch (IllegalArgumentException e) {
      log.debug("Got expected exception: " + e);
    }

    try {
      // intersection to invalid
      p2.intersection(p1);
      fail();
    }
    catch (IllegalStateException e) {
      log.debug("Got expected exception: " + e);
    }

    Period result1 = null;
    Period result2 = null;

    // both not complete

    // 1
    p1.setFrom(null);
    p1.setTo(c2.getTime());
    p2.setFrom(c3.getTime());
    p2.setTo(null);
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1);
    assertNull(result1);
    assertNull(result2);

    // 2
    p1.setFrom(null);
    p1.setTo(c2.getTime());
    p2.setFrom(null);
    p2.setTo(c1.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertFalse(result1.isComplete());
    assertTrue(result1.getFrom() == null
        && result1.getTo().equals(c1.getTime()));

    // 3
    p1.setFrom(c3.getTime());
    p1.setTo(null);
    p2.setFrom(c4.getTime());
    p2.setTo(null);
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertFalse(result1.isComplete());
    assertTrue(result1.getTo() == null
        && result1.getFrom().equals(c4.getTime()));

    // 4
    p1.setFrom(c2.getTime());
    p1.setTo(null);
    p2.setFrom(null);
    p2.setTo(c3.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertTrue(result1.isComplete());
    assertTrue(result1.getFrom().equals(c2.getTime())
        && result1.getTo().equals(c3.getTime()));

    // both complete

    // 5
    p1.setFrom(c1.getTime());
    p1.setTo(c2.getTime());
    p2.setFrom(c3.getTime());
    p2.setTo(c4.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1);
    assertNull(result1);
    assertNull(result2);

    // 6
    p1.setFrom(c1.getTime());
    p1.setTo(c3.getTime());
    p2.setFrom(c2.getTime());
    p2.setTo(c4.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertTrue(result1.isComplete());
    assertTrue(result1.getFrom().equals(c2.getTime())
        && result1.getTo().equals(c3.getTime()));

    // 7
    p1.setFrom(c1.getTime());
    p1.setTo(c4.getTime());
    p2.setFrom(c2.getTime());
    p2.setTo(c3.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertTrue(result1.isComplete());
    assertTrue(result1.getFrom().equals(c2.getTime())
        && result1.getTo().equals(c3.getTime()));

    // mixed (complete, incomplete)

    // 8
    p1.setFrom(c2.getTime());
    p1.setTo(c3.getTime());
    p2.setFrom(c1.getTime());
    p2.setTo(null);
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertTrue(result1.isComplete());
    assertTrue(result1.getFrom().equals(c2.getTime())
        && result1.getTo().equals(c3.getTime()));

    // 9
    p1.setFrom(c1.getTime());
    p1.setTo(c3.getTime());
    p2.setFrom(c2.getTime());
    p2.setTo(null);
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertTrue(result1.isComplete());
    assertTrue(result1.getFrom().equals(c2.getTime())
        && result1.getTo().equals(c3.getTime()));

    // 10
    p1.setFrom(c1.getTime());
    p1.setTo(c3.getTime());
    p2.setFrom(c4.getTime());
    p2.setTo(null);
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1);
    assertNull(result1);
    assertNull(result2);

    // 11
    p1.setFrom(c2.getTime());
    p1.setTo(c3.getTime());
    p2.setFrom(null);
    p2.setTo(c4.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertTrue(result1.isComplete());
    assertTrue(result1.getFrom().equals(c2.getTime())
        && result1.getTo().equals(c3.getTime()));

    // 12
    p1.setFrom(c2.getTime());
    p1.setTo(c4.getTime());
    p2.setFrom(null);
    p2.setTo(c3.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertTrue(result1.isComplete());
    assertTrue(result1.getFrom().equals(c2.getTime())
        && result1.getTo().equals(c3.getTime()));

    // 13
    p1.setFrom(c2.getTime());
    p1.setTo(c3.getTime());
    p2.setFrom(null);
    p2.setTo(c1.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1);
    assertNull(result1);
    assertNull(result2);

    // Test self intersection

    // <->
    p1.setFrom(c2.getTime());
    p1.setTo(c3.getTime());
    p2.setFrom(c2.getTime());
    p2.setTo(c3.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertTrue(result1.isComplete());
    assertTrue(result1.getFrom().equals(c2.getTime())
        && result1.getTo().equals(c3.getTime()));

    // <-
    p1.setFrom(null);
    p1.setTo(c3.getTime());
    p2.setFrom(null);
    p2.setTo(c3.getTime());
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertFalse(result1.isComplete());
    assertTrue(result1.getFrom() == null
        && result1.getTo().equals(c3.getTime()));

    // <-
    p1.setFrom(c2.getTime());
    p1.setTo(null);
    p2.setFrom(c2.getTime());
    p2.setTo(null);
    result1 = p1.intersection(p2);
    result2 = p2.intersection(p1);
    log.debug(result1.getPeriodString());
    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.equals(result2));
    assertTrue(result2.equals(result1));
    assertTrue(result1.isValid());

    assertFalse(result1.isComplete());
    assertTrue(result1.getTo() == null
        && result1.getFrom().equals(c2.getTime()));
  }

  @Test
  public void testGetMonthsRate() {

    int precision = 10000;

    Period p = new Period();
    assertFalse(p.isComplete());
    float months = 0;
    Float monthsRounded = null;
    try {
      // months from not complete period
      months = p.getMonthsRate();
      fail();
    }
    catch (IllegalStateException e) {
      log.debug("Got expected exception. " + e);
    }

    Calendar c1 = new GregorianCalendar(2006, Calendar.JANUARY, 15);
    Calendar c2 = new GregorianCalendar(2006, Calendar.MAY, 10);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 3.871)));

    c1 = new GregorianCalendar(2006, Calendar.FEBRUARY, 1);
    c2 = new GregorianCalendar(2006, Calendar.FEBRUARY, 28);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 1.0)));

    c1 = new GregorianCalendar(2006, Calendar.FEBRUARY, 1);
    c2 = new GregorianCalendar(2006, Calendar.FEBRUARY, 20);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 0.7143)));

    c1 = new GregorianCalendar(2006, Calendar.FEBRUARY, 5);
    c2 = new GregorianCalendar(2006, Calendar.FEBRUARY, 28);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 0.8571)));

    c1 = new GregorianCalendar(2006, Calendar.FEBRUARY, 5);
    c2 = new GregorianCalendar(2006, Calendar.FEBRUARY, 20);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 0.5714)));

    c1 = new GregorianCalendar(2006, Calendar.FEBRUARY, 20);
    c2 = new GregorianCalendar(2006, Calendar.MARCH, 10);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 0.6440)));

    c1 = new GregorianCalendar(2005, Calendar.JANUARY, 15);
    c2 = new GregorianCalendar(2006, Calendar.MAY, 10);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 15.8710)));

    c1 = new GregorianCalendar(2005, Calendar.JANUARY, 1);
    c2 = new GregorianCalendar(2005, Calendar.DECEMBER, 31);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 12)));

    c1 = new GregorianCalendar(2005, Calendar.NOVEMBER, 1);
    c2 = new GregorianCalendar(2006, Calendar.MAY, 31);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 7)));

    c1 = new GregorianCalendar(2004, Calendar.NOVEMBER, 1);
    c2 = new GregorianCalendar(2006, Calendar.MAY, 31);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 19)));

    c1 = new GregorianCalendar(2006, Calendar.JANUARY, 1);
    c2 = new GregorianCalendar(2006, Calendar.DECEMBER, 31);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 12)));

    c1 = new GregorianCalendar(2005, Calendar.MARCH, 15);
    c2 = new GregorianCalendar(2006, Calendar.MARCH, 14);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 12)));

    c1 = new GregorianCalendar(2005, Calendar.MARCH, 15);
    c2 = new GregorianCalendar(2006, Calendar.MARCH, 15);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 12.0323)));

    c1 = new GregorianCalendar(2005, Calendar.MARCH, 10);
    c2 = new GregorianCalendar(2006, Calendar.MARCH, 25);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 12.5161)));

    c1 = new GregorianCalendar(2005, Calendar.MARCH, 25);
    c2 = new GregorianCalendar(2006, Calendar.MARCH, 10);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 11.5484)));

    c1 = new GregorianCalendar(2005, Calendar.MARCH, 1);
    c2 = new GregorianCalendar(2006, Calendar.MARCH, 31);
    p = new Period(c1.getTime(), c2.getTime());
    months = p.getMonthsRate();
    monthsRounded = (float) Math.round(months * precision) / precision;
    log.debug(monthsRounded);
    assertTrue(monthsRounded.equals(Float.valueOf((float) 13)));

  }

  @Test
  public void testCountryShortName() {
    assertTrue("cz".equals(Country.CZ.getShortName()));
    assertTrue("pl".equals(Country.PL.getShortName()));
    assertTrue("sk".equals(Country.SK.getShortName()));
  }
}