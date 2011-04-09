package cz.silesnet.model.invoice;

import cz.silesnet.model.*;
import cz.silesnet.model.enums.BillingStatus;
import cz.silesnet.model.enums.Frequency;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * User: der3k
 * Date: 8.3.11
 * Time: 21:07
 */
public class BillBuilder {
  private final Customer customer;
  private final Billing billing;
  private final Date due;
  private final Period billPeriod;
  private Period adjustedBillPeriod;
  private final List<String> errors = new ArrayList<String>();
  private final List<BillItem> items = new ArrayList<BillItem>();
  private Amount totalNet = Amount.ZERO;
  private final List<Service> billedOneTimeServices = new ArrayList<Service>();
  private boolean built;

  public BillBuilder(final Customer customer, final Date due) {
    if (customer == null)
      throw new IllegalArgumentException("customer must not be null");
    if (due == null)
      throw new IllegalArgumentException("due date must not be null");
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
      if (billableServicePeriod.equals(Period.NONE))
        continue;
      BillItem item = buildItemFor(service, billableServicePeriod);
      totalNet = totalNet.plus(item.net());
      items.add(item);
    }
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
    BillItem item = new BillItem(service.getName(), percent, Amount.of(service.getPrice()));
    if (frequency.equals(Frequency.ONE_TIME)) {
      billedOneTimeServices.add(service);
      item.setIsDisplayUnit(false);
    } else {
      updateAdjustedBillPeriod(billableServicePeriod);
    }
    return item;
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

  private void checkPostConditions() {
    if (items.size() == 0)
      errors.add("billing.noBillItems");
    if (!hasOneTimeItem() && totalNet.equals(Amount.ZERO))
      errors.add("billing.zeroBillWithoutOneTimeItem");
    if (totalNet.compareTo(Amount.ZERO) < 0)
      errors.add("billing.negativeAmountBill");
  }

  private boolean hasOneTimeItem() {
    return billedOneTimeServices.size() != 0;
  }

  public boolean wouldBuild() {
    return errors.size() == 0;
  }

  public List<String> errors() {
    return errors;
  }

  public Bill build(Invoicing invoicing, BillingContext context) {
    return null;
  }

  public void removeBilledOneTimeServices(Iterator<Service> services) {
    checkBuild();
  }

  public void updateLastlyBilled(Billing billing) {
    checkBuild();
  }

  private void checkBuild() {
    if (!built)
      throw new IllegalStateException("invoice was not yet built");
  }

}
