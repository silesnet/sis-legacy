package cz.silesnet.service.impl;

import cz.silesnet.dao.CustomerDAO;
import cz.silesnet.model.*;
import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;
import cz.silesnet.service.BillingManager;
import cz.silesnet.service.CustomerManager;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * User: der3k
 * Date: 10.5.11
 * Time: 8:06
 */
public class BillingManagerTransactionIntegrationTest {

  @Test(groups = "integration")
  public void testBillingIsTransactional() throws Exception {
    String[] paths = {"context/sis-properties.xml", "context/sis-db.xml", "context/sis-hibernate.xml",
        "context/sis-dao.xml", "context/sis-transaction.xml", "context/sis-service.xml",
        "context/sis-email.xml", "context/sis-template.xml", "context/sis-billing.xml"};
    ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);

    BillingManager bmgr = ctx.getBean("billingManager", BillingManager.class);
    CustomerManager cmgr = ctx.getBean("customerManager", CustomerManager.class);
    final CustomerDAO cdao = ctx.getBean("customerDAO", CustomerDAO.class);
    DataSource source = ctx.getBean("dataSource", DataSource.class);

    cleanDatabase(source);

    // persist two billable customers
    Customer customer1 = customerFixture(1);
    cmgr.insert(customer1);
    Customer customer2 = customerFixture(2);
    cmgr.insert(customer2);
    assertThat(cmgr.getAll().size(), is(2));

    // initial invoicing to see that everything is in place
    Invoicing invoicing = invoicingFixture("2011-01-05", "2011000");
    bmgr.insertInvoicing(invoicing);
    assert bmgr.getInvoicings(Country.CZ).size() == 1;
    bmgr.billCustomersIn(invoicing);
    assert bmgr.getInvoices(invoicing).size() == 2;

    // second invoicing that should trigger transaction rollback
    Invoicing invoicing2 = invoicingFixture("2011-02-05", "2011090");
    bmgr.insertInvoicing(invoicing2);
    assert bmgr.getInvoicings(Country.CZ).size() == 2;
    // CustomerDAO that would throw exception while trying to save the second customer
    final Long c2Id = customer2.getId();
    CustomerDAO newCdao = new CustomerDAO() {
      public List<Customer> getAll() { return cdao.getAll(); }

      public List<Customer> getByExample(final Customer customerExample) { return cdao.getByExample(customerExample); }

      public void evict(final Customer customer) {cdao.evict(customer); }

      public Customer get(final Long customerId) { return cdao.get(customerId); }

      public Customer load(final Long customerId) { return cdao.load(customerId); }

      public void remove(final Customer customer) { cdao.remove(customer); }

      public void save(final Customer customer) {
        if (customer.getId().equals(c2Id))
          throw new RuntimeException("IT SHOULD CAUSE ROLLBACK");
        cdao.save(customer);
      }

      public void saveAll(final List<Customer> customers) { cdao.saveAll(customers); }

      public int getTotalCustomers(final Country c) { return cdao.getTotalCustomers(c); }

      public Iterable<Long> findActiveCustomerIdsByCountry(final Country country) {
        return cdao.findActiveCustomerIdsByCountry(country);
      }
    };
    // reset customerDao on target bean, behind Springs' proxy
    BillingManagerImpl targetBmgr = getTargetObject(bmgr, BillingManagerImpl.class);
    targetBmgr.setCustomerDao(newCdao);

    try {
      bmgr.billCustomersIn(invoicing2);
      fail();
    } catch (RuntimeException e) {
      // expected
    }

    // there was exception so no invoices added, transaction roll back
    assertThat(bmgr.getInvoices(invoicing2).size(), is(0));
    assertThat(cdao.get(customer1.getId()).getBilling().getLastlyBilled(), is((date("2011-01-31"))));
    assertThat(cdao.get(customer2.getId()).getBilling().getLastlyBilled(), is((date("2011-01-31"))));

    // clean up
    cleanDatabase(source);
  }

  private void cleanDatabase(final DataSource source) throws Exception {
    JdbcTemplate template = new JdbcTemplate(source);
    template.execute("delete from audit_items");
    template.execute("delete from bill_items");
    template.execute("delete from bills");
    template.execute("delete from invoicings");
    template.execute("delete from services");
    template.execute("delete from customers");
  }

  // http://www.techper.net/2009/06/05/how-to-acess-target-object-behind-a-spring-proxy/
  @SuppressWarnings({"unchecked"})
  protected <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
    if (AopUtils.isJdkDynamicProxy(proxy)) {
      return (T) ((Advised) proxy).getTargetSource().getTarget();
    } else {
      return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
    }
  }

  private Invoicing invoicingFixture(final String due, final String numbering) {
    Invoicing invoicing = new Invoicing();
    invoicing.setCountry(Country.CZ);
    invoicing.setInvoicingDate(date(due));
    invoicing.setNumberingBase(numbering);
    invoicing.setName(invoicing.getProposedName());
    return invoicing;
  }

  private Customer customerFixture(int i) {
    Customer customer = new Customer();
    customer.setName("Customer " + i);
    customer.setContractNo("2011/00" + i);
    customer.setPublicId("1234567" + i);
    customer.setInsertedOn(new Date());
    customer.setContact(contactFixture(i));
    customer.setBilling(billingFixture(i));
    customer.getServices().add(serviceFixture(i));
    return customer;
  }

  private Contact contactFixture(int i) {
    Address address = new Address();
    address.setStreet("Street 11" + i);
    address.setCity("New Town");
    address.setPostalCode("12345");
    address.setCountry(Country.CZ);
    Contact contact = new Contact();
    contact.setAddress(address);
    return contact;
  }

  private Billing billingFixture(int i) {
    Billing billing = new Billing();
    billing.setLastlyBilled(date("2010-12-31"));
    billing.setFrequency(Frequency.MONTHLY);
    billing.setIsActive(true);
    billing.setIsBilledAfter(false);
    billing.setStatus(BillingStatus.INVOICE);
    return billing;
  }

  private Service serviceFixture(int i) {
    Service service = new Service();
    service.setName("Service " + i);
    service.setPrice(100);
    service.setFrequency(Frequency.MONTHLY);
    service.setPeriod(new Period(date("2010-01-01"), null));
    service.setConnectivity(null);
    return service;
  }

  private static Date date(String date) {
    try {
      return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

}
