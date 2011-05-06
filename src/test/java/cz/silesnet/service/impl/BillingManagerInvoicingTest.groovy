package cz.silesnet.service.impl

import cz.silesnet.dao.BillDAO
import cz.silesnet.dao.CustomerDAO
import cz.silesnet.model.Bill
import cz.silesnet.model.Customer
import cz.silesnet.model.Invoicing
import cz.silesnet.model.enums.Country
import cz.silesnet.model.invoice.Accountant
import cz.silesnet.model.invoice.BillingContext
import cz.silesnet.model.invoice.BillingContextFactory
import cz.silesnet.model.invoice.BillingResult
import cz.silesnet.service.HistoryManager
import spock.lang.Specification

/**
 * User: der3k
 * Date: 20.4.11
 * Time: 7:49
 */
class BillingManagerInvoicingTest extends Specification {

  def 'bills customers'() {
    def customerDao = Mock(CustomerDAO)
    def billDao = Mock(BillDAO)
    def contextFactory = Mock(BillingContextFactory)

    def accountant = Mock(Accountant)
    def bMgr = new BillingManagerImpl() {
      @Override protected Accountant newAccountantFor(Invoicing invoicing, BillingContext context) {
        return accountant
      }

      @Override protected void auditBillingStart(Accountant anAccountant) {}
      @Override protected void auditBillingFinished(Accountant anAccountant) {}
    }

    bMgr.setCustomerDao(customerDao)
    bMgr.setBillDAO(billDao)
    bMgr.setBillingContextFactory(contextFactory)

    def customer = Mock(Customer)
    def bill = Mock(Bill)
    def invoicing = Mock(Invoicing)
    def result = BillingResult.success(bill, customer, [])
  when:
    bMgr.billCustomersIn(invoicing)
  then:
    1 * customerDao.findActiveCustomerIdsByCountry(_) >> [1L]
    1 * customerDao.get(1L) >> customer
    1 * accountant.bill(customer) >> result
    1 * billDao.save(bill)
    1 * customerDao.save(customer)
  }

  def 'audits billing errors'() {
    def errorsAudited = false
    def customerDao = Mock(CustomerDAO)
    def contextFactory = Mock(BillingContextFactory)

    def accountant = Mock(Accountant)
    def bMgr = new BillingManagerImpl() {
      @Override protected Accountant newAccountantFor(Invoicing invoicing, BillingContext context) {
        return accountant
      }

      @Override protected void auditBillBuildingErrors(Invoicing invoicing, Customer customer, List<String> errors) {
        if (errors[0] == 'error')
          errorsAudited = true
      }
      @Override protected void auditBillingStart(Accountant anAccountant) {}
      @Override protected void auditBillingFinished(Accountant anAccountant) {}
    }
    bMgr.setCustomerDao(customerDao)
    bMgr.setBillingContextFactory(contextFactory)
    bMgr.setHistoryManager(Mock(HistoryManager))

    def customer = Mock(Customer)
    def invoicing = Mock(Invoicing)
    def result = BillingResult.failure(['error'])
  when:
    bMgr.billCustomersIn(invoicing)
  then:
    1 * customerDao.findActiveCustomerIdsByCountry(_) >> [1L]
    1 * customerDao.get(1L) >> customer
    1 * accountant.bill(customer) >> result
    errorsAudited == true
  }

  def "bills customers of invoicing's country"() {
    def bMgr = new BillingManagerImpl() {
      @Override protected void auditBillingStart(Accountant anAccountant) {}
      @Override protected void auditBillingFinished(Accountant anAccountant) {}
    }
    def customerDao = Mock(CustomerDAO)
    bMgr.setCustomerDao(customerDao)
    bMgr.setBillingContextFactory(Mock(BillingContextFactory))
    bMgr.setHistoryManager(Mock(HistoryManager))
    def invoicing = invoicingWithNumberingBase2011000()
    invoicing.setCountry(Country.PL)
  when:
    bMgr.billCustomersIn(invoicing)
  then:
    1 * customerDao.findActiveCustomerIdsByCountry(Country.PL)
    thrown(NullPointerException)
  }

  def "bills customers using invoicing's billing context"() {
    def bMgr = new BillingManagerImpl()
    def contextFactory = Mock(BillingContextFactory)
    bMgr.setBillingContextFactory(contextFactory)
    bMgr.setCustomerDao(Mock(CustomerDAO))
    def invoicing = invoicingWithNumberingBase2011000()
  when:
    bMgr.billCustomersIn(invoicing)
  then:
    1 * contextFactory.billingContextFor(invoicing.getCountry())
    thrown(NullPointerException)
  }

