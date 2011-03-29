package cz.silesnet.model.invoice

import spock.lang.Specification
import cz.silesnet.model.Invoicing

import cz.silesnet.model.enums.Country
import cz.silesnet.model.Customer
import cz.silesnet.model.Billing
import cz.silesnet.model.Service
import cz.silesnet.model.enums.Frequency
import spock.lang.Ignore

/**
 * User: der3k
 * Date: 9.3.11
 * Time: 8:22
  */
class BillFactoryTest extends Specification {
  private static final Country CZ = Country.CZ
  private static final String DF = 'yyyy-MM-dd'
  private static final Date INVOICING_DATE = date('2011-10-05')
  private static final String NUMBERING_BASE = '10000'

  def invoicing = new Invoicing()
  def context = Mock(BillingContext)
  def customer = new Customer()
  def billing = new Billing()
  def recur = new Service()
  def onetime = new Service()

  def setup() {
    invoicing.setCountry(CZ)
    invoicing.setInvoicingDate(INVOICING_DATE)
    invoicing.setNumberingBase(NUMBERING_BASE)

    context.vatRate() >> 20
    context.purgeDays() >> 14

    billing.setIsActive(true)
    billing.setIsBilledAfter(false)
    billing.setFrequency(Frequency.MONTHLY)
    billing.setLastlyBilled(date('2011-09-30'))
    customer.setBilling(billing)

    recur.setFrequency(Frequency.MONTHLY)
    recur.setName('recur')
  }

  def 'instantiates from invoicing and BillingContext'() {
  expect:
    new BillFactory(invoicing, context) != null
  }

  @Ignore
  def 'creates bill for given customer'() {
    def factory = new BillFactory(invoicing, context)
    def bill
  when:
     bill = factory.createBillFor(customer)
  then:
    bill != null
  }

  static def Date date(String date) {
    Date.parse(DF, date)
  }
}
