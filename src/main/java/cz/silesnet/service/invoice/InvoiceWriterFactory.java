package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;

import java.io.IOException;

/**
 * Factory delivering different types of invoice writers.
 *
 * @author Richard Sikora
 */
public interface InvoiceWriterFactory {
  InvoiceWriter instanceOf(Invoice invoice) throws IOException;
}
