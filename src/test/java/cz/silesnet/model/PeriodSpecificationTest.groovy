package cz.silesnet.model

import org.joda.time.PeriodType
import spock.lang.Specification

/**
 * User: der3k
 * Date: 26.3.11
 * Time: 13:19
 */
class PeriodSpecificationTest extends Specification {

  def 'duplicate is the same period'() {
    def period = period('2011-01-01', '2011-01-31')
    def duplicate = period.duplicate()
  expect:
    duplicate.getFrom().equals(period.getFrom())
    duplicate.getTo().equals(period.getTo())
  }

  def 'duplicate changes does not affect original'() {
    def from = date('2011-01-01')
    def to = date('2011-01-31')
    def period = new Period(new Date(from.getTime()), new Date(to.getTime()))
    def duplicate = period.duplicate()
  when:
    duplicate.getFrom().setTime(0)
    duplicate.getTo().setTime(0)
  then:
    period.getFrom().equals(from)
    period.getTo().equals(to)
  }

  def 'duplicate is null safe'() {
  expect:
    new Period(null, null).duplicate()
  }

  def 'detects period within single month'() {
  expect:
    period.isWithinOneMonth() == within
  where:
    period | within
    period('2011-01-01', '2011-01-01') | true
    period('2011-01-01', '2011-01-15') | true
    period('2011-01-10', '2011-01-15') | true
    period('2011-01-01', '2011-01-31') | true

    period('2011-01-01', '2011-02-01') | false
    period('2011-01-01', '2012-01-01') | false
  }

  def 'detects whole months period'() {
  expect:
    period.isWholeMonthsOnly() == whole
  where:
    period | whole
    period('2011-01-01', '2011-01-31') | true
    period('2011-02-01', '2011-02-28') | true
    period('2011-12-01', '2011-12-31') | true
    period('2011-12-01', '2012-12-31') | true

    period('2011-01-01', '2011-01-30') | false
    period('2011-01-02', '2011-01-31') | false
    period('2011-02-01', '2011-02-27') | false
    period('2011-02-02', '2011-02-28') | false
  }

  def "adjusts start date by other's period end"() {
    def rounded = period('2011-01-31', '2011-02-01')
    rounded.adjustThisPeriodStartBy(other)
  expect:
    rounded == expected
  where:
    other | expected
    Period.NONE | period('2011-01-31', '2011-02-01')
    period('2011-01-30', '2011-01-30') | period('2011-01-31', '2011-02-01') // no overlap
    period('2011-01-01', '2011-01-01') | period('2011-01-31', '2011-02-01')

    period('2011-01-31', '2011-01-31') | period('2011-02-01', '2011-02-01')
  }

  def "adjusts end date by other's period start"() {
    def rounded = period('2011-01-31', '2011-02-01')
    rounded.adjustThisPeriodEndBy(other)
  expect:
    rounded == expected
  where:
    other | expected
    Period.NONE | period('2011-01-31', '2011-02-01')
    period('2011-02-02', '2011-02-02') | period('2011-01-31', '2011-02-01') // no overlap
    period('2011-02-28', '2011-02-28') | period('2011-01-31', '2011-02-01')

    period('2011-02-01', '2011-02-01') | period('2011-01-31', '2011-01-31')
  }

  def 'calculates month leading days'() {
  expect:
    period.daysLeadingToFirstOfNextMonth() == lead
  where:
    period | lead
    period('2011-01-05', '2011-01-09') | Period.NONE // must span at least two months to have leading days
    period('2011-01-01', '2011-01-30') | Period.NONE
    period('2011-01-01', '2011-01-31') | Period.NONE
    period('2011-01-02', '2011-01-31') | Period.NONE
    period('2011-01-30', '2011-01-31') | Period.NONE
    period('2011-01-31', '2011-01-31') | Period.NONE

    period('2011-01-01', '2011-02-01') | Period.NONE // no leading, trailer only
    period('2011-02-01', '2011-03-01') | Period.NONE

    period('2011-01-31', '2011-02-01') | period('2011-01-31', '2011-01-31')

    period('2011-01-02', '2011-02-01') | period('2011-01-02', '2011-01-31')
    period('2011-01-02', '2011-03-01') | period('2011-01-02', '2011-01-31')
    period('2011-01-02', '2012-03-01') | period('2011-01-02', '2011-01-31')
  }

