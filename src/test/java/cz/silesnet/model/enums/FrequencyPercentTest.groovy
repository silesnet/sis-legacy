package cz.silesnet.model.enums

import cz.silesnet.model.Period
import cz.silesnet.model.invoice.Percent
import spock.lang.Specification

/**
 * User: der3k
 * Date: 18.3.11
 * Time: 21:26
 */
class FrequencyPercentTest extends Specification {
  def 'calculates monthly percentage for period with one month'() {
  expect:
    Frequency.MONTHLY.percentageFor(period) == percent
  where:
    period | percent
    Period.NONE | Percent.ZERO
    period('2011-03-01', '2011-03-31') | Percent.HUNDRED
    period('2011-04-01', '2011-04-15') | Percent.FIFTY
    period('2011-04-01', '2011-04-03') | Percent.TEN

    period('2011-02-28', '2011-02-28') | Percent.rate(4) // 1/28 = 0.03571
    period('2011-04-01', '2011-04-01') | Percent.rate(3) // 1/30 = 0.03333
    period('2011-03-31', '2011-03-31') | Percent.rate(3) // 1/31 = 0.03225

    period('2011-02-01', '2011-02-27') | Percent.rate(96) // 27/28 = 0.96428
    period('2011-04-01', '2011-04-29') | Percent.rate(97) // 29/30 = 0.96666
    period('2011-03-01', '2011-03-30') | Percent.rate(97) // 30/31 = 0.96774
  }

  def 'calculates monthly percentage for whole month periods'() {
  expect:
    Frequency.MONTHLY.percentageFor(period) == percent
  where:
    period | percent
    period('2011-03-01', '2011-03-31') | Percent.HUNDRED
    period('2011-03-01', '2011-04-30') | Percent.rate(200)
    period('2011-03-01', '2011-05-31') | Percent.rate(300)
    period('2011-03-01', '2011-06-30') | Percent.rate(400)

    period('2011-01-01', '2011-12-31') | Percent.rate(1200)
    period('2011-01-01', '2012-12-31') | Percent.rate(2400)

  }

  def 'calculates percentage for periods with leading days'() {
  expect:
    Frequency.MONTHLY.percentageFor(period) == percent
  where:
    period | percent
    period('2011-03-31', '2011-04-30') | Percent.rate(103) // 1/31 = 0.03225 => +3%
    period('2011-03-02', '2011-04-30') | Percent.rate(197) // 30/31 = 0.96774 => +97%

    period('2011-04-30', '2011-05-31') | Percent.rate(103) // 1/30 = 0.33333 => +3%
    period('2011-04-02', '2011-05-31') | Percent.rate(197) // 29/30 = 0.96666 => +97%
    period('2011-04-16', '2011-05-31') | Percent.rate(150) // 15/30 = 0.50000 => +50%

    period('2011-02-02', '2011-03-31') | Percent.rate(196) // 27/28 = 0.96428 => +96%
    period('2011-02-28', '2011-03-31') | Percent.rate(104) // 1/28 = 0.03571 => +4%
  }

  def 'calculates percentage for periods with trailing days'() {
  expect:
    Frequency.MONTHLY.percentageFor(period) == percent
  where:
    period | percent
    period('2011-02-01', '2011-03-01') | Percent.rate(103) // 1/31 = 0.03225 => +3%
    period('2011-02-01', '2011-03-30') | Percent.rate(197) // 30/31 = 0.96774 => +97%

    period('2011-03-01', '2011-04-01') | Percent.rate(103) // 1/30 = 0.33333 => +3%
    period('2011-03-01', '2011-04-29') | Percent.rate(197) // 29/30 = 0.96666 => +97%
    period('2011-03-01', '2011-04-15') | Percent.rate(150) // 15/30 = 0.50000 => +50%

    period('2011-01-01', '2011-02-27') | Percent.rate(196) // 27/28 = 0.96428 => +96%
    period('2011-01-01', '2011-02-01') | Percent.rate(104) // 1/28 = 0.03571 => +4%
  }

  def 'calculates percentage for periods with leading and trailing days'() {
  expect:
    Frequency.MONTHLY.percentageFor(period) == percent
  where:
    period | percent
    period('2011-01-31', '2011-02-01') | Percent.rate(7) // 1/31 (3%) + 1/28 (4%) = 7%
    period('2011-02-28', '2011-03-01') | Percent.rate(7) // 1/28 (4%) + 1/31 (3%) = 7%
    period('2011-03-31', '2011-04-01') | Percent.rate(6) // 1/31 (3%) + 1/30 (3%) = 6%

    period('2011-01-02', '2011-02-27') | Percent.rate(193) // 30/31 (97%) + 27/28 (96%) = 193%
    period('2011-02-02', '2011-03-30') | Percent.rate(193) // 27/28 (96%) + 30/31 (97%) = 193%
    period('2011-03-02', '2011-04-29') | Percent.rate(194) // 30/31 (97%) + 29/30 (97%) = 194%
  }

  def 'calculates percentage for whole months periods with leading and trailing days'() {
  expect:
    Frequency.MONTHLY.percentageFor(period) == percent
  where:
    period | percent
    period('2011-01-02', '2011-03-30') | Percent.rate(294) // 97% + 100% + 97% = 294%
    period('2011-02-02', '2011-04-29') | Percent.rate(293) // 96% + 100% + 97% = 293%
    period('2011-03-02', '2011-05-30') | Percent.rate(294) // 97% + 100% + 97% = 294%

    period('2011-01-31', '2011-03-01') | Percent.rate(106) // 3% + 100% + 3% = 106%
    period('2011-02-28', '2011-04-01') | Percent.rate(107) // 4% + 100% + 3% = 107%
    period('2011-03-31', '2011-05-01') | Percent.rate(106) // 3% + 100% + 3% = 106%

    period('2011-01-31', '2011-04-01') | Percent.rate(206) // 3% + 200% + 3% = 206%
    period('2011-01-31', '2011-05-01') | Percent.rate(306) // 3% + 300% + 3% = 306%
    period('2011-01-31', '2011-06-01') | Percent.rate(406) // 3% + 400% + 3% = 406%
    period('2011-01-31', '2011-07-01') | Percent.rate(506) // 3% + 500% + 3% = 506%
  }

  def 'calculates percentage for periods with year overlap'() {
  expect:
    Frequency.MONTHLY.percentageFor(period) == percent
  where:
    period | percent
    period('2010-12-31', '2011-02-01') | Percent.rate(107) // 3% + 100% + 4% = 107%
    period('2010-11-30', '2011-01-01') | Percent.rate(106) // 3% + 100% + 3% = 106%
    period('2010-11-30', '2011-02-01') | Percent.rate(207) // 3% + 200% + 4% = 207%
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

  def 'period duplicate is the same period'() {
    def period = period('2011-01-01', '2011-01-31')
    def duplicate = period.duplicate()
  expect:
    duplicate.getFrom().equals(period.getFrom())
    duplicate.getTo().equals(period.getTo())
  }

  def 'period duplicate changes does not affect original'() {
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

  def "period adjusts start date by other period's end"() {
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

  def "period adjusts end date by other period's start"() {
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

  def static Date date(String date) {
    Date.parse('yyyy-MM-dd', date)
  }

  def static Period period(String from, String to) {
    new Period(date(from), date(to))
  }

}
