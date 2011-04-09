package cz.silesnet.utils

import cz.silesnet.model.Period
import spock.lang.Specification

/**
 * User: der3k
 * Date: 26.3.11
 * Time: 16:46
 */
class DatesTest extends Specification {

  def 'finds first day of the month'() {
  expect:
    Dates.firstDayOfMonth(date(date)).getTime() == date(first)
  where:
    date | first
    '2011-03-01' | '2011-03-01'
    '2011-03-05' | '2011-03-01'
    '2011-03-31' | '2011-03-01'
    '2011-12-07' | '2011-12-01'
  }

  def 'can get month from date'() {
  expect:
    Dates.getMonth(date(date)) == month
  where:
    date | month
    '2011-01-01' | 1
    '2011-01-31' | 1
    '2011-12-31' | 12
  }


  def 'calculates days of month'() {
  expect:
    Dates.daysOfMonth(date('2011-01-01')) == 31
    Dates.daysOfMonth(date('2011-02-01')) == 28
    Dates.daysOfMonth(date('2011-03-01')) == 31
    Dates.daysOfMonth(date('2011-04-01')) == 30
    Dates.daysOfMonth(date('2011-05-01')) == 31
    Dates.daysOfMonth(date('2011-06-01')) == 30
    Dates.daysOfMonth(date('2011-07-01')) == 31
    Dates.daysOfMonth(date('2011-08-01')) == 31
    Dates.daysOfMonth(date('2011-09-01')) == 30
    Dates.daysOfMonth(date('2011-10-01')) == 31
    Dates.daysOfMonth(date('2011-11-01')) == 30
    Dates.daysOfMonth(date('2011-12-01')) == 31

    Dates.daysOfMonth(date('2012-02-01')) == 29
  }

  def static Date date(String date) {
    Date.parse('yyyy-MM-dd', date)
  }

  def static Period period(String from, String to) {
    new Period(date(from), date(to))
  }
}
