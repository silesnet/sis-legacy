package cz.silesnet.model.invoice;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import cz.silesnet.model.*;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.enums.Frequency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static cz.silesnet.model.enums.BillingStatus.INVOICE;


/**
 * User: der3k
 * Date: 8.3.11
 * Time: 21:07
 */
public class BillBuilder {
  private static final int HASH_CODE_BASE = 1000000;
  private long hashCodeSeq = 0;
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
    if (!billing.getStatus().equals(INVOICE)) {
      switch (billing.getStatus()) {
        case CEASE:
          errors.add("billing.invoicingCeased");
          break;
        case DEADHEAD:
          errors.add("billing.customerDeadhead");
          break;
        case CELL:
          errors.add("billing.customerHostsCell");
          break;
        case VIP:
          errors.add("billing.customerVip");
          break;
        case PROMOTION:
          errors.add("billing.customerPromotion");
          break;
        case EXPIRED:
          errors.add("billing.billingExpired");
          break;
        case JURIST:
          errors.add("billing.customerGivenToJurist");
          break;
        default:
          errors.add("billing.billingDisabled");
          break;
      }
    }
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
      return service.getPeriod().getFrom().getTime() <= billPeriod.getTo().getTime() ? billPeriod : Period.NONE;
    Period intersection = service.getPeriod().intersection(billPeriod);
    return intersection != null ? intersection : Period.NONE;
  }

  protected BillItem buildItemFor(final Service service, final Period billableServicePeriod) {
    Frequency frequency = service.getFrequency();
    Percent percent = frequency.percentageFor(billableServicePeriod);
    String text = service.getBillItemText(customersCountry());
    final BillItem item = new BillItem(text, percent, Amount.of(service.getPrice()));
    if (isZeroAmountAndNotOneTime(item, frequency))
      return null; // skip such item
    if (isOneTimeService(frequency))
      handleOneTime(item, service);
    else
      updateAdjustedBillPeriod(billableServicePeriod);
    item.setIncludeDph(service.getIncludeDph());
    item.setServiceId(service.getId());
    return item;
  }

  private boolean isZeroAmountAndNotOneTime(final BillItem item, final Frequency frequency) {
    return (isZeroAmount(item) && (!isOneTimeService(frequency)));
  }

  private Country customersCountry() {
    return customer.getContact().getAddress().getCountry();
  }

  private boolean isZeroAmount(final BillItem item) {
    return Amount.ZERO.equals(item.net());
  }

  private boolean isOneTimeService(final Frequency frequency) {
    return Frequency.ONE_TIME.equals(frequency);
  }

  private void handleOneTime(final BillItem item, final Service service) {
    billedOneTimeServices.add(service);
    item.setIsDisplayUnit(false);
  }

  public boolean wouldBuild() {
    return errors.size() == 0;
  }

  public Iterable<Bill> build(final Accountant accountant) {
    final List<Bill> bills = new ArrayList<>();
    if (built)
      throw new IllegalStateException("can not build the bill twice");
    if (!wouldBuild())
      throw new IllegalStateException("builder is not buildable");
    final Multimap<Boolean, BillItem> billsItems = Multimaps.index(items, new Function<BillItem, Boolean>() {
      @Override
      public Boolean apply(BillItem input) {
        return input.getIncludeDph();
      }
    });
    for (Boolean isIncludeVat : billsItems.keySet()) {
      final Collection<BillItem> billItems = billsItems.get(isIncludeVat);
      Bill bill = new Bill();
      assignNewNumberAndInvoicingReferenceTo(bill, accountant);
      assignCustomerReferencesTo(bill);
      addItemsTo(bill, billItems);
      assignDatesAndVatRateTo(bill, accountant.billingContext());
      bill.setHashCode(currentHashCode());
      bill.setIsConfirmed(true);
      bills.add(bill);
    }
    built = true;
    return bills;
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

  private void assignNewNumberAndInvoicingReferenceTo(final Bill bill, final Accountant accountant) {
    bill.setNumber(accountant.nextBillNumber());
    bill.setInvoicingId(accountant.invoicing().getId());
  }

  private void assignDatesAndVatRateTo(final Bill bill, final BillingContext context) {
    bill.setPeriod(adjustedBillPeriod);
    bill.setBillingDate(due);
    bill.setPurgeDate(context.purgeDateFor(due));
    bill.setVat(vatRateFromContext(context));
  }

  private void assignCustomerReferencesTo(final Bill bill) {
    bill.setCustomerId(customer.getId());
    bill.setCustomerName(customer.getName());
    bill.setInvoicedCustomer(customer);
    bill.setDeliverByMail(customer.getBilling().getDeliverByMail());
  }

  private void addItemsTo(final Bill bill, Iterable<BillItem> billItems) {
    for (BillItem item : billItems) {
      item.setBill(bill);
      bill.getItems().add(item);
    }
  }

  private boolean hasOneTimeItem() {
    return billedOneTimeServices.size() != 0;
  }

  private int vatRateFromContext(final BillingContext context) {
    return context.calculateVatFor(Amount.HUNDRED).value().intValue();
  }

  private String currentHashCode() {
    return Long.toHexString(customer.getId() + HASH_CODE_BASE) + Long.toHexString(new Date().getTime() + hashCodeSeq++);
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