  def 'calculates month trailing days'() {
  expect:
    period.daysTrailingAfterLastOfPreviousMonth() == trailer
  where:
    period | trailer
    period('2011-02-01', '2011-02-28') | Period.NONE // must be at least in two months to have trailer days
    period('2011-02-02', '2011-02-28') | Period.NONE
    period('2011-02-01', '2011-02-27') | Period.NONE
    period('2011-02-01', '2011-02-01') | Period.NONE
    period('2011-02-02', '2011-02-27') | Period.NONE

    period('2011-02-28', '2011-03-31') | Period.NONE // no trailer, leading only
    period('2011-02-28', '2011-04-30') | Period.NONE

    period('2011-02-28', '2011-03-01') | period('2011-03-01', '2011-03-01')
    period('2011-01-01', '2011-03-01') | period('2011-03-01', '2011-03-01')
    period('2011-02-28', '2011-03-30') | period('2011-03-01', '2011-03-30')
    period('2011-02-28', '2011-04-29') | period('2011-04-01', '2011-04-29')
    period('2011-02-28', '2011-12-30') | period('2011-12-01', '2011-12-30')
    period('2011-02-28', '2012-12-30') | period('2012-12-01', '2012-12-30')
  }

  def 'converts to JodaTime period'() {
    def period = period('2011-01-01', '2011-02-01')
    def jodaPeriod = period.toJodaPeriod(PeriodType.days())
  expect:
    jodaPeriod.getDays() == 32
  }

  def 'calculates period days'() {
  expect:
    period('2011-01-01', '2011-01-01').days() == 1
    period('2011-01-01', '2011-01-02').days() == 2
    period('2011-01-01', '2012-01-02').days() == 367
  }

  def 'calculates period months'() {
  expect:
    period('2011-01-01', '2011-01-01').months() == 0
    period('2011-01-01', '2011-01-30').months() == 0
    period('2011-01-01', '2011-01-31').months() == 1
    period('2011-01-01', '2012-01-02').months() == 12
  }

  def 'validates for completeness and possibility'() {
  expect:
    period.isCompleteAndValid() == expected
  where:
    period | expected
    Period.NONE | false
    new Period(null, null) | false
    new Period(date('2011-01-01'), null) | false
    new Period(null, date('2011-01-01')) | false

    period('2011-04-16', '2011-06-30') | true
    new Period(date('2011-01-01'), date('2011-01-01')) | true
    new Period(instant('2011-01-01 00:00:00.000'), instant('2011-01-01 00:00:00.000')) | true
    new Period(instant('2011-01-01 00:00:00.000'), instant('2011-01-01 00:00:00.001')) | true
    new Period(instant('2011-01-01 00:00:00.000'), instant('2011-01-01 23:59:59.999')) | true

    period('2011-01-02', '2011-01-01') | false
    new Period(instant('2011-01-01 00:00:00.001'), instant('2011-01-01 00:00:00.000')) | false
    new Period(instant('2011-01-01 23:59:59.999'), instant('2011-01-01 23:59:59.998')) | false
    new Period(instant('2011-01-02 00:00:00.000'), instant('2011-01-01 23:59:59.999')) | false
  }

  def 'union adjusts period ends additively'() {
    def base = period('2011-01-10', '2011-01-20')
  when:
    base.unionThisPeriodWith(other)
  then:
    base == unioned
  where:
    other | unioned
    Period.NONE | period('2011-01-10', '2011-01-20')
    period('2011-01-10', '2011-01-20') | period('2011-01-10', '2011-01-20')
    period('2011-01-11', '2011-01-19') | period('2011-01-10', '2011-01-20')

    period('2011-01-22', '2011-01-22') | period('2011-01-10', '2011-01-22')
    period('2011-01-08', '2011-01-08') | period('2011-01-08', '2011-01-20')
  }

  def 'union fails with invalid or incomplete period'() {
    def base = period('2011-01-10', '2011-01-20')
  when:
    base.unionThisPeriodWith(other)
  then:
    thrown IllegalArgumentException
  where:
    other << [
        new Period(null, null),
        new Period(date('2011-01-10'), null),
        new Period(null, date('2011-01-10')),
        period('2011-01-10', '2011-01-09')
    ]
  }

  def 'union fails when invalid or incomplete period'() {
    def other = period('2011-01-10', '2011-01-20')
  when:
    base.unionThisPeriodWith(other)
  then:
    thrown IllegalStateException
  where:
    base << [
        new Period(null, null),
        new Period(date('2011-01-10'), null),
        new Period(null, date('2011-01-10')),
        period('2011-01-10', '2011-01-09')
    ]
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
