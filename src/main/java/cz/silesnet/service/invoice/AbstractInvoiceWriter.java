package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;

import javax.mail.internet.MimeBodyPart;
import java.io.PrintWriter;

/**
 * Abstract invoice writer containing common invoice writer functionality.
 *
 * @author Richard Sikora
 */
public abstract class AbstractInvoiceWriter implements InvoiceWriter {

  protected Bill bill;

  public AbstractInvoiceWriter(Bill bill) {
    this.bill = bill;
  }

  public abstract MimeBodyPart getMimeBodyPart();

  public abstract void writeTo(PrintWriter writer);

}
