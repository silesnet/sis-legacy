package cz.silesnet.model.invoice;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: der3k
 * Date: 30.4.11
 * Time: 16:52
 */
public class BillingResult {
  private static final List<String> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<String>());

  private final Bill bill;
  private final Customer customer;
  private final List<String> errors;
  private final List<String> warnings;
  private final boolean success;

  public static BillingResult success(final Bill bill, final Customer customer, final List<String> warnings) {
    return new BillingResult(bill, customer, warnings);
  }

  public static BillingResult failure(final List<String> errors) {
    return new BillingResult(errors);
  }

  private BillingResult(final Bill bill, final Customer customer, final List<String> warnings) {
    this.bill = bill;
    this.customer = customer;
    this.warnings = warnings;
    this.errors = EMPTY_LIST;
    this.success = true;
  }

  private BillingResult(final List<String> errors) {
    this.bill = null;
    this.customer = null;
    this.errors = errors;
    this.warnings = EMPTY_LIST;
    this.success = false;
  }

  public boolean isSuccess() {
    return success;
  }

  public Bill bill() {
    return bill;
  }

  public Customer customer() {
    return customer;
  }

  public List<String> warnings() {
    return warnings;
  }
  public List<String> errors() {
    return errors;
  }
}
