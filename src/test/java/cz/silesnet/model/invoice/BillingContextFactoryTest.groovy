package cz.silesnet.model.invoice

import cz.silesnet.model.enums.Country
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Specification

/**
 * User: der3k
 * Date: 11.3.11
 * Time: 21:05
 */
class BillingContextFactoryTest extends Specification {
  def 'when added returns context by country'() {
    def factory = new BillingContextFactory()
    def context = new BillingContextBean()
  when:
    factory.add(Country.CZ, context)
  then:
    factory.billingContextFor(Country.CZ) == context
  }

  def 'instantiates from Spring XML configuration'() {
    def spring = new ClassPathXmlApplicationContext('context/sis-properties.xml', 'context/billing-context.xml')
    def factory = spring.getBean('billingContextFactory', BillingContextFactory)
  expect:
    factory != null
    factory.billingContextFor(Country.CZ) != null
  }

  def 'czech billing context from Spring XML configuration has correct values'() {
    def spring = new ClassPathXmlApplicationContext('context/sis-properties.xml', 'context/sis-billing.xml')
    def factory = spring.getBean('billingContextFactory', BillingContextFactory)
    def czech = factory.billingContextFor(Country.CZ)
  expect:
    czech.calculateVatFor(Amount.HUNDRED) == Amount.of(19)
    czech.calculateVatFor(Amount.of('0.02')) == Amount.ZERO       // 0.02 * 0.19 = 0.0038 ~ 0.00
    czech.calculateVatFor(Amount.of('0.03')) == Amount.of('0.01') // 0.03 * 0.19 = 0.0057 ~ 0.01
    czech.roundTotalOf(Amount.of('0.49')) == Amount.ZERO
    czech.roundTotalOf(Amount.of('0.50')) == Amount.ONE
    czech.purgeDateFor(date('2011-01-05')) == date('2011-01-19')  // 14 days
  }

  def 'polish billing context from Spring XML configuration has correct values'() {
    def spring = new ClassPathXmlApplicationContext('context/sis-properties.xml', 'context/sis-billing.xml')
    def factory = spring.getBean('billingContextFactory', BillingContextFactory)
    def czech = factory.billingContextFor(Country.PL)
  expect:
    czech.calculateVatFor(Amount.HUNDRED) == Amount.of(23)
    czech.calculateVatFor(Amount.of('0.02')) == Amount.ZERO       // 0.02 * 0.23 = 0.0046 ~ 0.00
    czech.calculateVatFor(Amount.of('0.03')) == Amount.of('0.01') // 0.03 * 0.23 = 0.0069 ~ 0.01
    czech.roundTotalOf(Amount.of('0.49')) == Amount.of('0.49')
    czech.roundTotalOf(Amount.of('0.50')) == Amount.of('0.50')
    czech.purgeDateFor(date('2011-01-05')) == date('2011-01-19')  // 14 days
  }

  static def Date date(String date) {
    Date.parse('yyyy-MM-dd', date)
  }


}
