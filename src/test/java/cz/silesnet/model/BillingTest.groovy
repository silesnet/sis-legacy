package cz.silesnet.model

import cz.silesnet.model.enums.Frequency
import spock.lang.Specification

/**
 * User: der3k
 * Date: 13.3.11
 * Time: 14:49
 */
class BillingTest extends Specification {
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

  def 'finds first day of the month'() {
  expect:
    billing.firstDayOfMonth(date(date)).getTime() == date(first)
  where:
    date | first
    '2011-03-01' | '2011-03-01'
    '2011-03-05' | '2011-03-01'
    '2011-03-31' | '2011-03-01'
    '2011-12-07' | '2011-12-01'
  }


  def 'billing month is detected'() {
    billing.setFrequency(frequency)
  expect:
    billing.isInvoicingMonth(date(date))
  where:
    date | frequency
    '2011-01-01' | Frequency.MONTHLY
    '2011-01-01' | Frequency.Q
    '2011-01-01' | Frequency.QQ
    '2011-01-01' | Frequency.ANNUAL

    '2011-02-01' | Frequency.MONTHLY

    '2011-03-01' | Frequency.MONTHLY

    '2011-04-01' | Frequency.MONTHLY
    '2011-04-01' | Frequency.Q

    '2011-07-01' | Frequency.MONTHLY
    '2011-07-01' | Frequency.Q
    '2011-07-01' | Frequency.QQ

    '2011-10-01' | Frequency.MONTHLY
    '2011-10-01' | Frequency.Q
  }

  def 'non billing month is detected'() {
    billing.setFrequency(frequency)
  expect:
    !billing.isInvoicingMonth(date(date))
  where:
    date | frequency
    '2011-02-01' | Frequency.Q
    '2011-02-01' | Frequency.QQ
    '2011-02-01' | Frequency.ANNUAL

    '2011-03-01' | Frequency.Q
    '2011-03-01' | Frequency.QQ
    '2011-03-01' | Frequency.ANNUAL

    '2011-04-01' | Frequency.QQ
    '2011-04-01' | Frequency.ANNUAL

    '2011-05-01' | Frequency.Q
    '2011-05-01' | Frequency.QQ
    '2011-05-01' | Frequency.ANNUAL

    '2011-06-01' | Frequency.Q
    '2011-06-01' | Frequency.QQ
    '2011-06-01' | Frequency.ANNUAL

    '2011-07-01' | Frequency.ANNUAL
  }

  def 'can get month from date'() {
  expect:
    billing.getMonth(date(date)) == month
  where:
    date | month
    '2011-01-01' | 1
    '2011-01-31' | 1
    '2011-12-31' | 12
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

  def static Date date(String date) {
    Date.parse('yyyy-MM-dd', date)
  }

  def static Period period(String from, String to) {
    new Period(date(from), date(to))
  }
}
