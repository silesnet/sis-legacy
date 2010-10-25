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

  public String getNumber() {
    return bill.getNumber();
  }
}
