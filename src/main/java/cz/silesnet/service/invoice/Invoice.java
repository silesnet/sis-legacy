package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.enums.Country;

/**
 * User: der3k
 * Date: 25.10.2010
 * Time: 20:04:11
 */
public class Invoice {
  private final static String[] EMPTY_COPY_TO_EMAILS = new String[]{};

  final private Bill bill;
  final private Customer customer;


  public Invoice(final Bill bill, final Customer customer) {
    this.bill = bill;
    this.customer = customer;
    if (bill.getCustomerId() != customer.getId()) {
      throw new IllegalArgumentException("bill.customerId does not match the customer.id");
    }
  }

  public Country getCountry() {
    return customer.getContact().getAddress().getCountry();
  }

  public String getShortFormatInLowerCase() {
    return customer.getBilling().getFormat().shortName().toLowerCase();
  }

  public boolean isSignedDelivery() {
    return customer.getBilling().getDeliverSigned();
  }

  public String getEmail() {
    return customer.getContact().getEmail();
  }

  public String[] getCopyToEmails() {
    String copy = customer.getBilling().getDeliverCopyEmail();
    if (copy == null || copy.trim().length() == 0) {
      return EMPTY_COPY_TO_EMAILS;
    } else {
      return copy.split(",");
    }
  }

  public String getNumber() {
    return bill.getNumber();
  }

  public String getPeriod() {
    return bill.getPeriod().getPeriodString();
  }

  public String getUuid() {
    return bill.getHashCode();
  }

}
