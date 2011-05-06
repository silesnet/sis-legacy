package cz.silesnet.model.invoice

import cz.silesnet.model.Bill
import cz.silesnet.model.Customer
import cz.silesnet.model.Invoicing
import spock.lang.Specification

/**
 * User: der3k
 * Date: 30.4.11
 * Time: 12:51
 */
class AccountantTest extends Specification {
  def 'bills customer'() {
    def customer = Mock(Customer)
    def bill = Mock(Bill)
    def builder = Mock(BillBuilder)
    def context = Mock(BillingContext)
    def invoicing = invoicingWithNumberingBase2011000()
    def accountant = new Accountant(invoicing, context) {
      @Override protected BillBuilder newBillBuilder(Customer aCustomer, Date due) {
        return builder
      }
    }
  when:
    def result = accountant.bill(customer)
  then:
    1 * builder.wouldBuild() >> true
    1 * builder.build(accountant) >> bill
    1 * customer.updateBillingAndServicesAfterBilledWith(builder)
    1 * builder.warnings() >> ['warning']
    result.isSuccess()
    result.bill() == bill
    result.customer() == customer
    result.warnings() == ['warning']
    result.errors() == []
  }

  def 'provides errors when builder fails'() {
    def customer = Mock(Customer)
    def builder = Mock(BillBuilder)
    def context = Mock(BillingContext)
    def invoicing = invoicingWithNumberingBase2011000()
    def accountant = new Accountant(invoicing, context) {
      @Override protected BillBuilder newBillBuilder(Customer aCustomer, Date due) {
        return builder
      }
    }
  when:
    def result = accountant.bill(customer)
  then:
    1 * builder.wouldBuild() >> false
    1 * builder.errors() >> ['error']
    !result.isSuccess()
    result.bill() == null
    result.customer() == null
    result.warnings() == []
    result.errors() == ['error']
  }

  def 'provides errors when runtime exeption is thrown'() {
    def accountant = new Accountant(invoicingWithNumberingBase2011000(), Mock(BillingContext)) {
      @Override protected BillBuilder newBillBuilder(Customer aCustomer, Date due) {
        throw new RuntimeException()
      }
    }
  when:
    def result = accountant.bill(Mock(Customer))
  then:
    !result.isSuccess()
    result.bill() == null
    result.customer() == null
    result.warnings() == []
    result.errors() == ['billing.error']
  }

 def "provides invoice numbers based on invoicing's numbering base" () {
   def accountant = new Accountant(invoicingWithNumberingBase2011000(), Mock(BillingContext))
   expect:
    accountant.nextBillNumber() == '2011001'
    accountant.nextBillNumber() == '2011002'
    accountant.nextBillNumber() == '2011003'
    accountant.nextBillNumber() == '2011004'
 }

  def 'counters are set to zero right after instantiation'() {
    def accountant = new Accountant(invoicingWithNumberingBase2011000(), null)
  expect:
    accountant.billedCount() == 0
    accountant.skippedCount() == 0
    accountant.errorsCount() == 0
  }

  def 'counts billed customers'() {
    def customer = Mock(Customer)
    def bill = Mock(Bill)
    def builder = Mock(BillBuilder)
    def context = Mock(BillingContext)
    def invoicing = invoicingWithNumberingBase2011000()
    def accountant = new Accountant(invoicing, context) {
      @Override protected BillBuilder newBillBuilder(Customer aCustomer, Date due) {
        return builder
      }
    }
    builder.wouldBuild() >> true
    builder.build(invoicing, context) >> bill
    accountant.bill(customer)
  expect:
    accountant.billedCount() == 1
  }

  def 'counts skipped customers'() {
    def customer = Mock(Customer)
    def builder = Mock(BillBuilder)
    def context = Mock(BillingContext)
    def invoicing = invoicingWithNumberingBase2011000()
    def accountant = new Accountant(invoicing, context) {
      @Override protected BillBuilder newBillBuilder(Customer aCustomer, Date due) {
        return builder
      }
    }
    builder.wouldBuild() >> false
    accountant.bill(customer)
  expect:
    accountant.skippedCount() == 1
  }

  def 'counts billing errors'() {
    def customer = Mock(Customer)
    def context = Mock(BillingContext)
    def invoicing = invoicingWithNumberingBase2011000()
    def accountant = new Accountant(invoicing, context) {
      @Override protected BillBuilder newBillBuilder(Customer aCustomer, Date due) {
        throw new RuntimeException()
      }
    }
    accountant.bill(customer)
  expect:
    accountant.errorsCount() == 1
  }

  def 'sum of counters gives number of processed customers'() {
    def customer = Mock(Customer)
    def bill = Mock(Bill)
    def builder = Mock(BillBuilder)
    def context = Mock(BillingContext)
    def invoicing = invoicingWithNumberingBase2011000()
    def accountant = new Accountant(invoicing, context) {
      @Override protected BillBuilder newBillBuilder(Customer aCustomer, Date due) {
        return builder
      }
    }
    builder.wouldBuild() >>> [true, false, true, true, true]
    //                        bill  skip   bill  throw bill
    builder.build(accountant) >>> [bill, bill, { throw new RuntimeException() }, bill]
  when:
    5.times {
      accountant.bill(customer)
    }
  then:
    accountant.processedCount() == 5
    accountant.billedCount() == 3
    accountant.skippedCount() == 1
    accountant.errorsCount() == 1
  }

  def 'has invoicing'() {
    def invoicing = invoicingWithNumberingBase2011000()
    def accountant = new Accountant(invoicing, Mock(BillingContext))
  expect:
    accountant.invoicing() == invoicing
  }

  def 'has billing context'() {
    def context = Mock(BillingContext)
    def accountant = new Accountant(invoicingWithNumberingBase2011000(), context)
  expect:
    accountant.billingContext() == context
  }
  
  private static Invoicing invoicingWithNumberingBase2011000() {
    def invoicing = new Invoicing()
    invoicing.setNumberingBase('2011000')
    invoicing
    return invoicing
  }

}