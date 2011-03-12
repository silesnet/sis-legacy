package cz.silesnet.model.invoice

import spock.lang.Specification

import cz.silesnet.model.enums.Country
import cz.silesnet.model.Invoicing

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
}
