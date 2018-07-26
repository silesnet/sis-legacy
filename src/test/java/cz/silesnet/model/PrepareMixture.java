package cz.silesnet.model;

import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Prepare domain objects used in tests.
 *
 * @author Richard Sikora
 */
public class PrepareMixture {

    private static int contract = 1;

  public static Label getCommissionLabel() {
    Label l = new Label();
    l.setName("Commission Test Label");
    return l;
  }

  public static Address getAddress() {
    Address a = new Address();
    a.setStreet("Street Test 129/23");
    a.setCity("City Test");
    a.setPostalCode("739 65");
    a.setCountry(Country.CZ);
    return a;
  }

  public static Contact getContact(String name) {
    Contact c = new Contact();
    c.setName(name);
    c.setAddress(getAddress());
    c.setEmail("email@test.cz");
    c.setPhone("732 569 458, 558 365 458");
    return c;
  }

  public static Contact getContact() {
    return getContact("Contact Test Name");
  }

  public static Billing getBilling() {
    Billing b = new Billing();
    b.setFrequency(Frequency.MONTHLY);
    b.setLastlyBilled(new Date());
    b.setIsBilledAfter(false);
    b.setDeliverByEmail(true);
    b.setDeliverByMail(false);
    b.setDeliverCopyEmail("copy@test.cz); copy2@test.cz");
    b.setIsActive(true);
    return b;
  }

  public static Service getService(String name) {
    Service s = new Service();
    s.setId(Long.valueOf(ServiceId.firstServiceId(Country.CZ, ContractNo.contractNo(contract++)).id()));
    s.setCustomerId(null);
    s.setFrequency(Frequency.MONTHLY);
    s.setName(name);
    Calendar cal = new GregorianCalendar(2005, Calendar.APRIL, 2);
    s.setPeriod(new Period(cal.getTime(), null));
    s.setPrice(946);
    s.setInfo("Service Test Info String");
    return s;
  }

  public static Service getService() {
    return getService("Service Test");
  }

  public static Customer getCustomerSimple(String name) {
    Customer c = new Customer();
    c.setHistoryId(Long.valueOf(0));
    c.setName(name);
    c.setSupplementaryName("Supplementary Test Name");
    c.setContact(getContact());
    c.setPublicId("123456789");
    c.setDIC("CZ123426789");
    c.setServices(null);
    c.setBilling(getBilling());
    c.setConnectionSpot("Connection Spot Test String");
    c.setInfo("Customer Info Test String");
    c.setInsertedOn(new Date());
    return c;
  }

  public static Customer getCustomerSimple() {
    return getCustomerSimple("Customer Test Name");
  }

  public static Customer getCustomer(String name) {
    Customer c = getCustomerSimple(name);
    c.setServices(new ArrayList<Service>());
    c.getServices().add(0, getService("Service Test 1"));
    c.getServices().add(1, getService("Service Test 2"));
    return c;
  }

  public static Customer getCustomer() {
    return getCustomer("Customer Test Name");
  }

  public static Bill getBillSimple() {
    Bill b = new Bill();
    b.setInvoicedCustomer(null);
    b.setPeriod(new Period(new Date(), new Date()));
//    b.setHashCode("hash1234123512351");
    b.setHashCode("" + System.nanoTime());
    final BillItem item1 = new BillItem("First Test Line", 1, 1234);
    item1.setBill(b);
    b.getItems().add(item1);
    final BillItem item2 = new BillItem("Second Test Line", 3, 234);
    item2.setBill(b);
    b.getItems().add(item2);
    return b;
  }

  public static Bill getBill() {
    Bill b = getBillSimple();
    b.setInvoicedCustomer(getCustomer());
    return b;
  }

  public static Setting getSetting() {
    return new Setting("SettingTestName", "Setting Test Value");
  }

  public static Setting getSetting2() {
    return new Setting("SettingTestName2", "Setting Test Value2");
  }

}