  def 'can audit bill building errors'() {
    def bmgr = new BillingManagerImpl()
    def hmgr = Mock(HistoryManager)
    bmgr.setHistoryManager(hmgr)
    def errors = ['error']
    def invoicing = new Invoicing()
    def customer = new Customer()
  when:
    bmgr.auditBillBuildingErrors(invoicing, customer, errors)
  then:
    1 * hmgr.insertSystemBillingAudit(invoicing, customer, 'error', 'mainBilling.status.skipped')
  }

  def 'errors audit is mapping message keys to legacy values'() {
    def bmgr = new BillingManagerImpl()
    def hmgr = Mock(HistoryManager)
    bmgr.setHistoryManager(hmgr)
    def invoicing = new Invoicing()
    def customer = new Customer()
  when:
    bmgr.auditBillBuildingErrors(invoicing, customer, [key])
  then:
    1 * hmgr.insertSystemBillingAudit(invoicing, customer, legacy, 'mainBilling.status.skipped')
  where:
    key | legacy
    "billing.customerNotActive" | "mainBilling.status.deactivated"
    "billing.billingDisabled" | "mainBilling.msg.billinDisabled"
    "billing.customerHasNoServices" | "mainBilling.msg.no-active-services"
    "billing.noBillForPeriod" | "mainBilling.msg.invalidPeriod"
    "billing.noBillItems" | "mainBilling.msg.no-active-services"
    "billing.zeroBillWithoutOneTimeItem" | "mainBilling.msg.zeroInvoice"
    "billing.negativeAmountBill" | "mainBilling.msg.negativeInvoice"
    "billing.error" | "mainBilling.msg.illegalArgument"
  }

  def 'invoicing billing start audit contains invoicing date and numbering base'() {
    def audit = Mock(HistoryManager)
    def manager = new BillingManagerImpl()
    manager.setHistoryManager(audit)

    def invoicing = invoicingWithNumberingBase2011000()
    invoicing.setInvoicingDate(date('2011-01-05'))
    def accountant = new Accountant(invoicing, null)
  when:
    manager.auditBillingStart(accountant)
  then:
    1 * audit.insertSystemBillingAudit(invoicing, null, 'mainBilling.msg.billingStarted', '05.01.2011, 2011000 ***')
  }

  def 'invoicing billing finished audit contains status'() {
    def audit = Mock(HistoryManager)
    def manager = new BillingManagerImpl()
    manager.setHistoryManager(audit)

    def accountant = Mock(Accountant)
    def invoicing = Mock(Invoicing)
  when:
    manager.auditBillingFinished(accountant)
  then:
    1 * accountant.billedCount() >> 10
    1 * accountant.skippedCount() >> 3
    1 * accountant.errorsCount() >> 0
    1 * accountant.processedCount() >> 13
    1 * accountant.invoicing() >> invoicing
    1 * audit.insertSystemBillingAudit(invoicing, null, 'mainBilling.msg.billingFinished', '10/3/0/13 ***')
  }

  def 'audits invoicing billing start and finish'() {
    def theAccountant
    def startAudited = false
    def finishAudited = false
    def manager = new BillingManagerImpl() {
      @Override protected void auditBillingStart(Accountant accountant) {
        theAccountant = accountant
        if (!finishAudited)
          startAudited = true
      }

      @Override protected void auditBillingFinished(Accountant accountant) {
        if (accountant == theAccountant && startAudited)
          finishAudited = true
      }
    }
    def contextFactory = Mock(BillingContextFactory)
    def customerDao = Mock(CustomerDAO)
    manager.setBillingContextFactory(contextFactory)
    manager.setCustomerDao(customerDao)

    customerDao.findActiveCustomerIdsByCountry(_) >> []
    def invoicing = invoicingWithNumberingBase2011000()
  when:
    manager.billCustomersIn(invoicing)
  then:
    startAudited
    finishAudited
  }

  def 'groovy stubbing'() {
    def accountant = Mock(Accountant)
    def bmgr = new BillingManagerImpl()
    BillingManagerImpl.metaClass.newAccountantFor = {customer, due -> accountant }
  expect:
    bmgr.newAccountantFor(null, null) == accountant
  }

  static def Invoicing invoicingWithNumberingBase2011000() {
    def invoicing = new Invoicing()
    invoicing.setNumberingBase('2011000')
    invoicing
  }

  static def Date date(String date) {
    Date.parse('yyyy-MM-dd', date)
  }

}
