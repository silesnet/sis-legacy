package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;

/**
 * User: der3k
 * Date: 19.10.2010
 * Time: 20:36:24
 */
public abstract class FreeMarkerInvoiceWriter extends AbstractInvoiceWriter {

  protected FreeMarkerInvoiceWriter(Bill bill) {
    super(bill);
  }
  
}
