package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;

/**
 * Factory delivering different types of invoice writers.
 *
 * @author Richard Sikora
 */
public class InvoiceWriterFactory {

  private InvoiceWriterFactory() {
  }

  public static InvoiceWriter instanceOf(Bill invoice, InvoiceFormat format) {
    if (InvoiceFormat.LINK.equals(format))
      return new LinkInvoiceWriter(invoice);
    if (InvoiceFormat.HTML.equals(format))
      return new HtmlInvoiceWriter(invoice);
    if (InvoiceFormat.PDF.equals(format))
      return new PdfInvoiceWriter(invoice);
    return null;
  }

}
