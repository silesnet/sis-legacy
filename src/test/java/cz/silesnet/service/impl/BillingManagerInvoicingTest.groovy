package cz.silesnet.service.impl

import cz.silesnet.model.Customer
import cz.silesnet.model.Invoicing
import cz.silesnet.service.HistoryManager
import spock.lang.Specification

/**
 * User: der3k
 * Date: 20.4.11
 * Time: 7:49
 */
class BillingManagerInvoicingTest extends Specification {
  def 'audits bill building errors'() {
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

  }
}
