package cz.silesnet.model

import cz.silesnet.model.enums.Frequency
import java.text.DateFormat
import java.text.SimpleDateFormat
import spock.lang.Specification

/**
 * User: der3k
 * Date: 13.3.11
 * Time: 14:49
 */
class BillingTest extends Specification {
  private static final DateFormat TIME_FORMAT = new SimpleDateFormat('HH:mm:ss.SSS')

  def billing = new Billing()

  def 'calculates new period for billing forward'() {
    billing.setIsBilledAfter(false)
    billing.setFrequency(Frequency.MONTHLY)
    billing.setLastlyBilled(date('2011-03-31'))
  expect:
    billing.nextBillPeriod(due) == period
  where:
    due | period
    date('2011-04-05') | period('2011-04-01', '2011-04-30')
    date('2011-05-05') | period('2011-04-01', '2011-05-31')
  }

  def 'calculates new period for billing backward'() {
    billing.setIsBilledAfter(true)
    billing.setFrequency(Frequency.MONTHLY)
    billing.setLastlyBilled(date('2011-03-31'))
  expect:
    billing.nextBillPeriod(due) == period
  where:
    due | period
    date('2011-05-05') | period('2011-04-01', '2011-04-30')
    date('2011-06-05') | period('2011-04-01', '2011-05-31')
  }

  def 'no new period when billing.lastlyBilled is null'() {
    billing.setLastlyBilled(null)
  expect:
    billing.nextBillPeriod(new Date()) == Period.NONE
  }


  def 'next invoice-from date'() {
    billing.setLastlyBilled(date(last))
  expect:
    billing.nextInvoiceFrom().getTime() == date(next)
  where:
    last | next
    '2010-12-31' | '2011-01-01'
    '2011-02-28' | '2011-03-01'
    '2011-11-30' | '2011-12-01'
    '2012-02-29' | '2012-03-01'
  }

  def 'next invoice-from date has always time set to zero'() {
    billing.setLastlyBilled(last)
  expect:
    hasZeroTime(billing.nextInvoiceFrom().getTime())
  where:
    last << [
        instant('2011-01-01 00:00:00.001'),
        instant('2011-01-01 15:10:30.456'),
        instant('2011-01-01 23:59:59.999'),
    ]
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
