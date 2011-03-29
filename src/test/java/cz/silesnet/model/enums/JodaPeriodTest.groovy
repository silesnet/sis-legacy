package cz.silesnet.model.enums

import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import spock.lang.Specification

/**
 * User: der3k
 * Date: 18.3.11
 * Time: 22:08
 */
class JodaPeriodTest extends Specification {
  def 'calculates moths'() {
  expect:
    period.getMonths() == months
  where:
    period | months
    period('2011-01-01', '2011-01-31') | 1
    period('2011-02-01', '2011-02-28') | 1
    period('2011-03-01', '2011-03-31') | 1
    period('2011-04-01', '2011-04-30') | 1
    period('2011-05-01', '2011-05-31') | 1
    period('2011-06-01', '2011-06-30') | 1
    period('2011-07-01', '2011-07-31') | 1
    period('2011-08-01', '2011-08-31') | 1
    period('2011-09-01', '2011-09-30') | 1
    period('2011-10-01', '2011-10-31') | 1
    period('2011-11-01', '2011-11-30') | 1
    period('2011-12-01', '2011-12-31') | 1
  }

  def 'check DateTime'() {
    def from = new DateTime('2011-01-11')
    def to = new DateTime('2011-03-05')
    def last = from.dayOfMonth().withMaximumValue().plusDays(1)
    def leadingDays = new Period(from, last, PeriodType.days())
  expect:
    true
  }

  static def period(String from, String to) {
    new Period(new DateTime(from), new DateTime(to).plusDays(1), PeriodType.yearMonthDay())
  }
}
