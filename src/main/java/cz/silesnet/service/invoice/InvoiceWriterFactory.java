package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;

/**
 * Factory delivering different types of invoice writers.
 *
 * @author Richard Sikora
 */
public interface InvoiceWriterFactory {
  InvoiceWriter instanceOf(Bill invoice, InvoiceFormat format);
}
