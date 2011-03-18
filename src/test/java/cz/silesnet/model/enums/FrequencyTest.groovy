package cz.silesnet.model.enums

import cz.silesnet.model.enums.Frequency
import spock.lang.Specification
import java.text.SimpleDateFormat
import java.text.DateFormat
import cz.silesnet.model.Period

/**
 * User: der3k
 * Date: 17.3.11
 * Time: 19:04
 */
class FrequencyTest extends Specification {

  private static final DateFormat TIME_FORMAT = new SimpleDateFormat('HH:mm:ss.SSS')

  def 'one-time period is one-day period of the date'() {
  expect:
    Frequency.ONE_TIME.periodFor(date(date)) == period
  where:
    date | period
    '2011-01-01' | period('2011-01-01', '2011-01-01')
    '2011-02-11' | period('2011-02-11', '2011-02-11')
    '2011-12-31' | period('2011-12-31', '2011-12-31')
  }

  def 'daily period is one-day period of the date'() {
  expect:
    Frequency.DAILY.periodFor(date(date)) == period
  where:
    date | period
    '2011-01-01' | period('2011-01-01', '2011-01-01')
    '2011-02-11' | period('2011-02-11', '2011-02-11')
    '2011-12-31' | period('2011-12-31', '2011-12-31')
  }

  def 'weekly period is week containing the date'() {
  expect:
    Frequency.WEEKLY.periodFor(date(date)) == period
  where:
    date | period
    '2011-03-14' | period('2011-03-14', '2011-03-20')
    '2011-03-17' | period('2011-03-14', '2011-03-20')
    '2011-03-20' | period('2011-03-14', '2011-03-20')
    '2011-03-21' | period('2011-03-21', '2011-03-27')
    '2011-03-25' | period('2011-03-21', '2011-03-27')
    '2011-03-27' | period('2011-03-21', '2011-03-27')
    '2011-03-28' | period('2011-03-28', '2011-04-03')
    '2011-03-31' | period('2011-03-28', '2011-04-03')
    '2011-04-03' | period('2011-03-28', '2011-04-03')
  }

  def 'monthly period is month containing the date'() {
  expect:
    Frequency.MONTHLY.periodFor(date(date)) == period
  where:
    date | period
    '2011-01-01' | period('2011-01-01', '2011-01-31')
    '2011-01-11' | period('2011-01-01', '2011-01-31')
    '2011-01-31' | period('2011-01-01', '2011-01-31')
    '2011-02-01' | period('2011-02-01', '2011-02-28')
    '2011-02-11' | period('2011-02-01', '2011-02-28')
    '2011-02-28' | period('2011-02-01', '2011-02-28')
    '2011-12-01' | period('2011-12-01', '2011-12-31')
    '2011-12-15' | period('2011-12-01', '2011-12-31')
    '2011-12-31' | period('2011-12-01', '2011-12-31')
  }

  def 'quarterly period is quarter containing the date'() {
  expect:
    Frequency.Q.periodFor(date(date)) == period
  where:
    date | period
    '2011-01-01' | period('2011-01-01', '2011-03-31')
    '2011-02-15' | period('2011-01-01', '2011-03-31')
    '2011-03-31' | period('2011-01-01', '2011-03-31')
    '2011-04-01' | period('2011-04-01', '2011-06-30')
    '2011-05-28' | period('2011-04-01', '2011-06-30')
    '2011-06-30' | period('2011-04-01', '2011-06-30')
    '2011-07-01' | period('2011-07-01', '2011-09-30')
    '2011-08-15' | period('2011-07-01', '2011-09-30')
    '2011-09-30' | period('2011-07-01', '2011-09-30')
    '2011-10-01' | period('2011-10-01', '2011-12-31')
    '2011-11-15' | period('2011-10-01', '2011-12-31')
    '2011-12-31' | period('2011-10-01', '2011-12-31')
  }

  def '2 quarter period is half of year containing the date'() {
  expect:
    Frequency.QQ.periodFor(date(date)) == period
  where:
    date | period
    '2011-01-01' | period('2011-01-01', '2011-06-30')
    '2011-04-01' | period('2011-01-01', '2011-06-30')
    '2011-06-30' | period('2011-01-01', '2011-06-30')
    '2011-07-01' | period('2011-07-01', '2011-12-31')
    '2011-10-01' | period('2011-07-01', '2011-12-31')
    '2011-12-31' | period('2011-07-01', '2011-12-31')
  }

  def 'annual period is year containing the date'() {
  expect:
    Frequency.ANNUAL.periodFor(date(date)) == period
  where:
    date | period
    '2011-01-01' | period('2011-01-01', '2011-12-31')
    '2011-06-30' | period('2011-01-01', '2011-12-31')
    '2011-10-01' | period('2011-01-01', '2011-12-31')
    '2011-12-31' | period('2011-01-01', '2011-12-31')
    '2012-01-01' | period('2012-01-01', '2012-12-31')
    '2012-12-31' | period('2012-01-01', '2012-12-31')
  }

  def "produced period's ends have time set to zero"() {
  expect:
    hasZeroTime(frequency.periodFor(instant('2011-01-01 00:00:00.001')).getFrom())
    hasZeroTime(frequency.periodFor(instant('2011-01-01 00:00:00.001')).getTo())
    hasZeroTime(frequency.periodFor(instant('2011-01-01 15:10:30.456')).getFrom())
    hasZeroTime(frequency.periodFor(instant('2011-01-01 15:10:30.456')).getTo())
    hasZeroTime(frequency.periodFor(instant('2011-01-01 23:59:59.999')).getFrom())
    hasZeroTime(frequency.periodFor(instant('2011-01-01 23:59:59.999')).getTo())
  where:
    frequency << Frequency.values()
  }

  void hasZeroTime(Date date) {
    assert TIME_FORMAT.format(date) == '00:00:00.000'
  }

  def static Date instant(String instant) {
    Date.parse('yyyy-MM-dd HH:mm:ss.SSS', instant)
  }

  def static Date date(String date) {
    Date.parse('yyyy-MM-dd', date)
  }

  def static Period period(String from, String to) {
    new Period(date(from), date(to))
  }
}
