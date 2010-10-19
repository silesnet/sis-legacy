package cz.silesnet.service.invoice;

import cz.silesnet.model.Bill;

import javax.mail.internet.MimeBodyPart;
import java.io.PrintWriter;

/**
 * Invoice writer generating invoice in HTML format.
 *
 * @author Richard Sikora
 */
public class HtmlInvoiceWriter extends AbstractInvoiceWriter {

  public HtmlInvoiceWriter(Bill bill) {
    super(bill);
  }


  public void writeTo(PrintWriter writer) {
    // TODO Auto-generated method stub
  }
}
