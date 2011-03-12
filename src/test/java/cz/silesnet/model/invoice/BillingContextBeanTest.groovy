package cz.silesnet.model.invoice

import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Specification

/**
 * User: der3k
 * Date: 12.3.11
 * Time: 9:57
 */
class BillingContextBeanTest extends Specification {
  def context = new BillingContextBean()

  def 'calculates vat rounded to 2 digits after comma'() {
    context.vatPercent = 10
    context.setVatRounding(Rounding.SCALE_OF_2_HALF_UP_ROUNDING)
  expect:
    context.calculateVatFor(Amount.of(number)) == Amount.of(vat)
  where:
    number | vat
    10.00 | 1.00
    10.01 | 1.00
    10.02 | 1.00
    10.03 | 1.00
    10.04 | 1.00
    10.05 | 1.01
  }

  def 'calculates vat rounded to 0 digits after comma'() {
    context.vatPercent = 10
    context.setVatRounding(Rounding.SCALE_OF_0_HALF_UP_ROUNDING)
  expect:
    context.calculateVatFor(Amount.of(number)) == Amount.of(vat)
  where:
    number | vat
    10 | 1
    11 | 1
    12 | 1
    13 | 1
    14 | 1
    15 | 2
  }

  def 'rounds total to 0 digits after comma'() {
    context.setTotalRounding(Rounding.SCALE_OF_0_HALF_UP_ROUNDING)
  expect:
    context.roundTotalOf(Amount.of(total)) == Amount.of(rounded)
  where:
    total | rounded
    10 | 10
    10.1 | 10
    10.2 | 10
    10.3 | 10
    10.4 | 10
    10.5 | 11
  }

  def 'rounds total to 2 digits after comma'() {
    context.setTotalRounding(Rounding.SCALE_OF_2_HALF_UP_ROUNDING)
  expect:
    context.roundTotalOf(Amount.of(total)) == Amount.of(rounded)
  where:
    total | rounded
    10 | 10.00
    10.001 | 10.00
    10.002 | 10.00
    10.003 | 10.00
    10.004 | 10.00
    10.005 | 10.01
  }

  def 'adds purge days to invoicing date'() {
    context.purgeDays = 14
  expect:
    context.purgeDateFor(date(due)) == date(purgeDate)
  where:
    due | purgeDate
    '2011-01-01' | '2011-01-15'
    '2011-02-15' | '2011-03-01'
  }

  def 'instantiates from Spring XML'() {
    def spring = new ClassPathXmlApplicationContext('context/billing-context.xml')
    def context = spring.getBean('billingContextCz', BillingContext)
  expect:
    context.calculateVatFor(Amount.of(10)) == Amount.of(2)
    context.roundTotalOf(Amount.of(10.50)) == Amount.of(11)
    context.purgeDateFor(date('2011-01-01')) == date('2011-01-15')
  }

  def static Date date(String date) {
    Date.parse('yyyy-MM-dd', date)
  }

}
