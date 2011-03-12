package cz.silesnet.model.invoice

import spock.lang.Specification

import cz.silesnet.model.enums.Country
import cz.silesnet.model.Invoicing
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * User: der3k
 * Date: 11.3.11
 * Time: 21:05
 */
class BillingContextFactoryTest extends Specification {
  def 'when added returns context by country'() {
    def factory = new BillingContextFactory()
    def context = new BillingContextBean()
    def invoicing = Mock(Invoicing)
    when:
      factory.add(Country.CZ, context)
      invoicing.getCountry() >> Country.CZ
    then:
      factory.billingContextFor(invoicing) == context
  }

  def 'instantiates fro Spring XML configuration'() {
    def spring = new ClassPathXmlApplicationContext('context/billing-context.xml')
    def factory = spring.getBean('billingContextFactory', BillingContextFactory)
    def invoicing = new Invoicing()
    invoicing.setCountry(Country.CZ)
  expect:
    factory != null
    factory.billingContextFor(invoicing) != null
  }
}
