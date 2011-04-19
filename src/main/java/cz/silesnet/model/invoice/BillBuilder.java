package cz.silesnet.model.invoice;

import cz.silesnet.model.*;
import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Frequency;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: der3k
 * Date: 8.3.11
 * Time: 21:07
 */
public class BillBuilder {
  private static final int HASH_CODE_BASE = 1000000;
  private final Customer customer;
  private final Billing billing;
  private final Date due;
  private final Period billPeriod;
  private Period adjustedBillPeriod;
  private final List<String> errors = new ArrayList<String>();
  private final List<String> warnings = new ArrayList<String>();
  private final List<BillItem> items = new ArrayList<BillItem>();
  private Amount totalNet = Amount.ZERO;
  private final List<Service> billedOneTimeServices = new ArrayList<Service>();
  private boolean built;

  public BillBuilder(final Customer customer, final Date due) {
    failWhenNullArgumentEncountered(customer, due);
    this.customer = customer;
    this.billing = customer.getBilling();
    this.due = due;
    billPeriod = billing.nextBillPeriod(due);
    checkPreconditions();
    if (errors.size() == 0) {
      buildItems();
      ensureAdjustedBillPeriod();
      checkPostConditions();
    }
  }

  private void checkPreconditions() {
    if (!billing.getIsActive())
      errors.add("billing.customerNotActive");
    if (!billing.getStatus().equals(BillingStatus.INVOICE))
      errors.add("billing.billingDisabled");
    if (customer.getServices() == null || customer.getServices().size() == 0)
      errors.add("billing.customerHasNoServices");
    if (billPeriod.equals(Period.NONE))
      errors.add("billing.noBillForPeriod");
  }

  private void buildItems() {
    for (Service service : customer.getServices()) {
      Period billableServicePeriod = billablePeriodFor(service);
      if (billableServicePeriod.equals(Period.NONE)) {
        warnings.add("billing.serviceOutOfPeriodSkipped");
        continue;
      }
      BillItem item = buildItemFor(service, billableServicePeriod);
      if (item == null) {
        warnings.add("billing.zeroItemSkipped");
        continue;
      }
      totalNet = totalNet.plus(item.net());
      items.add(item);
    }
  }

  private void checkPostConditions() {
    if (items.size() == 0)
      errors.add("billing.noBillItems");
    if (!hasOneTimeItem() && totalNet.equals(Amount.ZERO))
      errors.add("billing.zeroBillWithoutOneTimeItem");
    if (totalNet.compareTo(Amount.ZERO) < 0)
      errors.add("billing.negativeAmountBill");
  }

  protected Period billablePeriodFor(Service service) {
    if (service.getFrequency().equals(Frequency.ONE_TIME))
      return billPeriod;
    Period intersection = service.getPeriod().intersection(billPeriod);
    return intersection != null ? intersection : Period.NONE;
  }

  protected BillItem buildItemFor(final Service service, final Period billableServicePeriod) {
    Frequency frequency = service.getFrequency();
    Percent percent = frequency.percentageFor(billableServicePeriod);
    String text = service.getBillItemText(customer.getContact().getAddress().getCountry());
    BillItem item = new BillItem(text, percent, Amount.of(service.getPrice()));
    if (Frequency.ONE_TIME.equals(frequency)) {
      billedOneTimeServices.add(service);
      item.setIsDisplayUnit(false);
    } else {
      if (Amount.ZERO.equals(item.net()))
        item = null;
      else
        updateAdjustedBillPeriod(billableServicePeriod);
    }
    return item;
  }

  public boolean wouldBuild() {
    return errors.size() == 0;
  }

  public Bill build(final Invoicing invoicing, final BillingContext context) {
    if (built)
      throw new IllegalStateException("can not build the bill twice");
    if (!wouldBuild())
      throw new IllegalStateException("builder is not buildable");
    Bill bill = new Bill();
    bill.setNumber("" + invoicing.nextBillNumber());
    bill.setHashCode(currentHashCode());
    bill.setInvoicedCustomer(customer);
    bill.setCustomerId(customer.getId());
    bill.setCustomerName(customer.getName());
    bill.setDeliverByMail(customer.getBilling().getDeliverByMail());
    bill.setBillingDate(due);
    bill.setPeriod(adjustedBillPeriod);
    bill.setVat(vatRateFromContext(context));
    bill.setPurgeDate(context.purgeDateFor(due));
    for (BillItem item : items) {
      item.setBill(bill);
      bill.getItems().add(item);
    }
    built = true;
    return bill;
  }

  public void updateBillingAndServicesOf(final Customer otherCustomer) {
    checkBuild();
    if (this.customer != otherCustomer)
      throw new IllegalArgumentException("billed customer differs from given customer");
    customer.getBilling().setLastlyBilled(adjustedBillPeriod.getTo());
    customer.getServices().removeAll(billedOneTimeServices);
  }

  public List<String> errors() {
    return errors;
  }

  public List<String> warnings() {
    return warnings;
  }

  private void updateAdjustedBillPeriod(final Period billableServicePeriod) {
    if (adjustedBillPeriod == null) {
      adjustedBillPeriod = billableServicePeriod.duplicate();
    } else {
      adjustedBillPeriod.unionThisPeriodWith(billableServicePeriod);
    }
  }

  private void ensureAdjustedBillPeriod() {
    if (adjustedBillPeriod == null)
      adjustedBillPeriod = billPeriod.duplicate();
  }

  private boolean hasOneTimeItem() {
    return billedOneTimeServices.size() != 0;
  }

  private int vatRateFromContext(final BillingContext context) {
    return context.calculateVatFor(Amount.HUNDRED).value().intValue();
  }

  private String currentHashCode() {
    return Long.toHexString(customer.getId() + HASH_CODE_BASE) + Long.toHexString(new Date().getTime());
  }

  private void failWhenNullArgumentEncountered(final Customer customer, final Date due) {
    throwWhenNull(customer, "customer must not be null");
    throwWhenNull(due, "due date must not be null");
  }

  private void throwWhenNull(final Object arg, final String message) {
    if (arg == null)
      throw new IllegalArgumentException(message);
  }

  private void checkBuild() {
    if (!built)
      throw new IllegalStateException("invoice was not yet built");
  }

}
