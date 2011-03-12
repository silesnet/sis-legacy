package cz.silesnet.model.invoice;

import cz.silesnet.model.Invoicing;
import cz.silesnet.model.enums.Country;
import cz.silesnet.model.invoice.BillingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * User: der3k
 * Date: 11.3.11
 * Time: 20:58
 */
public class BillingContextFactory {
  private final Map<Country, BillingContext> contexts = new HashMap<Country, BillingContext>();

  public void add(Country country, BillingContext context) {
    contexts.put(country, context);
  }

  public BillingContext billingContextFor(Invoicing invoicing) {
    return contexts.get(invoicing.getCountry());
  }
}
