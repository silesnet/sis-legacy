package cz.silesnet.model.invoice;

import java.util.Date;

/**
 * User: der3k
 * Date: 11.3.11
 * Time: 21:00
 */
public interface BillingContext {
  public Amount calculateVatFor(Amount amount);

  public Amount roundTotalOf(Amount amount);

  public Date purgeDateFor(Date date);
}
