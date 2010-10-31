package cz.silesnet.service.invoice.impl;

import cz.silesnet.model.*;
import cz.silesnet.model.enums.Country;
import cz.silesnet.service.invoice.Invoice;
import cz.silesnet.service.invoice.InvoiceFormat;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.GregorianCalendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * User: der3k
 * Date: 31.10.2010
 * Time: 20:41:24
 */
public class InvoiceTest {
  private Bill bill;
  private Customer customer;
  private Invoice invoice;

  @BeforeMethod
  private void setUp() {
    prepareInvoiceFixture();
  }

  @AfterMethod
  private void tearDown() {
    bill = null;
    customer = null;
    invoice = null;
  }

  @Test
  public void getCountry() throws Exception {
    Address address = new Address();
    address.setCountry(Country.PL);
    Contact contact = new Contact();
    contact.setAddress(address);
    customer.setContact(contact);
    assertThat(invoice.getCountry(), is(Country.PL));
  }

  @Test
  public void getShortFormatInLowerCase() throws Exception {
    Billing billing = new Billing();
    billing.setFormat(InvoiceFormat.HTML);
    customer.setBilling(billing);
    assertThat(invoice.getShortFormatInLowerCase(), is("html"));
  }

  @Test
  public void isSignedDelivery() throws Exception {
    Billing billing = new Billing();
    billing.setDeliverSigned(true);
    customer.setBilling(billing);
    assertThat(invoice.isSignedDelivery(), is(true));
  }

  @Test
  public void getEmail() throws Exception {
    Contact contact = new Contact();
    contact.setEmail("a@b");
    customer.setContact(contact);
    assertThat(invoice.getEmail(), is("a@b"));
  }

  @Test
  public void getCopyToEmails() throws Exception {
    Billing billing = new Billing();
    billing.setDeliverCopyEmail("c@b, d@e");
    customer.setBilling(billing);
    assertThat(invoice.getCopyToEmails(), is(new String[]{"c@b", "d@e"}));
  }

  @Test
  public void getCopyToEmailsFromNull() throws Exception {
    assertThat(invoice.getCopyToEmails(), is(new String[]{}));
  }

  @Test
  public void getNumber() throws Exception {
    bill.setNumber("1234");
    assertThat(invoice.getNumber(), is("1234"));
  }

  @Test
  public void getPeriod() throws Exception {
    Period period = new Period();
    GregorianCalendar calendarFrom = new GregorianCalendar();
    GregorianCalendar calendarTo = new GregorianCalendar();
    calendarFrom.set(2010, 9, 1);
    calendarTo.set(2010, 9, 31);
    period.setFrom(calendarFrom.getTime());
    period.setTo(calendarTo.getTime());
    bill.setPeriod(period);
    assertThat(invoice.getPeriod(), is("01.10.2010-31.10.2010"));
  }

  @Test
  public void uuid() throws Exception {
    bill.setHashCode("abcdef");
    assertThat(invoice.getUuid(), is("abcdef"));
  }

  private void prepareInvoiceFixture() {
    bill = new Bill();
    customer = new Customer();
    invoice = new Invoice(bill, customer);
  }
}
