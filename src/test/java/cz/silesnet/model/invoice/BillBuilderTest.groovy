package cz.silesnet.model.invoice

import cz.silesnet.model.enums.BillingStatus
import cz.silesnet.model.enums.Country
import cz.silesnet.model.enums.Frequency
import org.mockito.Mockito
import spock.lang.Specification
import cz.silesnet.model.*
import static spock.util.matcher.HamcrestMatchers.*

/**
 * User: der3k
 * Date: 9.3.11
 * Time: 8:22
 */
class BillBuilderTest extends Specification {

  def 'customer updates billing and services from builder by calling builders method with itself'() {
    def builder = Mockito.mock(BillBuilder)
    def customer = new Customer()
  when:
    customer.updateBillingAndServicesAfterBilledWith(builder)
  then:
    Mockito.verify(builder).updateBillingAndServicesOf(customer)
  }

  def "builder updates customer's lastly billed date after build to adjusted bill period end"() {
    def builder = buildableBillBuilderDueOn20110105()
    def customer = builder.customer
    builder.build(accountantWithNumberingBase2011000AndVatRate20())
  when:
    builder.updateBillingAndServicesOf(customer)
  then:
    customer.getBilling().getLastlyBilled() == builder.adjustedBillPeriod.getTo()
  }

  def 'builder removes billed one time services from customer after build'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    customer.setServices([monthlyServiceRunningFromJan2010WithPrice10(),
        oneTimeServiceForJan2011WithPrice10()])
    def builder = new BillBuilder(customer, date('2011-01-05'))
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  when:
    builder.updateBillingAndServicesOf(customer)
  then:
    bill.getItems().size() == 2
    customer.getServices().size() == 1
    customer.getServices()[0].getFrequency() == Frequency.MONTHLY
  }

  def "builder fails to update customer's billing and services when other customer given"() {
    def builder = buildableBillBuilderDueOn20110105()
    builder.build(accountantWithNumberingBase2011000AndVatRate20())
  when:
    builder.updateBillingAndServicesOf(new Customer())
  then:
    thrown IllegalArgumentException
  }

  def "builder fails to update customer's billing and services when bill was not build yet"() {
    def builder = buildableBillBuilderDueOn20110105()
    def customer = builder.customer
  when:
    builder.updateBillingAndServicesOf(customer)
  then:
    thrown IllegalStateException
  }

  def 'build sets purge date using billing context'() {
    def builder = buildableBillBuilderDueOn20110105()
    def context = contextWithVatRate20MockitoMock()
    Mockito.when(context.purgeDateFor(date('2011-01-05'))).thenReturn(date('2011-01-19'))
  when:
    def bill = builder.build(new Accountant(invoicingWithNumberingBase2011000(), context))
  then:
    bill.getPurgeDate() == date('2011-01-19')
  }

  def 'build sets vat rate from billing context'() {
    def builder = buildableBillBuilderDueOn20110105()
  when:
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  then:
    bill.getVat() == 20
  }

  def 'build sets confirmed flag to true'() {
    def builder = buildableBillBuilderDueOn20110105()
  when:
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  then:
    bill.getIsConfirmed()
  }

  def 'build sets invoicingId'() {
    def builder = buildableBillBuilderDueOn20110105()
    def invoicing = invoicingWithNumberingBase2011000()
    invoicing.setId(1000L)
  when:
    def bill = builder.build(new Accountant(invoicing, contextWithVatRate20MockitoMock()))
  then:
    bill.getInvoicingId() == 1000L
  }

  def 'build fails when there are errors'() {
    def builder = buildableBillBuilderDueOn20110105()
    builder.errors << 'error'
  when:
    builder.build(null)
  then:
    thrown IllegalStateException
  }

  def 'build fails when trying to build twice'() {
    def builder = buildableBillBuilderDueOn20110105()
    def accountant = new Accountant(invoicingWithNumberingBase2011000(), contextWithVatRate20MockitoMock())
    builder.build(accountant)
  when:
    builder.build(accountant)
  then:
    thrown IllegalStateException
  }

  def 'build populates due date'() {
    def builder = buildableBillBuilderDueOn20110105()
  when:
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  then:
    bill.getBillingDate() == date('2011-01-05')
  }

  def 'build populates adjusted bill period'() {
    def builder = buildableBillBuilderDueOn20110105()
    builder.adjustedBillPeriod = period('2010-01-01', '2010-01-01')
  when:
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  then:
    bill.getPeriod() == period('2010-01-01', '2010-01-01')
  }

  def 'build populates delivery by email flag'() {
    def builder = buildableBillBuilderDueOn20110105()
    builder.customer.getBilling().setDeliverByMail(true)
  when:
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  then:
    bill.getDeliverByMail()
  }

  def 'build populates each item with bill reference'() {
    def builder = buildableBillBuilderDueOn20110105()
  when:
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  then:
    bill.getItems()[0].getBill() == bill
  }

  def "build populates customer's data"() {
    def builder = buildableBillBuilderDueOn20110105()
    builder.customer.setId(1L)
    builder.customer.setName('Customer Name')
  when:
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  then:
    bill.getInvoicedCustomer() == builder.customer
    bill.getCustomerId() == 1L
    bill.getCustomerName() == 'Customer Name'
  }

  def "build sets bill hash-code"() {
    def builder = buildableBillBuilderDueOn20110105()
    builder.customer.setId(1L)
  when:
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  then:
    bill.getHashCode()[0..-4] == (Long.toHexString(1000001) + Long.toHexString(new Date().getTime()))[0..-4]
  }

  def 'build sets bill number from invoicing'() {
    def builder = buildableBillBuilderDueOn20110105()
  when:
    def bill = builder.build(accountantWithNumberingBase2011000AndVatRate20())
  then:
    bill.getNumber() == '2011001'
  }

  def 'builder would skip zero priced items except for one-time'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    def monthlyZeroService = monthlyServiceRunningFromJan2010WithPrice10()
    monthlyZeroService.setPrice(0)
    customer.getServices() << monthlyZeroService
  when:
    def builder = new BillBuilder(customer, date('2011-01-05'))
  then:
    builder.errors().contains('billing.noBillItems')
    builder.warnings().contains('billing.zeroItemSkipped')
  }

  def 'skipped zero priced item would not affect adjusted bill period'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    def monthlyZeroService = monthlyServiceRunningFromJan2010WithPrice10()
    monthlyZeroService.setPrice(0)
    monthlyZeroService.getPeriod().setFrom(date('2011-01-02')) // try to ensure bill period adjustment
    customer.getServices() << monthlyZeroService
  when:
    def builder = new BillBuilder(customer, date('2011-01-05'))
  then:
    builder.adjustedBillPeriod == period('2011-01-01', '2011-01-31')
  }

  def 'builder would not skip zero priced one-time items'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    def oneTimeZeroService = oneTimeServiceForJan2011WithPrice10()
    oneTimeZeroService.setPrice(0)
    customer.getServices() << oneTimeZeroService
  when:
    def builder = new BillBuilder(customer, date('2011-01-05'))
  then:
    builder.wouldBuild()
    builder.items.size() == 1
  }

  def 'build item adjusts bill period according to billed services periods'() {
    def builder = new BillBuilder(new Customer(), new Date())
    def service = monthlyServiceRunningFromJan2010WithPrice10()
  when:
    builder.buildItemFor(service, period('2011-04-10', '2011-04-25'))
    builder.buildItemFor(service, period('2011-04-10', '2011-04-26'))
    builder.buildItemFor(service, period('2011-04-09', '2011-04-25'))
    builder.buildItemFor(service, period('2011-04-08', '2011-04-27'))
    builder.buildItemFor(service, period('2011-04-11', '2011-04-24'))
  then:
    builder.adjustedBillPeriod == period('2011-04-08', '2011-04-27')
  }

  def 'build item ignores billed one-time service periods when adjusting bill period'() {
    def builder = new BillBuilder(new Customer(), new Date())
    def monthly = monthlyServiceRunningFromJan2010WithPrice10()
    def oneTime = monthlyServiceRunningFromJan2010WithPrice10()
    oneTime.setFrequency(Frequency.ONE_TIME)
  when:
    builder.buildItemFor(monthly, period('2011-04-10', '2011-04-25'))
    builder.buildItemFor(oneTime, period('2011-04-01', '2011-04-30'))
  then:
    builder.adjustedBillPeriod == period('2011-04-10', '2011-04-25')
  }

  def 'adjusted bill period is set to original bill period when no adjustments done'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    def onetimeService = oneTimeServiceForJan2011WithPrice10()
    customer.getServices() << onetimeService
  when:
    def builder = new BillBuilder(customer, date('2011-01-05'))
  then:
    builder.adjustedBillPeriod == builder.billPeriod
  }

  def 'build item populates service bill item text'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    customer.getContact().getAddress().setCountry(Country.CZ)
    def builder = new BillBuilder(customer, new Date())
    def service = monthlyServiceRunningFromJan2010WithPrice10()
    service.setName(name)
    def connectivity = new Connectivity()
    connectivity.setDownload(1000)
    service.setConnectivity(connectivity)
  expect:
    builder.buildItemFor(service, period('2011-01-01', '2011-01-31')).getText() == service.getBillItemText(Country.CZ)
  where:
    name << ["", "Test Name"]
  }

  def 'build item populates service price'() {
    def builder = new BillBuilder(new Customer(), new Date())
    def service = oneTimeServiceForJan2011WithPrice10()
    service.setPrice(price)
  expect:
    builder.buildItemFor(service, period('2011-01-01', '2011-01-31')).getPrice() == price
  where:
    price << [-1000, -1, 0, 1, 1000]
  }

  def 'build item calculates amount based on service frequency and period'() {
    def builder = new BillBuilder(new Customer(), new Date())
    def service = monthlyServiceRunningFromJan2010WithPrice10()
    service.setFrequency(frequency)
  expect:
    builder.buildItemFor(service, period('2011-01-01', '2011-01-31')).getAmount() closeTo(amount, 0.01)
  where:
    frequency | amount
    Frequency.ONE_TIME | 1.0F
    Frequency.DAILY | 31.0F
    Frequency.WEEKLY | 4.43F
    Frequency.MONTHLY | 1.0F
    Frequency.Q | 0.33F
    Frequency.QQ | 0.17F
    Frequency.ANNUAL | 0.08F
  }

  def 'build item calculates amount correctly'() {
    def builder = new BillBuilder(new Customer(), new Date())
    def service = monthlyServiceRunningFromJan2010WithPrice10()
  expect:
    builder.buildItemFor(service, period).getAmount() closeTo(amount, 0.01)
  where:
    period | amount
    period('2011-01-01', '2011-01-31') | 1.0F
    period('2011-01-01', '2011-01-01') | 0.03F
    period('2011-04-01', '2011-04-15') | 0.5F
    period('2011-04-16', '2011-04-30') | 0.5F
    period('2011-04-16', '2011-06-30') | 2.5F
  }

  def 'build item sets print unit flag based on service frequency'() {
    def builder = new BillBuilder(new Customer(), new Date())
    def service = monthlyServiceRunningFromJan2010WithPrice10()
    service.setFrequency(frequency)
  expect:
    builder.buildItemFor(service, period('2011-01-01', '2011-01-31')).getIsDisplayUnit() == display
  where:
    frequency | display
    Frequency.ONE_TIME | false
    Frequency.DAILY | true
    Frequency.WEEKLY | true
    Frequency.MONTHLY | true
    Frequency.Q | true
    Frequency.QQ | true
    Frequency.ANNUAL | true
  }

  def 'build item fails for non existing period'() {
    def builder = new BillBuilder(new Customer(), new Date())
    def service = monthlyServiceRunningFromJan2010WithPrice10()
  when:
    builder.buildItemFor(service, period('2011-01-02', '2011-01-01'))
  then:
    thrown(IllegalArgumentException)
  }

  def 'builder skips services that have no intersection with bill period'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    def service = monthlyServiceRunningFromJan2010WithPrice10()
    service.getPeriod().setTo(date('2010-12-31'))
    customer.getServices() << service
  when:
    def builder = new BillBuilder(customer, date('2011-01-05'))
  then:
    builder.warnings().contains('billing.serviceOutOfPeriodSkipped')
  }

  def 'instantiates from customer and due date'() {
  expect:
    new BillBuilder(new Customer(), new Date()) != null
  }

  def 'customer must not be null'() {
  when:
    new BillBuilder(null, new Date())
  then:
    thrown(IllegalArgumentException)
  }

  def 'due date must not be null'() {
  when:
    new BillBuilder(new Customer(), null)
  then:
    thrown(IllegalArgumentException)
  }

  def 'would not build when customer is not active'() {
    def customer = new Customer()
    customer.getBilling().setIsActive(false)
  when:
    def builder = new BillBuilder(customer, new Date())
  then:
    builder.errors().contains("billing.customerNotActive")
  }

  def "would not build when customer's billing is turned off"() {
    def customer = new Customer()
    customer.getBilling().setStatus(BillingStatus.VIP)
  when:
    def builder = new BillBuilder(customer, new Date())
  then:
    builder.errors().contains("billing.billingDisabled")
  }

  def 'would not build when there is no billing period for due date'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
  when:
    def builder = new BillBuilder(customer, date('2010-12-05'))
  then:
    builder.errors().contains("billing.noBillForPeriod")
  }

  def 'would not build when there are no services'() {
    def customer = new Customer()
    customer.setServices([] as List<Service>)
  when:
    def builder = new BillBuilder(customer, new Date())
  then:
    builder.errors().contains("billing.customerHasNoServices")
  }

  def 'would not build when there are no bill items'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    def service = monthlyServiceRunningFromJan2010WithPrice10()
    service.getPeriod().setTo(date('2010-12-31'))
    customer.getServices() << service
  when:
    def builder = new BillBuilder(customer, date('2011-01-05'))
  then:
    builder.errors().contains("billing.noBillItems")
  }

  def 'would not build when zero price bill and no one-time item'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    def service = monthlyServiceRunningFromJan2010WithPrice10()
    service.setPrice(0)
    customer.getServices() << service
  when:
    def builder = new BillBuilder(customer, date('2011-01-05'))
  then:
    builder.errors().contains("billing.zeroBillWithoutOneTimeItem")
  }

  def 'would not build when negative amount bill'() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    def service = monthlyServiceRunningFromJan2010WithPrice10()
    service.setPrice(-1)
    customer.getServices() << service
  when:
    def builder = new BillBuilder(customer, date('2011-01-05'))
  then:
    builder.errors().contains("billing.negativeAmountBill")
  }

  def 'billable period for one-time service is always set to bill period'() {
    def builder = new BillBuilder(activeCustomerBilledMonthlyForwardUpToDec2010(), date('2011-01-05'))
    def service = oneTimeServiceForJan2011WithPrice10()
    service.setPeriod(null)
    def billPeriod = period('2011-01-01', '2011-01-31')
  expect:
    builder.billPeriod == billPeriod // double check
    builder.billablePeriodFor(service) == billPeriod
  }


  static def Accountant accountantWithNumberingBase2011000AndVatRate20() {
    new Accountant(invoicingWithNumberingBase2011000(), contextWithVatRate20MockitoMock())
  }

  static def BillBuilder buildableBillBuilderDueOn20110105() {
    def customer = activeCustomerBilledMonthlyForwardUpToDec2010()
    def service = monthlyServiceRunningFromJan2010WithPrice10()
    customer.getServices() << service
    new BillBuilder(customer, date('2011-01-05'))
  }

  static def Customer activeCustomerBilledMonthlyForwardUpToDec2010() {
    def customer = new Customer()
    customer.setId(1L)
    customer.setName('Customer Name')
    def billing = customer.getBilling()
    billing.setIsActive(true)
    billing.setFrequency(Frequency.MONTHLY)
    billing.setIsBilledAfter(false)
    billing.setLastlyBilled(date('2010-12-31'))
    customer
  }

  static def Service monthlyServiceRunningFromJan2010WithPrice10() {
    def service = new Service()
    service.setFrequency(Frequency.MONTHLY)
    service.setPeriod(new Period(date('2010-01-01'), null))
    service.setName("Test monthly service")
    service.setPrice(10)
    service
  }

  static def Service oneTimeServiceForJan2011WithPrice10() {
    def service = new Service()
    service.setFrequency(Frequency.ONE_TIME)
    service.setPeriod(period('2011-01-01', '2011-01-31'))
    service.setName("Test one time service")
    service.setPrice(10)
    service
  }

  static def BillingContext contextWithVatRate20MockitoMock() {
    def context = Mockito.mock(BillingContext)
    Mockito.when(context.calculateVatFor(Mockito.any())).thenReturn(Amount.of(20))
    return context
  }

  static def Invoicing invoicingWithNumberingBase2011000() {
    def invoicing = new Invoicing()
    invoicing.setNumberingBase('2011000')
    invoicing
  }

  static def Period period(String from, String to) {
    new Period(date(from), date(to))
  }

  static def Date date(String date) {
    Date.parse('yyyy-MM-dd', date)
  }
}
