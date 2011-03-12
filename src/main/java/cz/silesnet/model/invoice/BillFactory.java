package cz.silesnet.model.invoice;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Invoicing;

/**
 * User: der3k
 * Date: 8.3.11
 * Time: 21:07
 */
public class BillFactory {
  private final Invoicing invoicing;
  private final BillingContext context;

  public BillFactory(final Invoicing invoicing, final BillingContext context) {
    this.invoicing = invoicing;
    this.context = context;
  }

  public Bill createBillFor(Customer customer) {
    return null;
  }

}
