package cz.silesnet.model.invoice;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Invoicing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: der3k
 * Date: 30.4.11
 * Time: 12:40
 */
public class Accountant {
  private final static BillingResult BILLING_ERRORS_RESULT = BillingResult.failure(billingErrors());
  private final Invoicing invoicing;
  private final BillingContext context;
  private long lastBillNumber;
  private int billedCount;
  private int skippedCount;
  private int errorsCount;


  public Accountant(final Invoicing invoicing, final BillingContext context) {
    this.invoicing = invoicing;
    this.context = context;
    lastBillNumber = Long.valueOf(invoicing.getNumberingBase());
  }

  public BillingResult bill(final Customer customer) {
    BillingResult result;
    try {
      result = doBill(customer);
      if (result.isSuccess())
        billedCount++;
      else
        skippedCount++;
    } catch (RuntimeException e) {
      result = BILLING_ERRORS_RESULT;
      errorsCount++;
    }
    return result;
  }

  private BillingResult doBill(final Customer customer) {
    BillBuilder builder = newBillBuilder(customer, invoicing.getInvoicingDate());
    if (builder.wouldBuild()) {
      Bill bill = builder.build(this);
      customer.updateBillingAndServicesAfterBilledWith(builder);
      return BillingResult.success(bill, customer, builder.warnings());
    }
    return BillingResult.failure(builder.errors());
  }

  protected BillBuilder newBillBuilder(final Customer customer, final Date due) {
    return new BillBuilder(customer, due);
  }

  private static List<String> billingErrors() {
    List<String> errors = new ArrayList<String>();
    errors.add("billing.error");
    return errors;
  }

  public String nextBillNumber() {
    lastBillNumber++;
    return "" + lastBillNumber;
  }

  public int processedCount() {
    return billedCount + skippedCount + errorsCount;
  }

  public int billedCount() {
    return billedCount;
  }

  public int skippedCount() {
    return skippedCount;
  }

  public int errorsCount() {
    return errorsCount;
  }

  public Invoicing invoicing() {
    return invoicing;
  }

  public BillingContext billingContext() {
    return context;
  }
}
