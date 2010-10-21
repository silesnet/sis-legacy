package cz.silesnet.service.invoice.impl;

import cz.silesnet.model.Bill;
import cz.silesnet.service.invoice.InvoiceWriter;

import javax.mail.internet.MimeBodyPart;
import java.io.PrintWriter;

/**
 * Abstract invoice writer containing common invoice writer functionality.
 *
 * @author Richard Sikora
 */
public abstract class AbstractInvoiceWriter implements InvoiceWriter {

  protected Bill bill;

  protected AbstractInvoiceWriter(Bill bill) {
    this.bill = bill;
  }

  public abstract void write(PrintWriter writer);

}
